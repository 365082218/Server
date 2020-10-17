package idevgame.meteor.gameserver.jkcp;

import io.netty.buffer.ByteBuf;

/**
 *
 * @author beykery
 */
public interface KcpListerner {

    /**
     * kcp message
     *
     * @param bb the data
     * @param kcp
     */
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp);

    /**
     *
     * kcp瀵倸鐖堕敍灞肩閸氬孩顒漦cp鐏忓彉绱扮悮顐㈠彠闂傦拷
     *
     * @param ex 瀵倸鐖�
     * @param kcp 閸欐垹鏁撳鍌氱埗閻ㄥ埍cp閿涘ull鐞涖劎銇氶棃鐎攃p闁挎瑨顕�
     */
    public void handleException(Throwable ex, KcpOnUdp kcp);

    /**
     * 閸忔娊妫�
     *
     * @param kcp
     */
    public void handleClose(KcpOnUdp kcp, int errorCode);
}
