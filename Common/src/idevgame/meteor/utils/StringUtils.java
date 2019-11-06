package idevgame.meteor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * string瀹搞儱鍙�
 * @author moon
 *
 */
public class StringUtils {
	/**
	 * 鏉╃偞甯撮弫鎵矋 
	 * 娑撳秵鏁幐浣哥唨绾拷缁鐎烽弫鎵矋娓氬顩ч敍姝﹏t[]
	 * @see #joinByInts(String, int...)
	 * @param separator 閸掑棝娈х粭锟�
	 * @param objects
	 */
	public static String join(String separator, Object...objects){
		if(objects.length==0){
			return "";
		}
		StringBuilder sb = new StringBuilder(objects[0].toString());
		for (int i = 1; i < objects.length; i++) {
			sb.append(separator).append(objects[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 鏉╃偞甯撮弫鎵矋
	 * @param separator 閸掑棝娈х粭锟�
	 * @param ints
	 */
	public static String joinByInts(String separator, int...ints){
		if(ints.length==0){
			return "";
		}
		StringBuilder sb = new StringBuilder(ints[0]+"");
		for (int i = 1; i < ints.length; i++) {
			sb.append(separator).append(ints[i]);
		}
		return sb.toString();
	}

	/**
	 * 鏉╃偞甯撮弫鎵矋
	 * @param separator 閸掑棝娈х粭锟�
	 * @param ls
	 */
	public static String join(String separator, List ls){
		if(ls == null || ls.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder(ls.get(0).toString());
		for (int i = 1; i < ls.size(); i++) {
			sb.append(separator).append(ls.get(i));
		}
		return sb.toString();
	}
	
	/** 
	 * 鏉烆兛绠熷锝呭灟閻楄鐣╃�涙顑� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 *  
	 * @param keyword 
	 * @return 
	 */  
	public static String escapeExprSpecialWord(String keyword) {  
	    if (StringUtils.isNotBlank(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	}  
	
	/**
	 * 鐎涙顑佹稉鍙夊閸掑棙鍨歁ap
	 *
	 * @param separator閿涙艾鍨庨梾鏃傤儊 娴兼俺绻樼悰宀冩祮娑斿顒滈崚娆戝濞堝﹤鐡х粭锟� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 * @param data:閹峰吋甯寸�涙顑佹稉锟� 娓氬顩_B_C
	 * @return Map<Integer, T> key鐠侊紕鐣绘禒锟�1瀵拷婵拷
	 * @input StringUtils.split("_", "A_B_C") 
	 * @output Map{1=A, 2=B, 3=C}
	 */
	@SuppressWarnings("unchecked")
	public static Map<Integer, String> splitToMap(String separator, String data){
		Map<Integer, String> maps = new HashMap<>();
		String[] datas = split(separator, data);
		if(datas.length == 0){
			return new HashMap<>();
		}
		int i = 1;//娴狅拷1瀵拷婵拷
		for(String str: datas){
			maps.put(i, str);
			i++;
		}
		return maps;
	}
	
	/**
	 * 鐎涙顑佹稉鍙夊閸掞拷
	 * @param separator:閸掑棝娈х粭锟� 娴兼俺绻樼悰宀冩祮娑斿顒滈崚娆戝濞堝﹤鐡х粭锟� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 * @param data
	 * @return
	 */
	public static String[] split(String separator, String data){
		if(isBlank(data) || isEmpty(separator)){
			return new String[0];
		}
		
		//闁灝鍘ら悧瑙勭暕鐎涙顑佹潻娑滎攽鏉烆兛绠�
		separator = escapeExprSpecialWord(separator);
		String[] datas = data.split(separator);
		return datas;
	}

	/**
	 * 鐎涙顑佹稉鍙夊閸掞拷
	 * @param separator:閸掑棝娈х粭锟� 娴兼俺绻樼悰宀冩祮娑斿顒滈崚娆戝濞堝﹤鐡х粭锟� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 * @param data
	 * @return
	 */
	public static int[] splitToInteger(String separator, String data){
		String[] datas = split(separator, data);
		if(datas == null || datas.length == 0){
			return null;
		}
		int[] res = new int[datas.length];
		for(int i = 0; i < datas.length; i++){
			double value = Double.valueOf(datas[i]);
			res[i] = (int) value;
		}
		
		return res;
	}
	
	/**
	 * 鐎涙顑佹稉鍙夊閸掞拷
	 * @param separator:閸掑棝娈х粭锟� 娴兼俺绻樼悰宀冩祮娑斿顒滈崚娆戝濞堝﹤鐡х粭锟� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 * @param data
	 * @return
	 */
	public static List<Integer> splitToListInteger(String separator, String data){
		String[] datas = split(separator, data);
		if(datas == null || datas.length == 0){
			return new ArrayList<Integer>();
		}
		List<Integer> res = new ArrayList<Integer>();
		for(int i = 0; i < datas.length; i++){
			double value = Double.valueOf(datas[i]);
			res.add((int) value);
		}
		return res;
	}
	
	/**
	 * 鐎涙顑佹稉鍙夊閸掞拷
	 * @param separator:閸掑棝娈х粭锟� 娴兼俺绻樼悰宀冩祮娑斿顒滈崚娆戝濞堝﹤鐡х粭锟� 閿涳拷$()*+.[]?\^{},|閿涳拷 
	 * @param data
	 * @return
	 */
	public static List<int[]> splitToArrList(String separator1, String separator2, String data){
		String[] datas = split(separator2, data);
		if(datas == null || datas.length == 0){
			return new ArrayList<>();
		}
		List<int[]> res = new ArrayList<>();
		for(int i = 0; i < datas.length; i++){
			int[] split = splitToInteger(separator1, datas[i]);
			res.add(split);
		}
		return res;
	}
	
	/**
	 * 鐎涙顑佹稉鎻掑瀻閸撳弶鍨歁ap
	 * <pre>
	 * 	StringUtils.splitToMap("_","|","a_A|b_B") = Map{a=A, b=B}
	 * </pre>
	 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
	 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
	 * @param data
	 * @return
	 */
	public static Map<Integer, Integer> splitToIntegerMap(String separator, String separator1, String data) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		String[] dataArr = StringUtils.split(separator1, data);
		
		for (String mapStr : dataArr) {
			String[] strs = StringUtils.split(separator, mapStr);
			if(strs.length != 2) { //閺嶇厧绱℃稉宥囶儊閸氬牏娈戠捄瀹犵箖
				continue;
			}
			if(map.containsKey(Integer.parseInt(strs[0]))) {
				continue;
			}
			map.put(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
		}
		
		return map;
	}
	
	/**
	 * Map閸氬牆鑻熼幋鎬眛ring
	 * <pre>
	 * 	StringUtils.combineToString("_","|", Map{a=A, b=B}) = "a_A|b_B"
	 * </pre>
	 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
	 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
	 * @param data
	 * @return
	 */
	public static <T, S> String combineToString(String separator, String separator1, Map<T, S> data) {
		if(data == null || data.isEmpty()) {
			return "";
		}
		
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<T, S> entry : data.entrySet()) {
			buffer.append(entry.getKey()).append(separator).append(entry.getValue()).append(separator1);
		}
		buffer.setLength(buffer.length() - 1);
		return buffer.toString();
	}
	
	
	
	/**
	 * 婢堆冨晸妫ｆ牕鐡уВ锟�
	 */
	public static String upFirst(String str) {
		return str.substring(0, 1).toUpperCase().concat(str.substring(1));
	}
	
	/**
	 * 鐏忓繐鍟撴＃鏍х摟濮ｏ拷
	 */
	public static String lowerFirst(String str) {
		return str.substring(0, 1).toLowerCase().concat(str.substring(1));
	}
	
	/**
	 * 妫ｆ牕鐡уВ宥呫亣閸愶拷
	 * @author son
	 * @param str 
	 */
	public static String upFirst1(String str) {
		char[] strs = str.toCharArray();
		if((strs[0] >= 'a' && strs[0] <= 'z')) {
			strs[0] -= 32;
			return String.valueOf(strs);
		}else {
			return upFirst(str);
		}
	}

	/**
	 * 娑撳鍨濈痪鍧楊棑閺嶈壈娴嗙亸蹇撳晸妞圭厧鍢�
	 */
	public static String underlineToLowerCamal(String s){
		String[] ss = s.split("_");
		for (int i = 1; i < ss.length; i++) {
			ss[i] = upFirst1(ss[i]);
		}
		return join("", ss);
	}

	/**
	 * 娑撳鍨濈痪鍧楊棑閺嶈壈娴嗘径褍鍟撴す鐓庡槻
	 */
	public static String underlineToUpperCamal(String s){
		String[] ss = s.split("_");
		for (int i = 0; i < ss.length; i++) {
			ss[i] = upFirst1(ss[i]);
		}
		return join("", ss);
	}

	/**
	 * 妞圭厧鍢叉潪顑跨瑓閸掓帞鍤�,閺堫亜顦╅悶鍡椼亣鐏忓繐鍟�
	 */
	public static String camalToUnderline(String s){
		StringBuilder sb = new StringBuilder();
		if(s.length()>0){
			sb.append(s.charAt(0));
		}
		for (int i = 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if(Character.isUpperCase(c)){
				sb.append("_");
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * 閸掋倖鏌囩�涙顑佹稉韫礋缁岀尨绱檔ull閹存牞锟斤拷""閿涳拷
	 * <pre>
	 * StringUtils.isEmpty(null) = true
	 * StringUtils.isEmpty("") = true
	 * StringUtils.isEmpty(" ") = false
	 * StringUtils.isEmpty("bob") = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		 return s == null || s.length() == 0;
	}
	
	/**
	 * 閸掋倖鏌囩�涙顑佹稉韫瑝娑撹櫣鈹栭敍鍧ll閹存牞锟斤拷""閿涳拷
	 * <pre>
	 * StringUtils.isNotEmpty(null) = false
	 * StringUtils.isNotEmpty("") = false
	 * StringUtils.isNotEmpty(" ") = true
	 * StringUtils.isNotEmpty("bob") = true
	 * StringUtils.isNotEmpty("  bob  ") = true
	 * </pre>
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return !StringUtils.isEmpty(s);
	}
	
	/**
	 * 閸掋倖鏌囩�涙顑佹稉韫礋缁岃櫣娅х�涙顑佹稉锟�(null閹存牞锟斤拷""閹存牞锟斤拷" ")
	 * <pre>
	 * StringUtils.isBlank(null) = true
	 * StringUtils.isBlank("") = true
	 * StringUtils.isBlank(" ") = true
	 * StringUtils.isBlank("bob") = false
	 * StringUtils.isBlank(" bob ") = false
	 * </pre>
	 * @param s
	 * @return
	 */
	public static boolean isBlank(String s) {
		int strLen;
		if(s == null || (strLen = s.length()) == 0 ) {
			return true;
		}
		
		for (int i = 0; i < strLen; i++) {
			if(Character.isWhitespace(s.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 閸掋倖鏌囩�涙顑佹稉韫瑝娑撹櫣鈹栭惂钘夌摟缁楋缚瑕�(null閹存牞锟斤拷""閹存牞锟斤拷" ")
	 * <pre>
	 * StringUtils.isNotBlank(null) = false
	 * StringUtils.isNotBlank("") = false
	 * StringUtils.isNotBlank(" ") = false
	 * StringUtils.isNotBlank("bob") = true
	 * StringUtils.isNotBlank(" bob ") = true
	 * </pre>
	 * @param s
	 * @return
	 */
	public static boolean isNotBlank(String s) {
		return !StringUtils.isBlank(s);
	}
	
	/**
	 * 閹搭亜褰囩�涙顑佹稉锟�
	 * <pre>
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "")  = ""
	 * StringUtils.substringBefore("ajp_djp_gjp_j", null)  = ""
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "jp_")  = "a"
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "jk_")  = "ajp_djp_gjp_j"
	 * </pre>
	 * @param str  鐞氼偅鍩呴崣鏍畱鐎涙顑佹稉锟�
	 * @param separator  閹搭亜褰囬崚鍡涙缁楋拷
	 * @return
	 */
    public static String substringBefore(String str, String separator) {
      if ((isEmpty(str)) || (separator == null)) {
    	  return str;
      }
      if (separator.isEmpty()) {
    	  return "";
      }
      int pos = str.indexOf(separator);
      if (pos == -1) {
    	  return str;
      }
      return str.substring(0, pos);
    }
    
    /**
	 * 閹搭亜褰囩�涙顑佹稉锟�
	 * <pre>
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "jp_")  = "defjp_ghi"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "")  = "ajp_djp_gjp_j"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", null)  = "ajp_djp_gjp_j"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "jk_")  = ""
	 * </pre>
	 * @param str  鐞氼偅鍩呴崣鏍畱鐎涙顑佹稉锟�
	 * @param separator  閹搭亜褰囬崚鍡涙缁楋拷
	 * @return
	 */
    public static String substringAfter(String str, String separator) {
      if (isEmpty(str)) {
        return str;
      }
      if (separator == null) {
        return "";
      }
      int pos = str.indexOf(separator);
      if (pos == -1) {
        return "";
      }
      return str.substring(pos + separator.length());
    }

    /**
   	 * 閹搭亜褰囩�涙顑佹稉锟�
   	 * <pre>
   	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "")  = "ajp_djp_gjp_j"
   	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", null)  = "ajp_djp_gjp_j"
   	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "jk_")  = "ajp_djp_g"
   	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "jp_")  = "ajp_djp_g"
   	 * </pre>
   	 * @param str  鐞氼偅鍩呴崣鏍畱鐎涙顑佹稉锟�
   	 * @param separator  閹搭亜褰囬崚鍡涙缁楋拷
   	 * @return
   	 */
    public static String substringBeforeLast(String str, String separator) {
      if ((isEmpty(str)) || (isEmpty(separator))) {
        return str;
      }
      int pos = str.lastIndexOf(separator);
      if (pos == -1) {
        return str;
      }
      return str.substring(0, pos);
    }

    /**
   	 * 閹搭亜褰囩�涙顑佹稉锟�
   	 * <pre>
   	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "")  = ""
   	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", null)  = ""
   	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "jk_")  = ""
   	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "jp_")  = "j"
   	 * </pre>
   	 * @param str  鐞氼偅鍩呴崣鏍畱鐎涙顑佹稉锟�
   	 * @param separator  閹搭亜褰囬崚鍡涙缁楋拷
   	 * @return
   	 */
    public static String substringAfterLast(String str, String separator) {
      if (isEmpty(str)) {
        return str;
      }
      if (isEmpty(separator)) {
        return "";
      }
      int pos = str.lastIndexOf(separator);
      if ((pos == -1) || (pos == str.length() - separator.length())) {
        return "";
      }
      return str.substring(pos + separator.length());
    }
    
//    /**
//	 * 鐎涙顑佹稉鎻掑瀻閸撳弶鍨歁ap楠炶埖鏂侀崗銉ょ炊閸忋儳娈憁ap閸愶拷
//	 * 	StringUtils.splitToMap("_","|","1_1_11|2_2_22") = Map{1=Map{1=11}, 2=Map{2=22}}
//	 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
//	 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
//	 * @param data
//	 * @param map
//	 * @param toneUp 閹绘劕宕屾晶鐐垫抄閻ф儳鍨庡В锟�
//	 * @return
//	 */
//	public static Map<Integer, Map<Integer, Integer>> stringAssemblyMap(String separator, String separator1, String data, Map<Integer, Map<Integer, Integer>> map, int toneUp) {
//		map = (map == null) ? new HashMap<Integer, Map<Integer, Integer>>() : map;
//		
//		if(isBlank(data)) {
//			return map;
//		}
//		
//		String[] dataArr = StringUtils.split(separator1, data);
//		
//		for (String mapStr : dataArr) {
//			String[] strs = StringUtils.split(separator, mapStr);
//			if(strs.length < 3) { //閺嶇厧绱℃稉宥囶儊閸氬牏娈戠捄瀹犵箖
//				continue;
//			}
//			
//			Integer type = Integer.parseInt(strs[0]);
//			Integer id = Integer.parseInt(strs[1]);
//			int num = Integer.parseInt(strs[2]);
//			
//			if(strs.length == 4) { //閸欐垿锟戒礁顨涢崝杈╂畱閺冭泛锟芥瑦婀佹径姘娑擃亝顩ч悳锟�
//				int random = Integer.parseInt(strs[3]);
//				
//				//鐠侊紕鐣诲鍌滃芳
//				int temp = RandomUtil.getRandomInt(1, 100);
//				if(temp > random){
//					continue;
//				}
//			}
//			
//			if(toneUp > 0 && num > 0){
//				num = NumericUtils.d100_EnhanceCount(num, toneUp);
//			}
//			
//			Map<Integer, Integer> subMap = map.get(type);
//			if(subMap == null ) {
//				subMap = new HashMap<>();
//				map.put(type, subMap);
//			}
//			num = subMap.get(id) == null ? num : num + subMap.get(id);
//			subMap.put(id, num);
//			
//		}
//		
//		return map;
//	}
	
	 /**
		 * 鐎涙顑佹稉鎻掑瀻閸撳弶鍨歁ap楠炶埖鏂侀崗銉ょ炊閸忋儳娈憁ap閸愶拷
		 * <pre>
		 * 	StringUtils.splitToMap("_","|","1_1_11|2_2_22") = Map{1=Map{1=11}, 2=Map{2=22}}
		 * </pre>
		 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
		 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
		 * @param data
		 * @param map
		 * @return
		 */
		public static Map<Integer, Map<Integer, Integer>> stringAssemblyMap(String separator, String separator1, String data, Map<Integer, Map<Integer, Integer>> map) {
			map = map == null ? new HashMap<Integer, Map<Integer, Integer>>() : map;
			
			if(isBlank(data)) {
				return map;
			}
			
			String[] dataArr = StringUtils.split(separator1, data);
			
			for (String mapStr : dataArr) {
				String[] strs = StringUtils.split(separator, mapStr);
				if(strs.length < 3) { //閺嶇厧绱℃稉宥囶儊閸氬牏娈戠捄瀹犵箖
					continue;
				}
				
				Integer type = Integer.parseInt(strs[0]);
				Integer id = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				
				if(strs.length == 4) { //閸欐垿锟戒礁顨涢崝杈╂畱閺冭泛锟芥瑦婀佹径姘娑擃亝顩ч悳锟�
					int random = Integer.parseInt(strs[3]);
					
					//鐠侊紕鐣诲鍌滃芳
					int temp = RandomUtil.getRandomInt(1, 100);
					if(temp > random){
						continue;
					}
				}
				
				Map<Integer, Integer> subMap = map.get(type);
				if(subMap == null ) {
					subMap = new HashMap<>();
					map.put(type, subMap);
				}
				num = subMap.get(id) == null ? num : num + subMap.get(id);
				subMap.put(id, num);
				
			}
			
			return map;
		}
	
		
		
		 /**
		 * 娑撹鐓勭粋蹇撳焺娑撴挾鏁�-鐎涙顑佹稉鎻掑瀻閸撳弶鍨歁ap楠炶埖鏂侀崗銉ょ炊閸忋儳娈憁ap閸愶拷
		 * <pre>
		 * 	StringUtils.splitToMap("_","|","1_1_11|2_2_22") = Map{1=Map{1=11}, 2=Map{2=22}}
		 * </pre>
		 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
		 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
		 * @param data
		 * @param map
		 * @param awardType 閻楃懓鐣鹃惃鍕殯閸旇京琚崹瀣剁礉=0 閹碉拷閺堝琚崹锟�
		 * @param randomPlus 閸旂姵鍨氶惃鍕洤閻滐拷,閻ф儳鍨庡В锟�
		 * @param value 閸旂姵鍨氶惃鍕殶闁诧拷
		 * @param percentageValue 閸旂姵鍨氶惃鍕閸掑棙鐦弫浼村櫤
		 * @return
		 */
		public static Map<Integer, Map<Integer, Integer>> stringAssemblyMapRandomPlus(String separator, String separator1, 
				String data, Map<Integer, Map<Integer, Integer>> map, int awardType, int randomPlus, int value, int percentageValue) {
			
			map = map == null ? new HashMap<Integer, Map<Integer, Integer>>() : map;
			
			if(isBlank(data)) {
				return map;
			}
			
			String[] dataArr = StringUtils.split(separator1, data);
			
			for (String mapStr : dataArr) {
				String[] strs = StringUtils.split(separator, mapStr);
				if(strs.length < 3) { //閺嶇厧绱℃稉宥囶儊閸氬牏娈戠捄瀹犵箖
					continue;
				}
				
				Integer type = Integer.parseInt(strs[0]);
				Integer id = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				
				if(strs.length == 4) { //閸欐垿锟戒礁顨涢崝杈ㄦ閸婃瑧娈戝鍌滃芳
					int random = Integer.parseInt(strs[3]);
					
					//鐠侊紕鐣诲鍌滃芳
					int temp1 = RandomUtil.getRandomInt(1, 100);
					if(temp1 > random){
						continue;
					}
				}
				
				//娑撹鐓勭粋蹇撳焺閸旂姵鍨�
				if(type == awardType || awardType == 0){
					int temp2 = RandomUtil.getRandomInt(1, 100);
					if(temp2 <= randomPlus){
						num += value;//婢х偛濮為弫浼村櫤
						if(percentageValue > 0){//婢х偟娉惂鎯у瀻濮ｏ拷
							num = NumericUtils.d100_EnhanceCount(num, percentageValue);
						}
					}
				}
				
				Map<Integer, Integer> subMap = map.get(type);
				if(subMap == null ) {
					subMap = new HashMap<>();
					map.put(type, subMap);
				}
				num = subMap.get(id) == null ? num : num + subMap.get(id);
				subMap.put(id, num);
				
			}
			
			return map;
		}
		
	/**
	 * 鐎涙顑佹稉鎻掑瀻閸撳弶鍨歁ap楠炶埖鏂侀崗銉ょ炊閸忋儳娈憁ap閸愶拷,濞屸剝婀佸鍌滃芳鐠侊紕鐣�
	 * <pre>
	 * 	StringUtils.splitToMap("_","|","1_1_11|2_2_22") = Map{1=Map{1=11}, 2=Map{2=22}}
	 * </pre>
	 * @param separator  key,value閻ㄥ嫬鍨庨崜锟�
	 * @param separator1 Map,Map閻ㄥ嫬鍨庨崜锟�
	 * @param data
	 * @param map
	 * @return
	 */
	public static Map<Integer, Map<Integer, Integer>> stringAssemblyMapNotProbability(String separator, String separator1, String data, Map<Integer, Map<Integer, Integer>> map) {
		map = map == null ? new HashMap<Integer, Map<Integer, Integer>>() : map;
		
		if(isBlank(data)) {
			return map;
		}
		
		String[] dataArr = StringUtils.split(separator1, data);
		
		for (String mapStr : dataArr) {
			String[] strs = StringUtils.split(separator, mapStr);
			if(strs.length < 3) { //閺嶇厧绱℃稉宥囶儊閸氬牏娈戠捄瀹犵箖
				continue;
			}
			
			Integer type = Integer.parseInt(strs[0]);
			Integer id = Integer.parseInt(strs[1]);
			int num = Integer.parseInt(strs[2]);
			
//			if(strs.length == 4) { //閸欐垿锟戒礁顨涢崝杈╂畱閺冭泛锟芥瑦婀佹径姘娑擃亝顩ч悳锟�
//				int random = Integer.parseInt(strs[3]);
//				
//				//鐠侊紕鐣诲鍌滃芳
//				int temp = RandomUtil.getRandomInt(1, 100);
//				if(temp > random){
//					continue;
//				}
//			}
			
			Map<Integer, Integer> subMap = map.get(type);
			if(subMap == null ) {
				subMap = new HashMap<>();
				map.put(type, subMap);
			}
			num = subMap.get(id) == null ? num : num + subMap.get(id);
			subMap.put(id, num);
			
		}
		
		return map;
	}
	

    
    /**
	 * Map閸掑棗澹婇幋鎰摟缁楋缚瑕嗛獮鑸垫杹閸忋儰绱堕崗銉ф畱String閸愶拷
	 * <pre>
	 * 	StringUtils.MapAssemblyString(Map{1=Map{1=11}, 2=Map{2=22}} data) ==>data = "1_1_11|2_2_22"
	 * </pre>
	 * @param map
	 * @param data 娴狅拷"_"閸滐拷"|"閸掑棝娈�
	 * @return
	 */
	public static String MapAssemblyString(String separator1, String separator2, Map<Integer, Map<Integer, Integer>> map, String data) {
		if(map == null || map.isEmpty()) {
			return data;
		}
		
		stringAssemblyMap(separator1, separator2, data, map);
		StringBuilder str = new StringBuilder();
		for (Map.Entry<Integer, Map<Integer, Integer>> entry : map.entrySet()) {
			for (Map.Entry<Integer, Integer> subEntry : entry.getValue().entrySet()) {
				str.append(entry.getKey()).append(separator1).append(subEntry.getKey()).append(separator1).append(subEntry.getValue()).append(separator2);
			}
		}
		int length = str.length();
		str.setLength(length > 0 ? length - 1 : 0);
		return str.toString();
		
	}
	
	  public static boolean isNumeric(String cs) {
	    if (isEmpty(cs)) {
	      return false;
	    }
	    int sz = cs.length();
	    for (int i = 0; i < sz; i++) {
	      if (!Character.isDigit(cs.charAt(i))) {
	        return false;
	      }
	    }
	    return true;
	  }
		/**
		 * 閹跺﹣浜抮egex閸掑棗澹婇惃鍕摟缁楋缚瑕嗛崚鍡毿掗幋鎰殶缂侊拷
		 * 
		 * @param str
		 * @return
		 */
		public static <T> T[] stringToArray(String str, String regex, Class<T> cls) {
			if (str == null || str.length() == 0) {
				return null;
			}
			String[] arr = split(str, regex);
			return stringToArray(arr, cls, 0);
		}
		
		/**
		 * 閹跺﹣浜抮egex閸掑棗澹婇惃鍕摟缁楋缚瑕嗛崚鍡毿掗幋鎰殶缂侊拷
		 * 
		 * @param str
		 * @return
		 */
		private static <T> T[] stringToArray(String[] scrValueArray, Class<T> cls,
				int startIndex) {
			if (scrValueArray == null || scrValueArray.length == 0) {
				return null;
			}
			int j = 0;
			if (cls == Integer.class) {
				Integer[] result = new Integer[scrValueArray.length - startIndex];
				for (int i = startIndex; i < scrValueArray.length; i++) {
					result[j] = (int)Float.parseFloat(scrValueArray[i]);
					j++;
				}
				return (T[]) result;
			} else if (cls == Float.class) {
				Float[] result = new Float[scrValueArray.length - startIndex];
				for (int i = startIndex; i < scrValueArray.length; i++) {
					result[j] = Float.parseFloat(scrValueArray[i]);
					j++;
				}
				return (T[]) result;
			} else if (cls == Long.class) {
				Long[] result = new Long[scrValueArray.length - startIndex];
				for (int i = startIndex; i < scrValueArray.length; i++) {
					result[j] = Long.parseLong(scrValueArray[i]);
					j++;
				}
				return (T[]) result;
			} else if (cls == String.class) {
				if (startIndex == 0) {
					return (T[]) scrValueArray;
				} else {
					String[] result = new String[scrValueArray.length - startIndex];
					for (int i = startIndex; i < scrValueArray.length; i++) {
						result[j] = scrValueArray[i];
						j++;
					}
				}
			}
			return null;
		}
}
