package idevgame.meteor.net;

import idevgame.meteor.net.PackCodec.Pack;
import io.netty.channel.ChannelHandlerContext;

public interface IHandlerNetty4 {
	void onConnect(ChannelHandlerContext ctx);

	void onReceive(ChannelHandlerContext ctx, Pack pak);

	void onClose(ChannelHandlerContext ctx);

	void onException(ChannelHandlerContext ctx,Throwable e);
}
