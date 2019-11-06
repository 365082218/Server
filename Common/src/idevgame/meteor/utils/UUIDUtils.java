package idevgame.meteor.utils;

import org.apache.commons.codec.binary.Base64;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UUID瀹搞儱鍙�
 *
 * @author moon
 */
public class UUIDUtils {
	// 闂呭繑婧�娴溠呮晸閹村潡妫縤d
	public static String GeneralRoomId() 
	{
		String roomId = "";
		for(int i = 0; i < 6; ++i){
			roomId += (int)Math.floor(Math.random()*10);
		}
		return roomId;
	}
	
	/**
	 * 閼惧嘲褰囨稉锟芥稉鐚絬id娑擄拷
	 *
	 * @return 鐎瑰本鏆ｉ惃鍓坲id娑擄拷
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 閼惧嘲褰囨稉锟芥稉顏嗙叚uuid,娴ｈ法鏁ase64閸樺缂�
	 *
	 * @return 閸樺缂夐崥搴ｆ畱uuid娑擄拷
	 */
	public static String shortUuid() {
		UUID uuid = UUID.randomUUID();
		return compressedUUID(uuid);
	}

	/**
	 * unrecommended 閸樺缂塽uid娑擄拷
	 *
	 * @param uuidString
	 * @return 閸樺缂夐崥搴ｆ畱uuid娑擄拷
	 */
	public static String compress(String uuidString) {
		UUID uuid = UUID.fromString(uuidString);
		return compressedUUID(uuid);
	}

	/**
	 * unrecommended 鐟欙絽甯囩紓锕梪id娑擄拷
	 *
	 * @param compressedUuid
	 * @return 鐎瑰本鏆ｉ惃鍓坲id娑擄拷
	 */
	public static String uncompress(String compressedUuid) {
		if (compressedUuid.length() != 22) {
			throw new IllegalArgumentException("Invalid uuid!");
		}
		byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
		long most = bytes2long(byUuid, 0);
		long least = bytes2long(byUuid, 8);
		UUID uuid = new UUID(most, least);
		return uuid.toString();
	}

	private static String compressedUUID(UUID uuid) {
		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
		return compressUUID;
	}

	private static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}

	private static long bytes2long(byte[] bytes, int offset) {
		long value = 0;
		for (int i = 7; i > -1; i--) {
			value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
		}
		return value;
	}


	/**
	 * twitter distributed uuid implementation <a href="https://github.com/twitter/snowflake">see</a>
	 * <p/>
	 * +-------------------------+------------------+-----------------------+ <br>
	 * | 41bit millis timestamp  | 6bit mechine id  | 17bit sequence number | <br>
	 * +-------------------------+------------------+-----------------------+ <br>
	 */
	public static class Snowflake {
		private final int mechineId;

		private int sequence = 0;
		private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
		private volatile int size = 0;
		private final AtomicBoolean filling = new AtomicBoolean(false);

		/**
		 * GMT 2010-01-01 00:00:00
		 */
		private final static long Y2010 = 1259539200000L;

		private final static int MECHINE_ID_BITS = 6;
		private final static int SEQUENCE_BITS = 17;
		private final static int SEQUENCE_MASK = -1 ^ (-1 << SEQUENCE_BITS);

		private final static int MECHINE_ID_SHIFT = SEQUENCE_BITS;
		private final static int TIMESTAMP_SHIFT = SEQUENCE_BITS + MECHINE_ID_BITS;

		private static Snowflake ins;

		/**
		 * @param mechineId must < 64
		 */
		public Snowflake(int mechineId) {
			if (mechineId >= 64) {
				throw new RuntimeException("mechineId must < 64");
			}

			this.mechineId = mechineId << MECHINE_ID_SHIFT;
			ins = this;
			fill();
		}

		/**
		 * must ensure Snowflake(mechineId) is called
		 *
		 * @return 64bit uuid
		 */
		public static Long get() {
			Long res = ins.queue.poll();
			--ins.size;
			if (ins.size < 1000 && !ins.filling.get()) {
				ins.fill();
			}

			return res;
		}

		private void fill() {
			if (size < 1000 && filling.compareAndSet(false, true)) {

				ThreadPool.getPool().execute(new Runnable() {

					private long lastTimestamp = System.currentTimeMillis();
					private long t = lastTimestamp - Y2010;

					@Override
					public void run() {
						for(int i=0; i < 10000; ++i) {
							sequence = (sequence + 1) & SEQUENCE_MASK;
							if (sequence == 0) {
								long time = System.currentTimeMillis();
								while (time <= lastTimestamp) {
									time = System.currentTimeMillis();
								}
								lastTimestamp = time;
								t = lastTimestamp - Y2010;
							}

							long res = 0;
							res += t << TIMESTAMP_SHIFT;
							res += mechineId;

							res += sequence;
							queue.add(res);
						}
						size = queue.size();
						filling.set(false);
					}
				});
			}
		}
	}

}
