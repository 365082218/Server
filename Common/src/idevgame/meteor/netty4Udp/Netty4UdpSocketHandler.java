package idevgame.meteor.netty4Udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import idevgame.meteor.net.IHandlerNetty4Udp;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public final class Netty4UdpSocketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private IHandlerNetty4Udp handler;

	public Netty4UdpSocketHandler(IHandlerNetty4Udp handler) {
		this.handler = handler;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)	throws Exception {
		doRead(ctx,msg);
	}

	protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		doRead(ctx,msg);
	}
	
	int lastM = 0;
	int inCount = 0;
	
	private void doRead(ChannelHandlerContext ctx, DatagramPacket msg) {
		try {
			
			
			ByteBuf in = msg.content();
			if(in.readableBytes() < 4){
				System.out.println("收到异常消息:in.readableBytes()=" + in.readableBytes());
				return;
			}

			final byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			
			handler.onReceive(ctx, msg,bytes);
			

	    } finally {

	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}