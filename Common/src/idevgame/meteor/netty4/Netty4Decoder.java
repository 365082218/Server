package idevgame.meteor.netty4;

import java.util.List;

import idevgame.meteor.net.PackCodec.Pack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class Netty4Decoder extends ByteToMessageDecoder{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// Check if there are at least 4 bytes readable
		int leftBytes = in.readableBytes();
		while (leftBytes >= 4)
		{
			int packetLength = in.getInt(0);
//			System.out.println("包长:" + packetLength);
			if (leftBytes >= packetLength)
			{
				int length = in.readInt();
//				System.out.println("包长:" + length);
				int message = in.readInt();
//				System.out.println("消息号:" + message);
				//已经读取了8字节，剩余身体长度
				int body = packetLength - 8;
				byte [] buff = new byte[body];
				if (body != 0)
					in.readBytes(buff);
				Pack p = new Pack(message, buff);
				out.add(p);
				leftBytes -= packetLength;
			}
			else
			{
				break;
			}
		}
	}
}
