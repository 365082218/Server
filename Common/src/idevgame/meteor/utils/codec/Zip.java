package idevgame.meteor.utils.codec;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.utils.CloseUtils;


public class Zip {
	
	private static final Logger logger = LoggerFactory.getLogger(Zip.class);
	private static final int BUFF_LEN = 65536;
	
	 /**
	  * 閸樺缂�
	  * 
	  * @param data 瀵板懎甯囩紓鈺傛殶閹癸拷
	  * @return return byte[] after zip or null if zip failed
	  */
	public static byte[] zip(byte[] data){
		byte[] output = null;

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[BUFF_LEN];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			logger.error("zip error", e);
		} finally {
			CloseUtils.close(bos);
		}
		compresser.end();
		return output;
	 }
	 
	 /**
	  * 鐟欙絽甯囩紓锟� 
	  * @param data 瀵板懓袙閸樺鏆熼幑锟�
	  * @return return byte[] after unzip or null if unzip failed
	  */
	public static byte[] unzip(byte[] data) {
		byte[] output = null;

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[BUFF_LEN];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			logger.error("zip error", e);
		} finally {
			CloseUtils.close(o);
		}

		decompresser.end();
		return output;
	}
}
