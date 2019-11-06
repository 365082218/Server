package idevgame.meteor.gameserver.jkcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author beykery
 */
public class KcpThread extends Thread {

    private final Output out;
    private final LinkedBlockingQueue<DatagramPacket> inputs;
    private volatile boolean running;
    private final Map<InetSocketAddress, KcpOnUdp> kcps;
    private final KcpListerner listerner;
    private int nodelay;
    private int interval = Kcp.IKCP_INTERVAL;
    private int resend;
    private int nc;
    private int sndwnd = Kcp.IKCP_WND_SND;
    private int rcvwnd = Kcp.IKCP_WND_RCV;
    private int mtu = Kcp.IKCP_MTU_DEF;
    private boolean stream;
    private int minRto = Kcp.IKCP_RTO_MIN;
    private long timeout;//idle
    private final Object lock;//閿�
    private final InetSocketAddress local;//鏈湴鍦板潃

    /**
     * fastest: ikcp_nodelay(kcp, 1, 20, 2, 1) nodelay: 0:disable(default),
     * 1:enable interval: internal update timer interval in millisec, default is
     * 100ms resend: 0:disable fast resend(default), 1:enable fast resend nc:
     * 0:normal congestion control(default), 1:disable congestion control
     *
     * @param nodelay
     * @param interval
     * @param resend
     * @param nc
     */
    public void noDelay(int nodelay, int interval, int resend, int nc) {
        this.nodelay = nodelay;
        this.interval = interval;
        this.resend = resend;
        this.nc = nc;
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.sndwnd = sndwnd;
        this.rcvwnd = rcvwnd;
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    /**
     * kcp宸ヤ綔绾跨▼
     *
     * @param out
     * @param listerner
     * @param local
     */
    public KcpThread(Output out, KcpListerner listerner, InetSocketAddress local) {
        this.out = out;
        this.listerner = listerner;
        inputs = new LinkedBlockingQueue<>();
        kcps = new HashMap<>();
        this.lock = new Object();
        this.local = local;
    }

    /**
     * 寮�鍚嚎绋�
     */
    @Override
    public synchronized void start() {
        if (!this.running) {
            this.running = true;
            super.start();
        }
    }

    /**
     * 鍏抽棴绾跨▼
     */
    public void close() {
        this.running = false;
    }

    @Override
    public void run() {
        while (this.running) {
            long st = System.currentTimeMillis();
            //input
            while (!this.inputs.isEmpty()) {
                DatagramPacket dp = this.inputs.remove();
                KcpOnUdp ku = this.kcps.get(dp.sender());
                ByteBuf content = dp.content();
                if (ku == null) {
                    ku = new KcpOnUdp(this.out, dp.sender(), local, this.listerner);//鍒濆鍖�
                    ku.noDelay(nodelay, interval, resend, nc);
                    ku.wndSize(sndwnd, rcvwnd);
                    ku.setMtu(mtu);
                    // conv搴旇鍦ㄥ鎴风绗竴娆″缓绔嬫椂鑾峰彇
                    int conv = content.getIntLE(0);
                    ku.setConv(conv);
                    ku.setMinRto(minRto);
                    ku.setStream(stream);
                    ku.setTimeout(timeout);
                    this.kcps.put(dp.sender(), ku);
                }
                ku.input(content);
            }
            //update
            KcpOnUdp temp = null;
            for (KcpOnUdp ku : this.kcps.values()) {
                if (ku.isClosed()) {
                    temp = ku;
                } else {
                    ku.update();
                }
            }
            if (temp != null)//鍒犳帀杩囨椂鐨刱cp
            {
                this.kcps.remove((InetSocketAddress) temp.getKcp().getUser());
            }
            if (inputs.isEmpty())//濡傛灉杈撳叆涓虹┖鍒欒�冭檻wait
            {
                long end = System.currentTimeMillis();
                if (end - st < this.interval) {
                    synchronized (this.lock) {
                        try {
                            lock.wait(interval - end + st);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
        release();
    }

    /**
     * 鏀跺埌杈撳叆
     */
    void input(DatagramPacket dp) {
        if (this.running) {
            this.inputs.add(dp);
            synchronized (this.lock) {
                lock.notify();
            }
        } else {
            dp.release();
        }
    }

    /**
     * stream mode
     *
     * @param stream
     */
    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public boolean isStream() {
        return stream;
    }

    public void setMinRto(int minRto) {
        this.minRto = minRto;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * 閲婃斁鎵�鏈夊唴瀛�
     */
    private void release() {
        for (DatagramPacket dp : this.inputs) {
            dp.release();
        }
        this.inputs.clear();
        for (KcpOnUdp ku : this.kcps.values()) {
            if (!ku.isClosed()) {
                ku.release();
            }
        }
        this.kcps.clear();
    }

}
