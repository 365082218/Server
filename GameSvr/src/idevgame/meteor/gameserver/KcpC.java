package idevgame.meteor.gameserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ResourceLeakDetector;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import com.google.protobuf.ByteString;

import idevgame.meteor.gameserver.jkcp.Kcp;
import idevgame.meteor.gameserver.jkcp.KcpClient;
import idevgame.meteor.gameserver.jkcp.KcpOnUdp;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.FrameCommand;
import idevgame.meteor.proto.MeteorMsgs.GameFrames;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;

/**
 * @author beykery
 */
public class KcpC extends KcpClient {

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        String content = bb.toString(Charset.forName("utf-8"));
        System.out.println("conv:" + kcp.getKcp().getConv() + " recv:" + content + " kcp-->" + kcp);
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(2048);
        buf.writeBytes(content.getBytes(Charset.forName("utf-8")));
        kcp.send(buf);
        bb.release();
    }


    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        System.out.println(ex);
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        super.handleClose(kcp);
        System.out.println("鏈嶅姟鍣ㄧ寮�:" + kcp);
        System.out.println("waitSnd:" + kcp.getKcp().waitSnd());
    }

    @Override
    public void out(ByteBuf msg, Kcp kcp, Object user) {
        super.out(msg, kcp, user);
    }

    /**
     * tcpdump udp port 2225 -x -vv -s0 -w 1112.pcap
     *
     * @param args
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        KcpC tc = new KcpC();
        tc.noDelay(1, 20, 2, 1);
        tc.setMinRto(10);
        tc.wndSize(32, 32);
        tc.setTimeout(10 * 1000);
        tc.setMtu(512);
        // tc.setConv(121106);//榛樿conv闅忔満

        tc.connect(new InetSocketAddress("localhost", 2222));
        tc.setConv(1);
        tc.start();
        String content = "测试KCP协议";
        GameFrames.Builder gb = GameFrames.newBuilder();
        FrameCommand.Builder fb = gb.addCommandsBuilder();
        fb.setCommand(MeteorMsg.Command.SpawnPlayer);
        fb.setLogicFrame(1);
        fb.setPlayerId(1);
        
        byte[] bytes = new byte[128];
        ByteString sss = ByteString.copyFrom(bytes);
        fb.setData(sss);
        GameFrames f = gb.build();
        ByteBuf bb = PackCodec.encodeEx(MeteorMsg.MsgType.SyncCommand_VALUE, f.toByteArray());
        tc.send(bb);
    }
}
