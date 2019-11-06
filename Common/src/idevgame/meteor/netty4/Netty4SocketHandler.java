package idevgame.meteor.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import idevgame.meteor.net.IHandlerNetty4;
import idevgame.meteor.net.PackCodec.Pack;

@Sharable
public final class Netty4SocketHandler extends SimpleChannelInboundHandler<Object> {

	private IHandlerNetty4 handler;

	public Netty4SocketHandler(IHandlerNetty4 handler) {
		this.handler = handler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channelActive:建立链接");
		handler.onConnect(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channelInactive:链接断开");
		handler.onClose(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		doRead(ctx,msg);
	}
	
//	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		doRead(ctx,msg);
	}
	
	private void doRead(ChannelHandlerContext ctx, Object msg) {
		try {
			handler.onReceive(ctx, (Pack)msg);
	    } finally {
	    }
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		handler.onException(ctx, cause);
	}

}