package idevgame.meteor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tool {
	
	private static Logger logger = LoggerFactory.getLogger(Tool.class);

	public static void sleep(long time)
	{
		if(time <= 0)
		{
			return;
		}
		try
		{
			Thread.sleep(time);
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
	}
	
	public static void safeCloseInputStream(InputStream ips)
	{
		if(ips == null)
		{
			return;
		}

		try
		{
			ips.close();
		}catch(Exception e)
		{
			// e.printStackTrace();
		}
	}

	public static void safeCloseOutputStream(OutputStream ops)
	{
		if(ops == null)
		{
			return;
		}

		try
		{
			ops.close();
		}catch(Exception e)
		{
			// e.printStackTrace();
		}
	}
	

	/**
	 * 鐏忥拷-128,127閻ㄥ垺yte鏉烆剚宕查幋锟�0,255閻ㄥ埇nt閺佸瓨鏆�
	 * @param b
	 * @return
	 */
	public static short byte2short(byte b)
	{
		//return (short) (b < 0 ? 256 + b : b);
		return (short)(b & 0xff);
	}
	
	public static int parseInt(String text,int defaultValue){
		
    	if (text == null || text.length() <= 0)
    		return defaultValue;
    	try {
    		return Integer.parseInt(text.trim());
    	} catch (Exception ex) {
    	}
    	return defaultValue;
	}
	public static int parseInt(String text){
		return parseInt(text,0);
	}
	
	public static int parseInt16(String text,int defaultValue){
		
    	if (text == null || text.length() <= 0)
    		return defaultValue;
    	try {
    		return Integer.parseInt(text.trim(),16);
    	} catch (Exception ex) {
    	}
    	return defaultValue;
	}
	
	public final static String[] split(String msg,String sep){
		if(msg == null || "".equals(msg) || sep == null || "".equals(sep)){
			return null;
		}
		Vector list = new Vector();
		int index = msg.indexOf(sep) ;
		while(index >= 0){
			String str = msg.substring(0,index);
			list.addElement(str);
			msg = msg.substring(index+1);
			index = msg.indexOf(sep);
		}
		list.addElement(msg);
		String[] tempResult = new String[list.size()];
		int idx = 0;
		String tempStr = null;
		for(int i=0; i<list.size(); i++){
			tempStr = (String)list.elementAt(i);
			if(tempStr == null || "".equals(tempStr)){
				continue;
			}
			tempResult[idx] = tempStr;
			idx++;
		}
		
		String[] result = new String[idx];
		System.arraycopy(tempResult, 0, result, 0, idx);
		return result;
	}
	  
    /**
     * 閼惧嘲绶�2娑擃亞鐓╄ぐ銏犲隘閸╃喓娈戞禍銈夋肠
     * @param x1
     * @param y1
     * @param w1
     * @param h1
     * @param x2
     * @param y2
     * @param w2
     * @param h2
     * @return 娴溿倝娉﹂弫鐗堝祦 ,娴溿倝娉﹂惌鈺佽埌閻ㄥ垕,Y,Width,Height
     */
    public static int[] rectGetIntersection(
    		int x1, int y1, int w1, int h1, 
    		int x2, int y2, int w2, int h2)
    {
    	
    	int x = x1;
    	if(x2 > x1){
    		x = x2;
    	}
    	
    	int y = y1;
    	if(y2 > y1){
    		y = y2;
    	}
    	
    	int endX1 = x1 + w1;
    	int endX2 = x2 + w2;
    	
    	int endX = endX1;
    	if(endX2 < endX1){
    		endX = endX2;
    	}
    	
    	int endY1 = y1 + h1;
    	int endY2 = y2 + h2;
    	
    	int endY = endY1;
    	if(endY2 < endY1){
    		endY = endY2;
    	}
    	
    	int w = endX - x;
    	int h = endY - y;
    	
    	if(w < 0 || h < 0){
    		w = 0;
    		h = 0;
    	}
    	return new int[]{x,y,w,h};
    }
    
    /**
     * 鐢摜鏁ら弬瑙勭《;
     * 閸掋倖鏌�2娑擃亞鐓╄ぐ銏☆攱閺勵垰鎯佺喊鐗堟寬;
     * 
     * 娑撴槒顩﹂悽銊ょ艾閸掋倖鏌囩划鍓т紥/缁墽浼掗崚鍥ф健 閺勵垰鎯侀崷锟� 鐏炲繐绠烽崘锟�;
     * @param _x1
     * @param _y1
     * @param _w1
     * @param _h1
     * @param _x2
     * @param _y2
     * @param _w2
     * @param _h2
     * @return
     */
    public final static boolean isColliding(int _x1, int _y1, int _w1, int _h1,
    		int _x2, int _y2, int _w2, int _h2)
    {
        if(_x1 + _w1 <= _x2      ) return false;
        if(_x1       >= _x2 + _w2) return false;
        if(_y1 + _h1 <= _y2      ) return false;
        if(_y1       >= _y2 + _h2) return false;
        return true;
    }
    
    /**鐢摜鏁ら弬瑙勭《閿涘苯鍨介弬顓犲仯閺勵垰鎯�(pointx,pointy)閸︹墽ect(x,y,w,h)閸栧搫鐓欓崘锟�
     * @param x
     * @param y
     * @param w
     * @param h
     * @param pointx
     * @param pointy
     * @return
     */
    public final static boolean rectIn(int x, int y, int w, int h, int pointx, int pointy)
    {
        if(x > pointx || y > pointy){
        	return false;
        }
        if((x + w) < pointx ){
        	return false;
        }
        if((y + h) < pointy){
        	return false;
        }
        return true;
    }
    
    public static boolean rectIntersect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
    {
       return Tool.isColliding(x1, y1, w1, h1, x2, y2, w2, h2);
    }
    
    /**
     * (x1,y1,w1,h1)閺勵垰鎯佺�瑰苯鍙忛崠鍛儓(x2,y2,w2,h2)
     * @param x1
     * @param y1
     * @param w1
     * @param h1
     * @param x2
     * @param y2
     * @param w2
     * @param h2
     * @return
     */
    public static boolean rectContain(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
    {
        if(x1 > x2){
        	return false;
        }
        int i2;
        int j2;
        i2 = x1 + w1;
        j2 = x2 + w2;
        if(i2 < j2 || y1 > y2){
        	return false;
        }
        int k2;
        i2 = y1 + h1;
        k2 = y2 + h2;
        if(i2 < k2){
        	return false;
        }
        return true;
    }
    
    // ============================================================================== //
    // 閺佹壆绮嶉幙宥勭稊
    public static short getShort(byte[] arr, int idx){
    	
    	if (arr==null || (idx + 1 >= arr.length)) {
    		return 0;
    	}
    	int v = arr[idx]  & 0xff;
		v = (v << 8) |  (arr[idx+1]  & 0xFF);
        return (short)v;
	}
    
    public static int getInt(byte[] arr, int idx){
    	
    	if (arr==null || (idx +3 >= arr.length)) {
    		return 0;
    	}
    	
    	int v = arr[idx]  & 0xff;
		v = (v << 8) |  (arr[idx+1]  & 0xFF);
		v = (v << 8) | ( arr[idx+2] & 0xFF);
		v = (v << 8) | ( arr[idx+3] & 0xFF);
        return v;
    }
    
    public static int getLong(byte[] arr, int idx){
    	
    	if (arr==null || (idx + 7 >= arr.length)) {
    		return 0;
    	}
    	
    	int v = arr[idx]  & 0xff;
		v = (v << 8) |  (arr[idx+1] & 0xFF);
		v = (v << 8) | ( arr[idx+2] & 0xFF);
		v = (v << 8) | ( arr[idx+3] & 0xFF);
		v = (v << 8) | ( arr[idx+4] & 0xFF);
		v = (v << 8) | ( arr[idx+5] & 0xFF);
		v = (v << 8) | ( arr[idx+6] & 0xFF);
		v = (v << 8) | ( arr[idx+7] & 0xFF);
        return v;
    }
    public static String getUTF(byte[] datas, int offset)
	{
		if(datas == null || offset >= datas.length - 2)
		{
			return null;
		}
		
		int index = offset;
		short length = getShort(datas, index);
		index += 2;
		if(index + length > datas.length)
		{
			return null;
		}
		
		try
		{
			return new String(datas, index, length, "UTF-8");
		}catch(UnsupportedEncodingException e)
		{
			return new String(datas, index, length);
		}
	}
    // ============================================================================== //
    
    
    public static Random random = new Random();
    
    /**
     * rand(int range) only return [0-range) exclude range.
     * Maximum is (range-1), no check on negative range, don't try.
     * It is similar to CLDC1.1 ran.nextInt(n)
     * @param range
     * @return
     */
    public final static int rand(int range) {
        if (range==0) return 0;
        // assert range>0
        // 0 may have division by zero error.
        return Math.abs(random.nextInt() % range);
    }
   
    /**
	 * random producer
	 * @param start the start point 
	 * @param end the end point 
	 * @return the random value閿涘cope is[start,end]閿涳拷
	 */
    public final static int rand(int start, int end) {
        int k1 = random.nextInt();
        if (k1 < 0)
            k1 = -k1;
        return k1 % ((end - start) + 1) + start;
    }
    
    public static int[] getCopyData(int[] data){
    	
    	int[] copyData = null;
    	
    	if(data != null){
    		copyData = new int[data.length];
    		System.arraycopy(data, 0, copyData, 0, data.length);
    	}
    	return copyData;
    }

    
    public static int getOffsetValue(int pixel,int totalSize,int screenSize,int screenSizeC) {

		int dis;        
		//閸︽澘娴樺В鏂跨潌楠炴洖顔�;
		if (totalSize > screenSize) {     
			dis = screenSizeC - pixel;            
			//娑擃參妫挎禒銉ヤ箯
			if (dis > 0)
				dis = 0;           
			//娑擃參妫挎禒銉ュ礁
			else if (dis < screenSize - totalSize)
				dis = screenSize - totalSize;            
		}
		else {
			dis = (screenSize - totalSize) / 2;
		}      
		return dis;
	}
    
    public static final int entropy(int _cur, int _target)
    {
    	return entropy(_cur,_target,false);
    }

    public static final int entropy(int _cur,int _target,boolean isAdjust){
    	if (_cur != _target)
    	{
    		if(isAdjust&&Math.abs(_cur-_target)<30){
    			_cur = _target;
    		}
    		else{
    			int m_oldH = _cur;
    			
//    			_cur = (_cur * 3 + _target) >> 2;
    			//_cur = (_cur * speed1 + _target) >> speed2;
    			
    			_cur = (_cur * 3 + _target * 2) / 5;
    			
//    			_cur += ((_target-_cur) * 2) / 5;
    			
    			if (m_oldH == _cur){
    				_cur = _target;
    			}
    		}
    	}
    	return _cur;
    }
    
    /**
	 * Function name:sqrt
	 * Description: 缁狅拷閸楁洖绱戦獮铏煙
	 * @param n
	 * @return
	 */
	public static int sqrt(int n) {
		int r, l, t; //r: 閺傝鐗�; l: 娴ｆ瑦鏆�; t: 鐠囨洟娅庨弫锟�; 
		if (n < 100) {
			r = 9;
			while (n < r * r)
				r--;
		} else {
			r = sqrt(n / 100);
			l = n - r * r * 100;
			t = l / (r * 20);
			while (t * (r * 20 + t) > l) {
				t--;
			}
			r = r * 10 + t;
		}
		return r;
	}
	
	public static int getCost(int x1,int y1,int x2,int y2) {
	    // 閼惧嘲绶遍崸鎰垼閻愬綊妫垮顔硷拷锟� 閸忣剙绱￠敍锟�(x1, y1)-(x2, y2)
	    int m = Math.abs(x1 - x2);
	    int n = Math.abs(y1 - y2);
	    // 閸欐牔琚遍懞鍌滃仯闂傚瓨顑傞崙鐘垫倞瀵扮柉绐涚粋浼欑礄閻╁鍤庣捄婵堫瀲閿涘浠涙稉杞板強娴犲嘲锟界》绱濋悽銊や簰閼惧嘲绶遍幋鎰拱
	    return sqrt(m * m + n * n);
	  }


	// ---------------------- (x,y) Key 閻╃鍙� ----------------- //
	// 婵″倹鐏夌憰浣峰▏閻€劏绀嬮弫鎵畱鐠囨繐绱癵etXKey,getYKey鐟曪拷 int -> short
	public static int setKeyXY(int x, int y){
		return (x & 0xffff) | ((y & 0xffff) << 16);
	}
	public static int getXKey(int key){
		return (key & 0xffff);
	}
	public static int getYKey(int key){
		return ((key >> 16) & 0xffff);
	}
	// ----------------------------------------------------- //
    
    public static String appendString(String[] strs,int startIndex,int endIndex){
    	StringBuffer sb = new StringBuffer();
    	
    	if(strs != null){
    		for(int i=startIndex;i<endIndex; i++){
    			
    			if(i<0 || i>= strs.length){
    				continue;
    			}
    			
    			if(strs[i] == null){
    				continue;
    			}
    			sb.append(strs[i]);
    		}
    	}
    	
    	return sb.toString();
    }
    
    
	public static void debug(Object object)
	{
		if(object == null)
		{
			return;
		}
//		if(LogicCommon.isOperTest==true){
//			logger.info("DEBUG: " + object.toString());
//		}
	}

	public static String join(short[] list, String sep)
	{
		StringBuffer sb = new StringBuffer();
		
		if(list == null){
			return "";
		}
		
		for(int i=0; i<list.length; i++){
			sb.append(list[i]);
			sb.append(sep);
		}
		
		return sb.toString();
	}
	
    public static String join(byte[] list, String sep)
	{
		StringBuffer sb = new StringBuffer();
		
		if(list == null){
			return "";
		}
		
		for(int i=0; i<list.length; i++){
			sb.append(list[i]);
			sb.append(sep);
		}
		
		return sb.toString();
	}
    public static String join(int[] list, String sep)
	{
		StringBuffer sb = new StringBuffer();
		
		if(list == null){
			return "";
		}
		
		for(int i=0; i<list.length; i++){
			sb.append(list[i]);
			sb.append(sep);
		}
		
		return sb.toString();
	}
    
    /**
     * 鐎圭偟鏁ら弬瑙勭《, 閹垫挸宓冮弫鎵矋閸愬懎顔�
     * @param objects
     * @param objectName
     */
    public static void printArray(Object[] objects, String objectName)
    {
    	if(objects == null) 
    	{
    		return;
    	}
    	
    	for(int i = 0 ; i < objects.length ; i ++)
    	{
    		debug(objectName + "[" + i + "]=" + objects[i]);
    	}
    }
    
    public static boolean isEmulator()
    {
    	//System.getProperty("microedition.platform") //閼惧嘲绶遍幍瀣簚楠炲啿褰�
        byte byte0 = 0;
        try
        {
            if(Class.forName("java.applet.Applet") != null){
            	byte0 = 1;
            }
        }
        catch(Exception exception) { }
        try
        {
            if(byte0 == 0 && Class.forName("emulator.Emulator") != null){
            	byte0 = 2;
            }
        }
        catch(Exception exception1) { }
        //閹靛婧�妞ょ晫顏� 閸愬懎鐡�=8000000 
        if(byte0 == 0 && Runtime.getRuntime().totalMemory() - 0x3001dbL == 0x4a1025L)
            byte0 = 3;
        return byte0 != 0;
    }
    
    /**
     * 閸╄櫣顢呴崐锟�+閸旂姵鍨氶崐纭风礉濡拷閺屻儰绗傛稉瀣鏉╂柨娲�
     * @param base 閸╄櫣顢呴崐锟�
     * @param add 閸旂姵鍨氶崐锟�
     * @param min 閺堬拷鐏忓繐锟斤拷
     * @param max 閺堬拷婢堆冿拷锟�
     * @return
     */
    public static int sumValue(int base, int add, int min, int max){
    	
    	int tepValue = base+add;
    	
    	if(base > 0 && add > 0 && tepValue <= 0){//鐡掑﹦鏅禍锟�,娴ｈ法鏁ら張锟芥径褍锟斤拷
    		tepValue = max;
    	}
    	
    	if(tepValue < min){
    		tepValue = min;
    	}
    	
    	if(tepValue > max){
    		tepValue = max;
    	}
    	
    	return tepValue;
    }

    /**
     * 闁俺绻冮幎锟介懗绲�D閸滃本濡ч懗鐣岀搼缁狙呮畱闁槒绶拋锛勭暬閼惧嘲褰囬幎锟介懗鑺ュ劔娑擄拷閻ㄥ嚚ey
     * @param skillID
     * @param level
     * @return
     */
    public final static Integer getSkillKey(int skillID, byte level){
		return new Integer(((level) << 24 | skillID & 0x00FFFFFF));
	}
    
    /**
     * 闁俺绻冩稉锟芥稉顏堟肠閸氬湯tring鐏忎浇顥婃稉锟芥稉鐚則ring閺佹壆绮�
     * @param vector
     * @return
     */
    public static final String[] getStringArrayByVector(Vector vector){
		if(vector == null){
    		return null;
    	}
		int size = vector.size();
		if(size <= 0) {
			return null;
		}
    	
    	String[] array = new String[size];
    	for(int i=0; i<size; i++){
    		String str = (String) vector.elementAt(i);
    		if(str == null){
    			continue;
    		}
    		
    		array[i] = str;
    	}
    	return array;
    }
    
    /**
     * 閸掋倖鏌囬弰顖氭儊閺佹壆绮嶇搾濠勬櫕
     * @param index
     * @param array
     * @return
     */
    public static boolean isArrayIndexOutOfBounds(int index, Object arrayObject){
    	
    	if(arrayObject == null){
    		return true;
    	}
    	
    	int length = 0;
    	if(arrayObject instanceof byte[]){
    		length = ((byte[])arrayObject).length;
    	}
    	else if(arrayObject instanceof short[]){
    		length = ((short[])arrayObject).length;
    	}
    	else if(arrayObject instanceof int[]){
    		length = ((int[])arrayObject).length;
    	}
    	else if(arrayObject instanceof String[]){
    		length = ((String[])arrayObject).length;
    	}
    	else if(arrayObject instanceof Vector){
    		length = ((Vector)arrayObject).size();
    	}
    	
    	if(index < 0 || index >= length){
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 閼惧嘲绶辨担宥嗘殶閻ㄥ嫭甯洪惍浣革拷锟�
     * 娓氬顩�
     * 1娴ｏ拷 = 0x1;
     * 2娴ｏ拷 = 0x3;
     * 3娴ｏ拷 = 0x7;
     * @param bitNum
     * @return
     */
    public static int getMaskBitValue(int bitNum){
    	
    	if(bitNum >= 32){
    		return 0xFFFFFFFF;
    	}
    	
    	int value = 0;
    	for(int i=0;i<bitNum;i++){
    		value |= 1 << i;
    	}
    	return value;
    }
    
    /**
	 * 閸掋倖鏌囩�涙顑佹稉鍙夋Ц閸氾缚璐烴ULL | 缁岋拷
	 * @param text
	 * @return
	 */
	public static boolean isNullText(String text){
		
		if(text == null){
			return true;
		}
		
		if(text.trim().equals("")){
			return true;
		}
		return false;
	}
	
	/**
	 * 娴ｅ秵鎼锋担婊嗙箥缁狅拷(鐠佸墽鐤嗛崐锟�)
	 * @param flag true鐞涖劎銇氱拋锟�1,false鐞涖劎銇氱拋锟�0
	 * @param index 缁楊剙鍤戞担锟�
	 * @param value 鐠佸墽鐤嗛惃鍕拷锟�
	 * @return
	 */
	public static int setBit(boolean flag, int index, int value){ 
		if(flag){
			value |=  index;
		}
		else{
			value &= ~index;
		}
		return value;
	}
	/**
	 * 娴ｅ秵鎼锋担婊嗙箥缁狅拷(閸掋倖鏌囬崐锟�)
	 * @param index 缁楊剙鍤戞担锟�
	 * @param value 閸掋倖鏌囬惃鍕拷锟�
	 * @return
	 */
	public static boolean isBit(int index, int value){
		return (value & index) != 0;
	}
	
	/**
	 * 閼惧嘲绶遍幒鈺冪垳閻ㄥ嫪缍呴弫甯礄閺堬拷婢讹拷8娴ｅ稄绱�
	 * 娓氬顩� 1111 = 4娴ｏ拷
	 * @param maskValue
	 * @return
	 */
	public static int getBitNum(int maskValue){
		
		int bitNum = 0;
		for(int i=0;i<8;i++){
			int bitValue = 1 << i;
			if(isBit(bitValue, maskValue)){
				bitNum ++;
			}
		}
		
		return bitNum;
	}
	
	/**
	 * 
	 * Function name:getSubList
	 * Description: 閻€劋绨�甸�涚娑擃亜鍨悰銊ㄧ箻鐞涘苯鍨庢い闈涱槱閻烇拷
	 * @param <T>
	 * @param orgList: 濠ф劕鍨悰锟�
	 * @param pageIndex閿涙岸銆夐惍锟�
	 * @param pageSize閿涙艾鍨庢い鍨蒋閻╊喗鏆�
	 * @param subList閿涙艾鍨庢い闈涙倵閻ㄥ嫬鍨悰锟�
	 * @return閿涙俺绻戦崶鐐电波閺嬶拷(-1閸掑棝銆夐崣鍌涙殶瀵倸鐖� >=0閹崵娈戞い鍨殶)
	 */
	public static <T> List<T> getSubList(List<T> orgList,int pageIndex,int pageSize){
		List<T> subList = new ArrayList<T>(pageSize);
		if(pageIndex <= 0){//妞ょ敻娼版稉濠囨鐡掑懍绨￠敍宀冪箲閸ョ偟顑囨稉锟芥い锟�
			pageIndex = 1;
		}
		
		if(orgList == null || orgList.size() == 0 || pageSize <= 0) {
			return subList;
		}
		
		int size = orgList.size();
		int pageTotalNum = (size%pageSize) == 0 ? (size/pageSize) : ((size/pageSize)+1);
		if(pageTotalNum<pageIndex){//妞ょ數鐖滅搾鍛啊閿涘矁绻戦崶鐐存付閸氬簼绔存い锟�
			pageIndex = pageTotalNum;
		}
		
		int start = pageSize*(pageIndex-1);
		int end = pageSize*pageIndex;
		end = end > size ? size : end;
		subList = orgList.subList(start, end);
		return subList;
	}

	public static <T> boolean isHaveSameElementInList(List<T> list){
		List<T> tempList = new ArrayList<>();
		for (T temp : list) {
			if(tempList.contains(temp)){
				return true;
			}else{
				tempList.add(temp);
			}
		}
		
		return false;
	}
}
