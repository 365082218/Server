package idevgame.meteor.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import idevgame.meteor.utils.codec.LittleEndianCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * PackCodec
 *
 * @author moon
 * @version 2.0 - 2014-05-30
 */
public class PackCodec {
	private static final int LEN = 4;

	public static Pack decode(byte[] data){
		int cmd = 0;
		cmd = LittleEndianCodec.readInt(data, 0);
		byte[] newdata = Arrays.copyOfRange(data, LEN, data.length);
		return new Pack(cmd, newdata);
	}

	//鍖呭惈澶撮儴闀垮害鐨�
	public static ByteBuf encodeEx(int cmd, byte[] data)
	{
		int l = data == null ? 2 * LEN : data.length + 2 * LEN;
		byte[] res = new byte[l];
		byte[] command = new byte[4];
		byte[] length = new byte[4];
		if (data != null)
			System.arraycopy(data, 0, res, 2 * LEN, data.length);
		ByteBuffer bb = ByteBuffer.wrap(command);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.asIntBuffer().put(cmd);
		System.arraycopy(command, 0, res, 4, 4);
		bb = ByteBuffer.wrap(length);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.asIntBuffer().put(l);
		System.arraycopy(length, 0, res, 0, 4);
		ByteBuf b = PooledByteBufAllocator.DEFAULT.buffer(l);
		b.writeBytes(res);
		return b;
	}
	
	public static byte[] encode(int cmd, byte[] data) {
		int l = data == null ? LEN : data.length + LEN;
		byte[] res = new byte[l];
		byte[] command = new byte[4];
		if (data != null)
			System.arraycopy(data, 0, res, 4, data.length);
		ByteBuffer bb = ByteBuffer.wrap(command);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.asIntBuffer().put(cmd);
		System.arraycopy(command, 0, res, 0, 4);
		return res;
	}

	public static byte[] encode(Pack pack) {
		return encode(pack.cmd, pack.data);
	}

	public static final class Pack{
		public int length;
		public final int cmd;
		public final byte[] data;

		public Pack(int cmd, byte[] data) {
			this.cmd = cmd;
			this.data = data;
			this.length = getDataLength() + 4;
		}
		
		public int getDataLength(){
			return data == null?0:data.length;
		}
	}

}
