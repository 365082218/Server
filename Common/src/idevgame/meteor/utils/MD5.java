package idevgame.meteor.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;

@Entity
public class MD5 {
	public static String toMD5(String inStr) {

		StringBuffer sb = new StringBuffer();

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");

			try {
				md.update(inStr.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte b[] = md.digest();

			int i;

			for (int offset = 0; offset < b.length; offset++) {

				i = b[offset];

				if (i < 0)

					i += 256;

				if (i < 16)

					sb.append("0");

				sb.append(Integer.toHexString(i));

			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString().toUpperCase();

	}

	public static void main(String[] s) {
		String ss = "publicKey=moonplatcenter&gameId=1&platformId=1&act=2005&username=gd3527024&verifyInfo=E10ADC3949BA59ABBE56E057F20F883E&mobilePhone=&ip=10.6.0.119&client=iPod4,1&os=2=6.0&channel=142&childChannel=2&regType=5&realName=来自很好&idCardNo=";
		try {
			System.out.println(toMD5(ss));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
