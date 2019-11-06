package idevgame.meteor.net;

import io.netty.channel.ChannelHandlerContext;

/**
 * IHandler
 *
 * @author moon
 * @version 2.0 - 2014-05-30
 */
public interface IHandler {

	void onConnect(ChannelHandlerContext ctx);

	void onReceive(ChannelHandlerContext ctx, byte[] bytes);

	void onClose(ChannelHandlerContext ctx);

	void onException(ChannelHandlerContext ctx,Throwable e);

}
