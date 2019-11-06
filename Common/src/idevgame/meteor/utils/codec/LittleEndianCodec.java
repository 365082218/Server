package idevgame.meteor.utils.codec;


public class LittleEndianCodec {

	public static int readInt(byte[] bytes, int offset){
		int it = offset;
		return (bytes[it++] & 0xFF)
				+ ((bytes[it++] & 0xFF) << 8)
				+ ((bytes[it++] & 0xFF) << 16)
				+ ((bytes[it++] & 0xFF) << 24);
	}

	public static void writeInt(byte[] bytes, int offset, int value) {
		int it = offset;
		bytes[it++] = (byte) (value & 0xFF);
		bytes[it++] = (byte) ((value >>> 8) & 0xFF);
		bytes[it++] = (byte) ((value >>> 16) & 0xFF);
		bytes[it++] = (byte) ((value >>> 24) & 0xFF);
	}

}
