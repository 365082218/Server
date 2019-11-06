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
     * kcp寮傚父锛屼箣鍚庢kcp灏变細琚叧闂�
     *
     * @param ex 寮傚父
     * @param kcp 鍙戠敓寮傚父鐨刱cp锛宯ull琛ㄧず闈瀔cp閿欒
     */
    public void handleException(Throwable ex, KcpOnUdp kcp);

    /**
     * 鍏抽棴
     *
     * @param kcp
     */
    public void handleClose(KcpOnUdp kcp);
}
