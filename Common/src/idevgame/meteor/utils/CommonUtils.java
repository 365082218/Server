package idevgame.meteor.utils;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 缁鎮曠粔甯窗CommonUtils 缁粯寮挎潻甯窗鐢摜鏁ゅ銉ュ徔缁拷 閸掓稑缂撴禍鐚寸窗yxh 閸掓稑缂撻弮鍫曟？閿涳拷2012-7-16 娑撳﹤宕�07:43:40 娣囶喗鏁兼径鍥ㄦ暈閿涳拷
 * 
 * @version 1.0.0
 * 
 */
public class CommonUtils {

	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	/**
	 * 閺勵垰鎯侀弫鏉戣埌缁鐎�
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isInteger(Object data) {
		if (data == null || "".equals(data))
			return false;
		String reg = "[\\d]+";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(data.toString());
		return Integer.MAX_VALUE >= Double.parseDouble(data.toString())
				&& m.matches();
	}

	private static Pattern contentPattern = Pattern
			.compile("[\u4e00-\u9fa50-9a-zA-Z,，。.?？!！@()（）\\[\\]\\-_]+");
	/**
	 * 閸掋倖鏌囨稉锟芥稉顏堟肠閸氬牊妲搁崥锔胯礋缁岀儤鍨ㄥ▽鈩冩箒閸忓啰绀�
	 * 
	 */
	public static <T> boolean isCollectionEmpty(Collection<T> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 鐎涙顑佹稉鍙夋Ц閸氾缚璐熺粚锟�
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNull(String str) {
		return str == null || str.length() == 0 || str.equals(" ")
				|| str.equals("null") || str.trim().length() == 0;
	}


	/**
	 * 閸旂喕鍏橀敍姘灲閺傤厼鐡х粭锔胯閺勵垰鎯佹稉鐑樻殶鐎涳拷
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @param obj
	 * @return
	 */
	public static int parseInt(String obj) {
		if (obj == null || obj.equals("")) {
			return 0;
		}
		try {
			if (!obj.toString().contains(".")) {
				return Integer.parseInt(obj.toString());
			}
			return (int) Double.parseDouble((obj.toString()));
		} catch (Exception e) {
			logger.error("鐎涙顑佹稉鐬穧鏉烆剚宕瞚nt閸戞椽鏁�",obj,e);
		}
		return 0;
	}


	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static float parseFloat(Object obj) {
		if (obj == null || obj.equals("")) {
			return 0;
		}
		try {
			return Float.parseFloat(obj.toString());
		} catch (Exception e) {
			logger.error("鐎涙顑佹稉鐬穧鏉烆剚宕瞗loat閸戞椽鏁�",obj,e);
		}
		return 0;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static long parseLong(Object obj) {
		if (obj == null || obj.equals("")) {
			return 0;
		}
		try {
			if (!obj.toString().contains(".")) {
				return Long.parseLong(obj.toString());
			}
			return (long) Double.parseDouble((obj.toString()));
		} catch (Exception e) {
			logger.error("鐎涙顑佹稉鐬穧鏉烆剚宕瞕ouble閸戞椽鏁�",obj,e);
		}
		return 0;
	}

	/**
	 * 閸愬懎顔愰弰顖氭儊閸氬牊纭�
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isContentIll(String content) {
		Matcher matcher = contentPattern.matcher(content);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			sb.append(matcher.group());
		}
		String ill = sb.toString();
		if (!ill.equals(content))
			return false; // 閺堝澹掑▓濠傜摟缁楋拷
		return true;
	}

	private static Pattern namePattern = Pattern
			.compile("[\u4e00-\u9fa50-9a-zA-Z]+");

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isNameIll(String name) {
		Matcher matcher = namePattern.matcher(name);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			sb.append(matcher.group());
		}
		String ill = sb.toString();
		if (!ill.equals(name))
			return false; // 閺堝澹掑▓濠傜摟缁楋拷
		return true;
	}

	/**
	 * 閼惧嘲褰囬梾蹇旀簚閸婏拷
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandom(int minNumber, int maxNumber) {
		if (minNumber > maxNumber) {
			int temp = minNumber;
			minNumber = maxNumber;
			maxNumber = temp;
		}
		return (int) (minNumber + Math.random() * (maxNumber - minNumber + 1));
	}

	/**
	 * 閹稿銆庢惔蹇撳閸忋儱鍘撶槐锟�(閹绘帒鍙嗛幒鎺戠碍)
	 * 
	 * @param list
	 * @param o
	 *            (鐎圭偟骞嘋omparable閹恒儱褰�)
	 * @param true-闂勫秴绨� false-閸楀洤绨�
	 */
	@SuppressWarnings("unchecked")
	public static <T> void addOrderList(List<T> list, T o, boolean isDesc) {
		for (int i = 0; i < list.size(); i++) {
			if (isDesc) {
				if (((Comparable<T>) o).compareTo(list.get(i)) == 1) {
					list.add(i, o);
					return;
				}
			} else {
				if (((Comparable<T>) o).compareTo(list.get(i)) == -1) {
					list.add(i, o);
					return;
				}
			}
		}
		list.add(o);
	}

	/**
	 * 鏉╂柨娲栫�涙顑佹稉鏌ユ毐鎼达拷
	 * 
	 * @param value
	 * @return
	 */
	public static int stringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 閼惧嘲褰囩�涙顔岄崐鑲╂畱闂�鍨閿涘苯顩ч弸婊冩儓娑擃厽鏋冪�涙顑侀敍灞藉灟濮ｅ繋閲滄稉顓熸瀮鐎涙顑侀梹鍨娑擄拷2閿涘苯鎯侀崚娆庤礋1 */
		for (int i = 0; i < value.length(); i++) {
			/* 閼惧嘲褰囨稉锟芥稉顏勭摟缁楋拷 */
			String temp = value.substring(i, i + 1);
			/* 閸掋倖鏌囬弰顖氭儊娑撹桨鑵戦弬鍥х摟缁楋拷 */
			if (temp.matches(chinese)) {
				/* 娑擃厽鏋冪�涙顑侀梹鍨娑擄拷2 */
				valueLength += 2;
			} else {
				/* 閸忔湹绮�涙顑侀梹鍨娑擄拷1 */
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * 閸掋倖鏌囨稉锟芥稉鐚癮p閺勵垰鎯佹稉铏光敄閹存牗鐥呴張澶婂帗缁憋拷
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <K, V> boolean isMapEmpty(Map<K, V> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 鏉烆剚宕茬�涙濡�
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
}
