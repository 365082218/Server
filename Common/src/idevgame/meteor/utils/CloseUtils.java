package idevgame.meteor.utils;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 閸欘垰鍙ч梻顓烆嚠鐠炩�冲彠闂傤厼浼愰崗锟�
 * @author moon
 *
 */
public class CloseUtils {
	private static final Logger logger = LoggerFactory.getLogger(CloseUtils.class);
	
	/**閸忔娊妫�*/
	public static void close(Object o) {
		if (o!=null) {
			try {
				if (o instanceof Closeable) {
					Closeable closer = (Closeable) o;
					closer.close();
				}
				// TODO add other closer when you meet
			} catch (Exception e) {
				logger.error("closer exception!", e);
			}
		}
	}
}
