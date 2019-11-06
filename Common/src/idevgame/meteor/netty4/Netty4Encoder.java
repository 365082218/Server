package idevgame.meteor.netty4;

import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class Netty4Encoder extends MessageToByteEncoder<Pack>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Pack pak, ByteBuf out)
			throws Exception {
		out.writeBytes(PackCodec.encode(pak));
	}
	
}
