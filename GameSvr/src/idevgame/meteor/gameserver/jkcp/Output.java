package idevgame.meteor.gameserver.jkcp;

import io.netty.buffer.ByteBuf;

/**
 * @author beykery
 */
public interface Output {

    /**
     * kcp鐨勫簳灞傝緭鍑�
     *
     * @param msg  娑堟伅
     * @param kcp  kcp瀵硅薄
     * @param user 杩滅鍦板潃
     */
    void out(ByteBuf msg, Kcp kcp, Object user);
}
