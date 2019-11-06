package idevgame.meteor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDCardUtil {
	private static Map<String, String> cityMap = new HashMap<>();
	private static boolean isInit = false;
	/** 
     * 18娴ｅ秷闊╂禒鍊熺槈閺嶏繝鐛�,缁鏆愰惃鍕墡妤狅拷 
     * @author lyl 
     * @param idCard 
     * @return 
     */  
    public static boolean is18ByteIdCard(String idCard){  
        Pattern pattern1 = Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$"); //缁鏆愰惃鍕墡妤狅拷  
        Matcher matcher = pattern1.matcher(idCard);  
        if(matcher.matches()){  
            return true;  
        }  
        return false;  
    }  
    
    /** 
     * 18娴ｅ秷闊╂禒鍊熺槈閺嶏繝鐛�,濮ｆ棁绶濇稉銉︾壐閺嶏繝鐛� 
     * @author lyl 
     * @param idCard 
     * @return 
     */  
    public static boolean is18ByteIdCardComplex(String idCard){  
        Pattern pattern1 = Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$");   
        Matcher matcher = pattern1.matcher(idCard);  
        int[] prefix = new int[]{7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};  
        int[] suffix = new int[]{ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };  
        if(matcher.matches()){  
        	if (isInit == false) {
        		initCityMap();
			}
            if(cityMap.get(idCard.substring(0,2)) == null ){  
                return false;  
            }  
            int idCardWiSum=0; //閻€劍娼垫穱婵嗙摠閸擄拷17娴ｅ秴鎮囬懛顏冪娴犮儱濮為弶鍐ㄦ礈鐎涙劕鎮楅惃鍕拷璇叉嫲  
            for(int i=0;i<17;i++){  
                idCardWiSum+=Integer.valueOf(idCard.substring(i,i+1))*prefix[i];  
            }  
              
            int idCardMod=idCardWiSum%11;//鐠侊紕鐣婚崙鐑樼墡妤犲瞼鐖滈幍锟介崷銊︽殶缂佸嫮娈戞担宥囩枂  
            String idCardLast=idCard.substring(17);//瀵版鍩岄張锟介崥搴濈娴ｅ秷闊╂禒鍊熺槈閸欓鐖�  
              
            //婵″倹鐏夌粵澶夌艾2閿涘苯鍨拠瀛樻閺嶏繝鐛欓惍浣规Ц10閿涘矁闊╂禒鍊熺槈閸欓鐖滈張锟介崥搴濈娴ｅ秴绨茬拠銉︽ЦX  
            if(idCardMod==2){  
                if(idCardLast.equalsIgnoreCase("x")){  
                    return true;  
                }else{  
                    return false;  
                }  
            }else{  
                //閻€劏顓哥粻妤�鍤惃鍕崣鐠囦胶鐖滄稉搴㈡付閸氬簼绔存担宥堥煩娴犲�熺槈閸欓鐖滈崠褰掑帳閿涘苯顩ч弸婊�绔撮懛杈剧礉鐠囧瓨妲戦柅姘崇箖閿涘苯鎯侀崚娆愭Ц閺冪姵鏅ラ惃鍕煩娴犲�熺槈閸欓鐖�  
                if(idCardLast.equals(suffix[idCardMod]+"")){  
                    return true;  
                }else{  
                    return false;  
                }  
           }  
        }  
        return false;  
    }  
      
    public static void initCityMap() {  
    		if (isInit) {
				return;
			}
            cityMap.put("11", "閸栨ぞ鍚�");  
            cityMap.put("12", "婢垛晜瑙�");  
            cityMap.put("13", "濞屽啿瀵�");  
            cityMap.put("14", "鐏炶精銈�");  
            cityMap.put("15", "閸愬懓鎸嬮崣锟�");  
              
            cityMap.put("21", "鏉堣棄鐣�");  
            cityMap.put("22", "閸氬鐏�");  
            cityMap.put("23", "姒涙垿绶冲Ч锟�");  
              
            cityMap.put("31", "娑撳﹥鎹�");  
            cityMap.put("32", "濮圭喕瀚�");  
            cityMap.put("33", "濞存瑦鐫�");  
            cityMap.put("34", "鐎瑰绐�");  
            cityMap.put("35", "缁傚繐缂�");  
            cityMap.put("36", "濮圭喕銈�");  
            cityMap.put("37", "鐏炲彉绗�");  
              
            cityMap.put("41", "濞屽啿宕�");  
            cityMap.put("42", "濠�鏍у");  
            cityMap.put("43", "濠�鏍у础");  
            cityMap.put("44", "楠炲じ绗�");  
            cityMap.put("45", "楠炶儻銈�");  
            cityMap.put("46", "濞村嘲宕�");  
              
            cityMap.put("50", "闁插秴绨�");  
            cityMap.put("51", "閸ユ稑绐�");  
            cityMap.put("52", "鐠愰潧绐�");  
            cityMap.put("53", "娴滄垵宕�");  
            cityMap.put("54", "鐟楄儻妫�");  
              
            cityMap.put("61", "闂勬洝銈�");  
            cityMap.put("62", "閻㈡鍊�");  
            cityMap.put("63", "闂堟帗鎹�");  
            cityMap.put("64", "鐎逛礁顦�");  
            cityMap.put("65", "閺傛壆鏋�");  
              
//          cityMap.put("71", "閸欑増鍜�");  
//          cityMap.put("81", "妫ｆ瑦鑵�");  
//          cityMap.put("82", "濠㈡娊妫�");  
//          cityMap.put("91", "閸ヨ棄顦�");  
//          System.out.println(cityMap.keySet().size());  
            
            isInit = true;
        }

}
