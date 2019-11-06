package idevgame.meteor.utils;

import java.text.ParseException;

/**
 * twitter distributed uuid implementation <a href="https://github.com/twitter/snowflake">see</a>
 * <p/>
 * +-------------------------+------------------+-----------------------+ <br>
 * | 40bit millis timestamp  | 11bit mechine id  | 12bit sequence number | <br>
 * +-------------------------+------------------+-----------------------+ <br>
 * 34楠烇拷-2048(0-2047)娑擃亜鐤勬笟锟�-濮ｅ繑顕犵粔鎺戝讲娴狅拷4096(0-4095)娑撶嫪D
 */
public class GUID {
	private final long mechineId;
	
	/**
	 * GMT 2015-10-21 00:00:00
	 */
	private final static long BEGINTIME = 1445356800000L;
	
	private long sequence = 0L;
	private final static long MECHINE_ID_BITS = 11L;
	public final static long MAX_MECHINE_ID = -1 ^ (-1 << MECHINE_ID_BITS);
	private final static long SEQUENCE_BITS = 12L;
	public final static long SEQUENCE_MASK = -1 ^ -1 << SEQUENCE_BITS;

	private final static long MECHINE_ID_SHIFT = SEQUENCE_BITS;
	private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + MECHINE_ID_BITS;
	
	private long lastTimestamp = -1L;
	
	private static GUID ins;

	public GUID(long mechineId) {
		super();
		if (mechineId > MAX_MECHINE_ID || mechineId < 0) {
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",MAX_MECHINE_ID));
		}
		this.mechineId = mechineId;
		
		ins = this;
	}
	
	public static long get() {
		return ins.nextId();
	}

	public synchronized long nextId() {
		long timestamp = timeGen();
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & SEQUENCE_MASK;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0;
		}
		if (timestamp < lastTimestamp) {
			try {
				throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		lastTimestamp = timestamp;
		long nextId = ((timestamp - BEGINTIME << TIMESTAMP_SHIFT)) | (mechineId << MECHINE_ID_SHIFT) | (sequence);
		return nextId;
	}

	private static long tilNextMillis(final long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	private static long timeGen() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) throws ParseException {
//		new GUID(1);
//		System.out.println(GUID.get());
		
		System.err.println("閺堝搫娅�: " + (-1 ^ (-1 << MECHINE_ID_BITS)));
		System.err.println("sq: " + (-1 ^ -1 << SEQUENCE_BITS));
	}

}
