package idevgame.meteor.utils;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	public static void main(String[] args) {
//		String content1 = "AEStest-content";
//		String content = "AEStest-content";
//		for (int i = 0; i < 40; i++) {
//			content = content + content1;
//		}
//		String password = "AEStest-password";
//		System.out.println("濞村鐦疉ES閸旂姴鐦戦敍锟�");
//		System.out.println("鐎靛棎锟斤拷闁姐儻绱�" + password);
//		System.out.println("閸旂姴鐦戦崜宥忕窗" + content);
//		System.out.println("閸旂姴鐦戦崜宥夋毐鎼达拷:" + content.length());
//		// 閸旂姴鐦�
//		String encryptResult = encryptString(content, password);
//		System.out.println("閸旂姴鐦戦崥搴窗" + encryptResult);
//		System.out.println("閸旂姴鐦戦崥搴ㄦ毐鎼达拷:" + encryptResult.length());
//		// 鐟欙絽鐦�
//		String decryptResult = decryptString(encryptResult, password);
//		System.out.println("鐟欙絽鐦戦崥搴窗" + decryptResult);
		
		byte[] content = {0,1,2,3,4,5,6,7,8,9};
		String password = "AEStest-password";
		System.out.println("濞村鐦疉ES閸旂姴鐦戦敍锟�");
		System.out.println("鐎靛棎锟斤拷闁姐儻绱�" + password);
		System.out.println("閸旂姴鐦戦崜宥忕窗" + byteToString(content));
		System.out.println("閸旂姴鐦戦崜宥夋毐鎼达拷:" + content.length);
		// 閸旂姴鐦�
		byte[] encryptResult = encryptBytes(content, password);
		System.out.println("閸旂姴鐦戦崥搴窗" + byteToString(encryptResult));
		System.out.println("閸旂姴鐦戦崥搴ㄦ毐鎼达拷:" + encryptResult.length);
		// 鐟欙絽鐦�
		byte[] decryptResult = decryptBytes(encryptResult, password);
		System.out.println("鐟欙絽鐦戦崥搴窗" + byteToString(decryptResult));
	}

	private static String byteToString(byte[] decryptResult) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < decryptResult.length; i++) {
			sb.append(decryptResult[i]);
			if(i<decryptResult.length - 1){
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * 鐎涙顑佹稉鎻掑鐎碉拷
	 * @param content 瀵板懎濮炵�靛棗鍞寸�癸拷
	 * @param key 閸旂姴鐦戦惃鍕槕闁斤拷
	 * @return 鏉烆剚宕查幋锟�16鏉╂稑鍩楃悰銊с仛閻ㄥ嫬鐡х粭锔胯
	 */
	public static String encryptString(String content, String key) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] byteRresult = cipher.doFinal(byteContent);
			System.out.println(byteContent.length + "-->" + byteRresult.length);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteRresult.length; i++) {
				String hex = Integer.toHexString(byteRresult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append(hex.toUpperCase());
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * byte閺佹壆绮嶉崝鐘茬槕
	 * @param byteContent
	 * @param key
	 * @return
	 */
	public static byte[] encryptBytes(byte[] byteContent, String key) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] byteRresult = cipher.doFinal(byteContent);
			return byteRresult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 鐎涙顑佹稉鑼缎掔�碉拷
	 * @param content 瀵板懓袙鐎靛棗鍞寸�癸拷,16鏉╂稑鍩楃悰銊с仛閻ㄥ嫬鐡х粭锔胯
	 * @param key 鐟欙絽鐦戦惃鍕槕闁斤拷
	 * @return 
	 */
	public static String decryptString(String content, String key) {
		if (content.length() < 1)
			return null;
		byte[] byteRresult = new byte[content.length() / 2];
		for (int i = 0; i < content.length() / 2; i++) {
			int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2), 16);
			byteRresult[i] = (byte) (high * 16 + low);
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(byteRresult);
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] decryptBytes(byte[] byteRresult, String key) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(byteRresult);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
