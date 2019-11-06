package idevgame.meteor.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 
 * 
 *
-----------------------------------------------------
Letter  Date or Time Component  Presentation  	Examples  
G  		Era designator  		Text  			AD  
y  		Year  					Year  			1996; 96  	
M  		Month in year  			Month  			July; Jul; 07  
w  		Week in year  			Number  		27  
W  		Week in month  			Number  		2  
D  		Day in year  			Number  		189  
d  		Day in month  			Number  		10  
F  		Day of week in month  	Number  		2  
E  		Day in week  			Text  			Tuesday; Tue  
a  		Am/pm marker  			Text  			PM  
H  		Hour in day (0-23)  	Number  		0  
k  		Hour in day (1-24)  	Number  		24  
K  		Hour in am/pm (0-11)  	Number  		0  
h  		Hour in am/pm (1-12)  	Number  		12  
m  		Minute in hour  		Number  		30  
s  		Second in minute  		Number  		55  
S  		Millisecond  			Number  		978  
z  		Time zone  General 		time zone  		Pacific Standard Time; PST; GMT-08:00  
Z  		Time zone  RFC 822 		time zone  		-0800  
-----------------------------------------------------
Quick Format
yyyy/MM/dd 	= 2008/09/12
yy-MM-dd 	= 08-09-12
-----------------------------------------------------
 * @author hj
 *
 */
public class DateUtil {
	/** 濮ｎ偆顫�:1缁夛拷 */
	public static final long MS = 1000L; //濮ｎ偆顫�:1缁夛拷
	/** 缁夛拷:娑擄拷閸掑棝鎸� */
	public static final long TOTAL_SEC_PER_MINUTE = 60; //缁夛拷:娑擄拷閸掑棝鎸�
	/** 缁夛拷:娑擄拷鐏忓繑妞� */
	public static final long TOTAL_SEC_PER_HOUR = 60L*TOTAL_SEC_PER_MINUTE;//缁夛拷:娑擄拷鐏忓繑妞�
	/** 缁夛拷:娑擄拷婢讹拷 */
	public static final long TOTAL_SEC_PER_DAY = 24L*TOTAL_SEC_PER_HOUR;//缁夛拷:娑擄拷婢讹拷
	/** 濮ｎ偆顫�:娑擄拷婢讹拷 */
	public static final long TOTAL_MS_PER_DAY = 24L*TOTAL_SEC_PER_HOUR*MS;//濮ｎ偆顫�:娑擄拷婢讹拷
	/** 濮ｎ偆顫�:娑擄拷閸涳拷 */
	public static final long TOTAL_MS_PER_WEEK = 7L*24*TOTAL_SEC_PER_HOUR*MS;//濮ｎ偆顫�:娑擄拷閸涳拷
	
	/** yyyy-MM-dd */
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	/** yyyy-MM-dd HH:mm:ss */
	public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** yyyyMMdd */
	public static final String DATE_PATTERN1 = "yyyyMMdd";
	/** yyyyMMddHHmmss */
	public static final String TIME_PATTERN1 = "yyyyMMddHHmmss";
	
	/**
	 * @return yyyy-MM-dd
	 */
	public static String getToday() {
		Date today = new Date();
		return format(today);
	}
	/**
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getAllToday() {
		Date today = new Date();
		return formatAllDate(today);
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫�
	 * @return 鏉╂柨娲栭弽鐓庣础 yyyyMMdd	
	 */
	public static String getNotFlagToday() {
		Date today = new Date();
		return formatNotFlagDate(today);
	}
	
	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫�
	 * @return 鏉╂柨娲栭弽鐓庣础 yyyyMMddHHmmss
	 */
	public static String getToDayTimestamp(){
		Date today = new Date();
		return formatgetToDayTimestampDate(today);
	}
	
	
	/**
	 * 閺嶇厧绱￠崠鏍ㄦ闂傦拷  yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, DATE_PATTERN);
	}

	/**
	 * 閺嶇厧绱￠崠鏍ㄦ闂傦拷  yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String formatAllDate(Date date) {
		return format(date, TIME_PATTERN);
	}

	/**
	 * 閺嶇厧绱￠崠鏍ㄦ闂傦拷  yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String formatNotFlagDate(Date date) {
		return format(date, DATE_PATTERN1);
	}
	
	/**
	 * 閺嶇厧绱￠崠鏍ㄦ闂傦拷  yyyyMMddHHmmss
	 * @param date
	 * @return
	 */
	public static String formatgetToDayTimestampDate(Date date) {
		return format(date, TIME_PATTERN1);
	}
	
	public static String getDateStr(String tag){
		Date date = getDate(tag);
		
		return format(date, DATE_PATTERN);
	}
	
	public static boolean isValidFormat(String tag, String value){
		String datePattern1 = "^\\d{8}$";
		String datetimePattern1 = "^\\d{8} \\d{4}$";
		
		if("date1".equals(tag)){
			return Pattern.matches(datePattern1, value);
		}else if("dtime1".equals(tag)){
			return Pattern.matches(datetimePattern1, value);
		}
		
		return true;
	}
	
	public static String formatTime(long timeVal){
		if(timeVal < 0){
			return null;
		}
		
		SimpleDateFormat f = new SimpleDateFormat(TIME_PATTERN);
		
		return f.format(new Date(timeVal));
	}
	
	public static String formatTime(long timeVal, String format){
		if(timeVal < 0){
			return null;
		}
		
		if(StringUtils.isBlank(format)){
			format = TIME_PATTERN;
		}
		
		SimpleDateFormat f = new SimpleDateFormat(format);
		
		return f.format(new Date(timeVal));
	}
	
	/**
	 * 閺冨爼妫挎潪顒�瀵叉稉鍝勭摟缁楋缚瑕�
	 * Date --> yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date){
		if(date == null){
			return null;
		}
		SimpleDateFormat f = new SimpleDateFormat(TIME_PATTERN);
		return f.format(date);
	}
	
	/**
	 * 閺冨爼妫挎潪顒�瀵叉稉鍝勭摟缁楋缚瑕�
	 * @param date
	 * @param nullValue 姒涙顓荤�涙顑佹稉锟�
	 * @return
	 */
	public static String formatTime(Date date, String nullValue){
		if(date == null){
			return nullValue;
		}
		SimpleDateFormat f = new SimpleDateFormat(TIME_PATTERN);
		
		return f.format(date);
	}
	
	public static int getHourNow(){
		Calendar cal = Calendar.getInstance();
		if(cal == null){
			return -1;
		}
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute(){
		Calendar cal = Calendar.getInstance();
		if(cal == null){
			return -1;
		}
		return cal.get(Calendar.MINUTE);
	}
	
	/**
	 * 閺冨爼妫块弽鐓庣础閸栨牔璐焟ysql閺冨爼妫块敍灞藉祮鐢箑宕熷鏇炲娇
	 * @param timeVal  閺冨爼妫縧ong閸婏拷
	 * @return
	 */
	public static String formatMysqlTime(long timeVal){
		if(timeVal < 0){
			return "'null'";
		}
		return formatTime(new Date(timeVal), "'null'", true);
	}

	/**
	 * 閺冦儲婀￠弽鐓庣础閸栨牔璐焟ysql閺冦儲婀￠敍灞藉祮鐢箑宕熷鏇炲娇
	 * @param date 
	 * @return
	 */
	public static String formatMysqlDate(Date date){
		return "'" + format(date, DATE_PATTERN) + "'";
	}

	
	public static String formatMysqlTime(Date date){
		return formatTime(date, "'null'", true);
	}
	
	/**
	 * 閺嶇厧绱￠崠鏍ㄦ闂傦拷
	 * @param date  閺冨爼妫�
	 * @param nullValue  姒涙顓婚崐锟�
	 * @param withQuote  閺勵垰鎯佺敮锕�宕熷鏇炲娇
	 * @return
	 */
	public static String formatTime(Date date, String nullValue, boolean withQuote){
		if(date == null){
			return nullValue;
		}
		SimpleDateFormat f = new SimpleDateFormat(TIME_PATTERN);
		if(withQuote){
			return "'" + f.format(date) + "'";
		}
		// implicit else
		return f.format(date);
	}
	
	/**
	 * 閺嶈宓佹导鐘插弳閻ㄥ嫭鐗稿蹇旂壐瀵繐瀵查弮鍫曟？
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format){
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(date);
	}
	
	public static String format(long time, String format){
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date date = new Date(time);
		return f.format(date);
	}
	
	/**
	 * 閺嶈宓佹导鐘插弳閻ㄥ嫬鐡х粭锔胯鏉堟挸鍤弮鍫曟？
	 * <pre>
	 * DateUtil.getDate(null) = new Date();
	 * DateUtil.getDate("now") = new Date();
	 * DateUtil.getDate("today") = truncate(new Date()); @see {@link DateUtil#truncate(Date)}
	 * DateUtil.getDate("yesterday") = getDate(truncate(new Date()), -1); @see {@link DateUtil#getDate(Date, int)}
	 * DateUtil.getDate("tomorrow") = getDate(truncate(new Date()), 1); @see {@link DateUtil#getDate(Date, int)}
	 * </pre>
	 * @param tag
	 * @return
	 */
	public static Date getDate(String tag){
		if(tag == null){
			return new Date();
		}
		if("now".equals(tag)){
			return new Date();
		}
		if("today".equals(tag)){
			return truncate(new Date());
		}
		if("yesterday".equals(tag)){
			return getDate(truncate(new Date()), -1);
		}
		if("tomorrow".equals(tag)){
			return getDate(truncate(new Date()), 1);
		}
		return new Date();
	}
	
	/**
	 * Get the Date of month 
	 * 
	 * @param date
	 * @param monthShift
	 * @param day	-N to N  (the Nth day, -Nth last N day, 0 = last day of month)
	 * @return
	 */
	public static Date getDateByMonth(Date date, int monthShift, int day){
		if(date == null){
			return null;	// NULL in, NULL out
		}
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		

		int currentMonth = cal.get(Calendar.MONTH);
		int monthDiff = (currentMonth - monthShift) % 12;
		int yearDiff = - (monthShift - monthDiff) / 12;
		
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - yearDiff);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - monthDiff);
		
		int maxDay = cal.getMaximum(Calendar.DATE);
		if(day == 0){
			day = maxDay;
		}else if(day < 0){
			if(day < -maxDay){
				day = -maxDay+1;
			}
			day = maxDay + day; 
			// System.out.println("MaxDay:" + maxDay + " Day: " + day);
		}else if(day > maxDay){
			day = maxDay;
		}
		
		cal.set(Calendar.DATE, day);
		
				
		return cal.getTime();
		
	}
	
	/**
	 * Get the Date of week 
	 * 
	 * @param date
	 * @param monthShift
	 * @param day	-N to N  (the Nth day, -Nth last N day, -1 = last day of month)
	 * @return
	 */
	public static Date getDateByWeek(Date date, int monthShift, int day){
		return null;
	}

	
	/**
	 * 瀵版鍩� date 閺冨爼妫� + hour鐏忓繑妞傞崥搴ｆ畱閺冨爼妫�
	 * @param date
	 * @param dayDiff
	 * @return
	 */
	public static Date addHour(Date date, int hour){
		long time = date.getTime();
		
		long newTime = time + (hour * TOTAL_SEC_PER_HOUR * MS);
		
		return new Date(newTime);
	}
	
	/**
	 * 瀵版鍩� date 閺冨爼妫� + minute閸掑棝鎸撻崥搴ｆ畱閺冨爼妫�
	 * @param date
	 * @param dayDiff
	 * @return
	 */
	public static Date addMinute(Date date, int minute){
		long time = date.getTime();
		
		long newTime = time + (minute * TOTAL_SEC_PER_MINUTE * MS);
		
		return new Date(newTime);
	}
	
	/**
	 * 瀵版鍩� date 閺冨爼妫� + dayDiff婢垛晛鎮楅惃鍕闂傦拷
	 * @param date
	 * @param dayDiff
	 * @return
	 */
	public static Date getDate(Date date, int dayDiff){
		long time = date.getTime();
		
		long newTime = time + (dayDiff * TOTAL_SEC_PER_DAY * MS);
		
		return new Date(newTime);
	}
	
	public static Date getDate(String dateStr, int dayDiff){
		Date date = parseTime(dateStr);
		if(date == null){
			return null;
		}
		
		return getDate(date, dayDiff);
	}
	
	/**
	 * 閹搭亜褰囬弮鍫曟？閿涘苯骞撻幒澶嬫閸掑棛顫�
	 * <pre>
	 * DateUtil.truncate(new Date()) = Wed Nov 04 00:00:00 CST 2015
	 * </pre>
	 * @param date
	 * @return
	 */
	public static Date truncate(Date date){
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		// Reset all time to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 閼惧嘲绶遍弽鍥у櫙閺嶇厧绱￠弮鍫曟？
	 * <pre>
	 * yyyy-MM-dd HH:mm:ss 
	 * </pre>
	 * @param dateStr
	 * @return
	 */
	public static Date parseTime(String dateStr) {
		return parse(dateStr, TIME_PATTERN);
	}
	
	/**
	 * 閼惧嘲绶遍弽鍥у櫙閺嶇厧绱￠弮銉︽埂
	 * <pre>
	 * yyyy-MM-dd 
	 * </pre>
	 * @param dateStr
	 * @return
	 */
	public static Date parse(String dateStr) {
		return parse(dateStr, DATE_PATTERN);
	}

	/**
	 * 閺嶈宓佺紒娆忕暰閻ㄥ嫭妞傞梻瀛樼壐瀵骏绱濋幎濠傜摟缁楋缚瑕嗛弽鐓庣础閹存劖妞傞梻瀵歌閸拷
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date parse(String dateStr, String format) {
		DateFormat formatter = new SimpleDateFormat(format);
		
        try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 閼惧嘲褰嘾ay婢垛晛澧犻惃鍕闂傦拷
	 * @param reportDate
	 * @param day
	 * @return
	 */
	public static Date dateSub(Date reportDate, int day) {
		if(day == 0){
			return reportDate; 
		}
		
		long value = reportDate.getTime();
		return new Date(value - day*TOTAL_MS_PER_DAY);
	}
	
	/**
	 * 閼惧嘲褰嘾ay婢垛晛鎮楅惃鍕闂傦拷
	 * @param reportDate
	 * @param day
	 * @return
	 */
	public static Date addDay(Date reportDate, int day) {
		if(day == 0){
			return reportDate; 
		}
		
		long value = reportDate.getTime();
		return new Date(value + day*TOTAL_MS_PER_DAY);
	}
	
	public static long getTimeMillis(Date date){
		if(date == null){
			return 0;
		}
		return date.getTime(); 
	}
	
	/**
	 * 鏉╂柨娲栧В蹇撱亯0閻愶拷0閸掑棴绱ｇ粔鎺旀畱閺冨爼妫跨捄婵囩垼閸戝棗鐔�閸戝棙妞傞梻瀵告畱濮ｎ偆顫楅弫锟�
	 */
	public static long getTodayTimeval(){
		return truncate(new Date()).getTime();
	}
	/**
	 * 鏉╂柨娲栬ぐ鎾炽亯閸掓壆顑囨禍灞姐亯0閻愮懓澧挎担娆戞畱濮ｎ偆顫楅弫锟�
	 * @return
	 */
	public static long getTodayOverMs() {
		return (truncate(new Date()).getTime() + TOTAL_MS_PER_DAY) -  System.currentTimeMillis();
	}
	
	/**
	 * 濡拷濞村绱堕崗銉︽闂傚瓨妲搁崥锔胯礋娴犲﹤銇�
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date) {
		long todayTime = truncate(new Date()).getTime();
		if(todayTime <= date.getTime() 
				&& date.getTime() < todayTime + TOTAL_MS_PER_DAY ) {
			return true;
		}
		return false;
	}
	
	public static int getHour(long warStartTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(warStartTime);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public static int getWeek() {
		Calendar cal = Calendar.getInstance();
		if(cal == null){
			return -1;
		}
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public static Date getWeekDayDate(int day){
		Calendar cal = Calendar.getInstance();
		if(cal==null){
			return null;
		}
		int weedDay = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DAY_OF_MONTH, day-weedDay);
		Date rs = truncate(cal.getTime());
		return rs;
	}
	
	public static int getDaysBetween(Date startDate, Date endDate) {  
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (int)((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));  
    }  
	
	/**
	 * 閸欐牕绶遍崡濠傚嬀閸撳秶娈戦弮銉︽埂
	 * @return
	 */
	public static String getHalfyearDate(String format){
		DateFormat formatter = new SimpleDateFormat(format);
		Calendar calendar=Calendar.getInstance();   
		calendar.set(Calendar.MONTH,calendar.get(Calendar.MONDAY)-6);
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * 閸掋倖鏌囬懗钘夋儊闁插秶鐤嗛敍灞肩炊閸忋儰绗傛稉锟藉▎锛勬畱閺冨爼妫块幋鍐叉嫲闁插秶鐤嗛弮鍫曟？閻愶拷
	 * @author chenzl
	 * @param date -- 閸掋倖鏌囬惃鍕闂傦拷
	 * @param hour -- 闁插秶鐤嗛惃鍕毈閺冿拷  閿涳拷 24鐏忓繑妞傞崚锟� 1-24
	 * @return true閿涙艾顦╂禍搴㈡О婢垛晛褰叉禒銉╁櫢缂冿拷
	 */
	public static boolean canReset(Date date , int hour) {
		long resetTime = DateUtil.truncate(new Date()).getTime() + hour* TOTAL_SEC_PER_HOUR * MS;//闁插秶鐤嗛弮鍫曟？閻愶拷
		long currTime = System.currentTimeMillis();//瑜版挸澧犻弮鍫曟？
		long zeroTime = 0l;//娴犲﹤銇夐惃锟�"闂嗗墎鍋ｉ弮鍫曟？閻愶拷"

		if(currTime >= resetTime){//瀹歌尙绮℃潻鍥︾啊娴犲﹤銇夐惃鍕櫢缂冾喗妞傞梻瀵稿仯
			zeroTime = resetTime;
		}else{//鏉╂ɑ鐥呴張澶庣箖娴犲﹤銇夐惃鍕櫢缂冾喗妞傞梻瀵稿仯閿涘矁顩﹂悽銊ょ瑐娑擄拷娑擃亪鍣哥純顔芥闂傚鍋�
			zeroTime = resetTime - TOTAL_MS_PER_DAY;
		}
		
		if(date.getTime() >= zeroTime){//婢堆傜艾闁插秶鐤嗛弮鍫曟？閿涘矁銆冪粈鍝勫嚒缂佸繘鍣哥純顔跨箖娴滐拷 娑撳秷鍏橀柌宥囩枂
			return false;
		}
		
		return true;
	}
	
	/**
	 * 閸掋倖鏌囨稉銈勯嚋閺冨爼妫块弰顖氭儊婢跺嫪绨崥灞肩婢讹拷
	 * @author chenzl
	 * @param date1 -- 閸掋倖鏌囬惃鍕闂傦拷1
	 * @param date2 -- 閸掋倖鏌囬惃鍕闂傦拷2
	 * @param hour -- 闁插秶鐤嗛惃鍕毈閺冿拷  閿涳拷 24鐏忓繑妞傞崚锟� 1-24
	 * @return true閿涙艾顦╂禍搴℃倱娑擄拷婢讹拷
	 */
	public static boolean isSameDay1(Date date1 , Date date2, int hour) {
		long time1 = date1.getTime();
		long zero1 = DateUtil.truncate(date1).getTime() + hour * TOTAL_SEC_PER_HOUR * MS;//缁楊兛绔存稉顏堟祩閻愯妞傞梻锟�
		if(zero1 > time1){
			zero1 = zero1 - 24 * TOTAL_SEC_PER_HOUR * MS;
		}
		
		long time2 = date2.getTime();
		long zero2 = DateUtil.truncate(date2).getTime() + hour * TOTAL_SEC_PER_HOUR * MS;//缁楊兛绔存稉顏堟祩閻愯妞傞梻锟�
		if(zero2 > time2){
			zero2 = zero2 - 24 * TOTAL_SEC_PER_HOUR * MS;
		}
		
		if(zero1 == zero2){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 閸掋倖鏌囨稉銈勯嚋閺冨爼妫块弰顖氭儊婢跺嫪绨崥灞肩婢讹拷
	 * @author chenzl
	 * @param date1 -- 閸掋倖鏌囬惃鍕闂傦拷1
	 * @param date2 -- 閸掋倖鏌囬惃鍕闂傦拷2
	 * @param hour -- 闁插秶鐤嗛惃鍕毈閺冿拷  閿涳拷 24鐏忓繑妞傞崚锟� 1-24
	 * @return true閿涙艾顦╂禍搴℃倱娑擄拷婢讹拷
	 */
	public static boolean isSameDay2(long time1, long time2, int hour) {
		long zero1 = DateUtil.truncate(new Date(time1)).getTime() + hour * TOTAL_SEC_PER_HOUR * MS;//缁楊兛绔存稉顏堟祩閻愯妞傞梻锟�
		if(zero1 > time1){
			zero1 = zero1 - 24 * TOTAL_SEC_PER_HOUR * MS;
		}
		
		long zero2 = DateUtil.truncate(new Date(time2)).getTime() + hour * TOTAL_SEC_PER_HOUR * MS;//缁楊兛绔存稉顏堟祩閻愯妞傞梻锟�
		if(zero2 > time2){
			zero2 = zero2 - 24 * TOTAL_SEC_PER_HOUR * MS;
		}
		
		if(zero1 == zero2){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 閼惧嘲绶遍垾婊堟祩閻愬厜锟芥繃妞傞梻锟�
	 * @author chenzl
	 * @param time -- 瑜版挸澧犻弮鍫曟？
	 * @param hour -- 闁插秶鐤嗛惃鍕毈閺冿拷  閿涳拷 24鐏忓繑妞傞崚锟� 1-24
	 * @return 閳ユ粓娴傞悙瑙勬闂傜补锟斤拷
	 */
	public static long getZeroTime(long time, int hour) {
		long zero = DateUtil.truncate(new Date(time)).getTime() + hour * TOTAL_SEC_PER_HOUR * MS;//缁楊兛绔存稉顏堟祩閻愯妞傞梻锟�
		if(zero > time){
			zero = zero - 24 * TOTAL_SEC_PER_HOUR * MS;
		}
	
		return zero;
	}
	
	/**
	 * 閸掋倖鏌� date 閺冨爼妫� 娑撳骸缍嬫径鈺冩畱hour鐏忓繑妞俶inute閸掑棝鎸搒econd缁夛拷 鏉╂瑤閲滈弮鍫曟？  閺勵垰鎯佹潏鎯у煂閸欘垯浜掗崚閿嬫煀
	 * @param date -- 閸掋倖鏌囬惃鍕闂傦拷
	 * @param hour -- 闁插秶鐤嗛惃锟� 鐏忓繑妞�  閿涳拷 24鐏忓繑妞傞崚锟� 0-23
	 * @param minute -- 闁插秶鐤嗛惃鍕瀻闁界噦绱� 0-59
	 * @param second -- 闁插秶鐤嗛惃鍕潡闁斤拷; 0-59
	 * @return
	 */
	public static boolean isMayReset(Date date , int hour, int minute, int second) {
		Date truncate = truncate(new Date());
		long resetTime = truncate.getTime() + hour* TOTAL_SEC_PER_HOUR * MS + minute * TOTAL_SEC_PER_MINUTE * MS + second * MS;
		long currTime = System.currentTimeMillis();//瑜版挸澧犻弮鍫曟？
		long zeroTime = 0l;//娴犲﹤銇夐惃锟�"闂嗗墎鍋ｉ弮鍫曟？閻愶拷"

		if(currTime >= resetTime){//瀹歌尙绮℃潻鍥︾啊娴犲﹤銇夐惃鍕櫢缂冾喗妞傞梻瀵稿仯
			zeroTime = resetTime;
		}else{//鏉╂ɑ鐥呴張澶庣箖娴犲﹤銇夐惃鍕櫢缂冾喗妞傞梻瀵稿仯閿涘矁顩﹂悽銊ょ瑐娑擄拷娑擃亪鍣哥純顔芥闂傚鍋�
			zeroTime = resetTime - TOTAL_MS_PER_DAY;
		}
		
		if(date.getTime() >= zeroTime){//婢堆傜艾闁插秶鐤嗛弮鍫曟？閿涘矁銆冪粈鍝勫嚒缂佸繘鍣哥純顔跨箖娴滐拷 娑撳秷鍏橀柌宥囩枂
			return false;
		}
		
		return true;
	}
	
	/**
	 * 鐠佸墽鐤嗛張顏囩箖閺冨墎娈戦弮銉︽埂閿涘奔绔存稉顏呮箑娑擃厾娈戦弻鎰亯
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Date setNonageingToMonthDay(int day, int hour, int minute, int second){
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, second);

		Calendar nowDate = Calendar.getInstance();
		if(date.getTimeInMillis() < nowDate.getTimeInMillis()){
			date.add(Calendar.MONTH, 1);//娑撳閲滈張鍫㈡畱閺冨爼妫�
		}
		
		return date.getTime();
	}
	
	/**
	 * 鐠佸墽鐤嗛張顏囩箖閺冨墎娈戦弮銉︽埂閿涘奔绔存稉顏勬噯娑擃厾娈戦弻鎰亯
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Date setNonageingToWeekDay(int day, int hour, int minute, int second){
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_WEEK, day);
		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, second);

		Calendar nowDate = Calendar.getInstance();
		if(date.getTimeInMillis() < nowDate.getTimeInMillis()){
			date.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);//娑撳閲滈崨銊ф畱閺冨爼妫�
		}
		
		return date.getTime();
	}
	
	/**
	 * 鐠佸墽鐤嗘稉锟芥稉顏勬噯娑擃厾娈戦弻鎰亯
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Date setWeekDay(int day, int hour, int minute, int second){
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_WEEK, day);
		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, second);
		return date.getTime();
	}
	
	/**
	 * 閸掋倖鏌嘾ate 閺冨爼妫� 娑擄拷 閺堫剙鎳嗛弰鐔告埂day 鏉╂瑤閲滈弮鍫曟？  閺勵垰鎯佹潏鎯у煂閸掗攱鏌�
	 * @param date -- 閸掋倖鏌囬惃鍕闂傦拷
	 * @param day -- 闁插秶鐤嗛惃鍕噯婢垛晜妞傞梻杈剧窗娑擄拷閸涳拷7婢讹拷  閺勭喐婀℃径鈺�璐熷В蹇撴噯缁楊兛绔存径锟�
	 * @param hour -- 闁插秶鐤嗛惃锟� 鐏忓繑妞�  閿涳拷 24鐏忓繑妞傞崚锟� 0-23
	 * @return
	 */
	public static boolean isMayResetForWeek(Date date, int day, int hour) {
		long newTime = date.getTime() - (day - 1) * TOTAL_MS_PER_DAY - hour * TOTAL_SEC_PER_HOUR * MS;
		if(isSameWeek(new Date(newTime), new Date())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 閸掋倖鏌嘾ate 閺冨爼妫� 娑擄拷 閺堫剙鎳嗛弰鐔告埂day 鏉╂瑤閲滈弮鍫曟？  閺勵垰鎯佹潏鎯у煂閸掗攱鏌�
	 * @param date -- 閸掋倖鏌囬惃鍕闂傦拷
	 * @param day -- 闁插秶鐤嗛惃鍕噯婢垛晜妞傞梻杈剧窗娑擄拷閸涳拷7婢讹拷  閺勭喐婀℃径鈺�璐熷В蹇撴噯缁楊兛绔存径锟�
	 * @param hour -- 闁插秶鐤嗛惃锟� 鐏忓繑妞�  閿涳拷 24鐏忓繑妞傞崚锟� 0-23
	 * @param minute -- 闁插秶鐤嗛惃锟� 鐏忓繑妞�  閿涙艾鍨庨柦锟�
	 * @return
	 */
	public static boolean isMayResetForWeek(Date date, int day, int hour, int minute) {
		long newTime = date.getTime() - (day - 1) * TOTAL_MS_PER_DAY - (hour * TOTAL_SEC_PER_HOUR * MS) - (minute * TOTAL_SEC_PER_MINUTE * MS);
		if(isSameWeek(new Date(newTime), new Date())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 閼惧嘲褰囨导鐘插弳閻ㄥ嫭妞傞梻锟� + second缁夛拷  - 閻滄澘婀惃鍕闂傦拷 =  閸撯晙绗呴惃鍕潡閺侊拷 閿涳拷 鐠愮喐鏆熼弮璁圭窗 鏉╂柨娲�0
	 * date娑撶皠ull閺冭绱濇潻鏂挎礀0
	 * @param date
	 * @param minute
	 * @return
	 */
	public static long getDiffSecond(Date date , int second) {
		if(date == null) {
			return 0;
		}
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(date);
        fromCalendar.set(Calendar.SECOND ,  second + fromCalendar.get(Calendar.SECOND));
        long ms = fromCalendar.getTime().getTime() -  new Date().getTime();
        if(ms < 0)
        	ms = 0;
        else
        	ms = ms/MS;
		return ms;
	}
	
	/**
	 * 閼惧嘲褰囨导鐘插弳閻ㄥ嫭妞傞梻锟� 閼凤拷 瑜版挸澧犻弮鍫曟？閼版妞傜粔鎺撴殶
	 * @param time
	 * @return
	 */
	public static long getCostSecond(long time){
		 Calendar fromCalendar = Calendar.getInstance();  
	     fromCalendar.setTimeInMillis(time);
	     long ms = (new Date().getTime() - fromCalendar.getTime().getTime()) / MS;
	     return ms;
	}
	
	/**
	 * 閸掋倖鏌囬弰顖氭儊閸︺劌鎮撴稉锟介崨锟�
	 * @param d1
	 * @param d2
	 * @return (d1=null or d2= null) return false
	 */
	public static boolean isSameWeek(Date d1, Date d2) {
		
		if (d1 == null) return false;
		if (d2 == null) return false;
		
		Calendar calendar=Calendar.getInstance();   
		calendar.setTime(d1);
		int y1 = calendar.get(Calendar.YEAR);
		int m1 = calendar.get(Calendar.WEEK_OF_YEAR);
		
		calendar.setTime(d2);
		int y2 = calendar.get(Calendar.YEAR);
		int m2 = calendar.get(Calendar.WEEK_OF_YEAR);
		
		return y1==y2 && m1==m2;
		
	}
	/**
	 * 閼惧嘲褰�2娑擃亝妞傞梻瀵告纯閹恒儳娈戦弫鏉戠毈閺冭埖鏆�
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long getHoursBeteewn(Date d1, Date d2) {
		long t1 = d1.getTime();
		long t2 = d2.getTime();
		if (t1 > t2) {
			return (t1-t2)/(1000 * 60 * 60);
		} else {
			return (t2-t1)/(1000 * 60 * 60);
		}
	}
	
	static TimeZone timeZone = Calendar.getInstance().getTimeZone();//閺堝秴濮熼崳銊╃帛鐠併倖妞傞崠锟�
	/**
	 * 閸滃苯顓归幋椋庮伂閻ㄥ嫮绮烘稉锟界紒婵嗩嚠閺冨爼妫挎导鐘伙拷浣圭垼閸戯拷
	 */
	public static long getTimeInGMT0(long dateTime){
		long ti = timeZone.getOffset(dateTime) + dateTime;
//		System.out.println(dateTime + "-->" + ti + ",sub=" + (ti - dateTime));
		return ti;
	}
	
	/**
	 * 閺勵垰鎯侀崷銊﹀瘹鐎规碍妞傞梻瀛橆唽閸愶拷
	 * @param start
	 * @param end
	 * @param now
	 * @return
	 */
	public static boolean isInTimeRange(Date start, Date end, Date now){
		return (start.getTime() <= now.getTime() && now.getTime() <= end.getTime());
	}
	
	/**
	 * * 鐏忓棙妫╅張鐔绘祮閸栨牗鍨� oracle 閻拷 to_date('xxx','xxx') 閺嶇厧绱�
	 * 
	 * @param d
	 *            閺冦儲婀�
	 * @param format
	 *            閺冦儲婀￠弽鐓庣础閿涘奔绶ユ俊锟� "yyyy-MM-dd HH:mm"
	 * @param hqlFormat
	 *            oracle閻ㄥ嫭妫╅張鐔哥壐瀵骏绱濇笟瀣洤閿涳拷"yyyy-mm-dd hh24:mi"
	 * @return
	 */

	public static String toDate(final Date d, final String format, final String hqlFormat) {
		StringBuffer bf = new StringBuffer();
		bf.append("to_date('");
		bf.append(format(d, format));
		bf.append("','");
		bf.append(hqlFormat);
		bf.append("')");
		return bf.toString();
	}

	/**
	 * * 鐏忓棙妫╅張鐔绘祮閸栨牗鍨� oracle 閻拷 to_date('xxx','xxx') 閺嶇厧绱�
	 * 
	 * @param d
	 *            閺冦儲婀�
	 * 
	 * @param hqlFormat
	 *            oracle閻ㄥ嫭妫╅張鐔哥壐瀵骏绱濇笟瀣洤閿涳拷"yyyy-mm-dd hh24:mi"
	 * @return
	 */

	public static String toDate(final String date, final String hqlFormat) {
		StringBuffer bf = new StringBuffer();
		bf.append("to_date('");
		bf.append(date);
		bf.append("','");
		bf.append(hqlFormat);
		bf.append("')");
		return bf.toString();
	}
	
	/**
	 * * 鐏忓棙妫╅張鐔绘祮閸栨牗鍨� oracle 閻拷 to_date('xxx','xxx') 閺嶇厧绱�
	 * 
	 * @param d
	 *            閺冦儲婀�
	 * 
	 * @param hqlFormat
	 *            oracle閻ㄥ嫭妫╅張鐔哥壐瀵骏绱濇笟瀣洤閿涳拷"yyyy-mm-dd hh24:mi"
	 *            
	 * @param i
	 *            閸旂姳绔存径鈺傚灗閼板懎鍣烘稉锟芥径锟�  婵″偊绱癷=-1;i=1         
	 * @return
	 */

	public static String toDate(final String date, final String hqlFormat,final int i) {
		StringBuffer bf = new StringBuffer();
		bf.append("to_date('");
		bf.append(date);
		bf.append("','");
		bf.append(hqlFormat);
		bf.append("')+"+i);
		return bf.toString();
	}
	
	/**
	 *
	 * 濮ｆ棁绶濇稉銈勯嚋閺冦儲婀￠惄绋挎▕闁棄鐨径鈹匡拷锟� 
	 * 娑撱倓閲滈弮銉︽埂閿涘潊,b閿涘娴夊顔笺亯閺侊拷 閺勵垰鎯侀弰顖ょ礄C 閿涘銇�/閸栧懎鎯堥妴锟� 婵″倹鐏夐弰顖氭皑鏉╂柨娲杢rue 閵嗭拷
	 * 濮ｆ柨顩�2011-08-31 5:20:30  閸滐拷 2011-09-01 5:19:20 娴犳牔婊戦惄鎼佹閻ㄥ嫪绗夐弰顖欑婢讹拷 
	 * 閸欏牆顩�2011-08-31 5:20:30  閸滐拷 2011-09-01 5:21:20 娴犳牔婊戦惄鎼佹閻ㄥ嫭妲告稉锟芥径锟�
	 */
	public static Boolean getPoorSeconds(Date a,Date b,int c){
	    Calendar timea = Calendar.getInstance();
	    Calendar timeb = Calendar.getInstance();
	    timea.setTime(a);
	    timeb.setTime(b);
	    return (timea.getTimeInMillis() - timeb.getTimeInMillis())/TOTAL_MS_PER_DAY>=c;
	}
	/**
	 * 瀵版鍩岄弮銉︽埂閸戝繋绔存稉顏呮箑閺冦儲婀�
	 * @return
	 */
	public static String getAfterMonthDate(Date date){
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);//閺嶇厧绱￠崠鏍ь嚠鐠烇拷
		  Calendar calendar = Calendar.getInstance();//閺冦儱宸荤�电钖�
		  calendar.setTime(date);//鐠佸墽鐤嗚ぐ鎾冲閺冦儲婀�
		  calendar.add(Calendar.MONTH, -1);//閺堝牅鍞ら崙蹇庣
		  return sdf.format(calendar.getTime());
	}
	
	/**
	 * 閼惧嘲绶遍幐鍥х暰閺冦儲婀￠幍锟介崷銊ф畱閺勭喐婀￠崗锟�
	 * @param time
	 * @return
	 */
	public static String convertWeekByDate(Date time) {  
        SimpleDateFormat sdf=new SimpleDateFormat(DATE_PATTERN);
        Calendar cal = Calendar.getInstance();  
        cal.setTime(time);  
        //閸掋倖鏌囩憰浣筋吀缁犳娈戦弮銉︽埂閺勵垰鎯侀弰顖氭噯閺冦儻绱濇俊鍌涚亯閺勵垰鍨崙蹇庣婢垛晞顓哥粻妤�鎳嗛崗顓犳畱閿涘苯鎯侀崚娆庣窗閸戞椽妫舵０姗堢礉鐠侊紕鐣婚崚棰佺瑓娑擄拷閸涖劌骞撴禍锟�  
        //閼惧嘲绶辫ぐ鎾冲閺冦儲婀￠弰顖欑娑擃亝妲﹂張鐔烘畱缁楊剙鍤戞径锟�  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(1 == dayWeek) {  
           cal.add(Calendar.DAY_OF_MONTH, -1);  
        }  
        //鐠佸墽鐤嗘稉锟芥稉顏呮Е閺堢喓娈戠粭顑跨婢垛晪绱濋幐澶夎厬閸ョ晫娈戞稊鐘冲劵娑擄拷娑擃亝妲﹂張鐔烘畱缁楊兛绔存径鈺傛Ц閺勭喐婀℃稉锟�  
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //閼惧嘲绶辫ぐ鎾冲閺冦儲婀￠弰顖欑娑擃亝妲﹂張鐔烘畱缁楊剙鍤戞径锟�  
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //閺嶈宓侀弮銉ュ坊閻ㄥ嫯顫夐崚娆欑礉缂佹瑥缍嬮崜宥嗘）閺堢喎鍣洪崢缁樻Е閺堢喎鍤戞稉搴濈娑擃亝妲﹂張鐔侯儑娑擄拷婢垛晝娈戝顔硷拷锟�   
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
        //閼惧嘲褰囬幐鍥х暰閺冦儲婀￠惃鍕閸︺劌鎳嗛惃鍕Е閺堢喎鍙�
        cal.add(Calendar.DATE, 5); 
        String imptimeEnd = sdf.format(cal.getTime());  
        //System.out.println("閹碉拷閸︺劌鎳嗛弰鐔告埂閺冦儳娈戦弮銉︽埂閿涳拷"+imptimeEnd);  
        return imptimeEnd;
    }  
	
	/**
	 * 
	 * @param datea 瀵拷婵妞傞梻锟�
	 * @param dateb 缂佹挻娼弮鍫曟？
	 * 濮ｆ棁绶濇稉銈勯嚋閺冦儲婀� 閻╃妯婃径姘毌閺冨爼鏆� 缁墽鈥橀崚鏉跨毈閺冭泛鎷伴崚鍡涙寭
	 * 濮ｆ柨顩�2014-02-19 12:00:00  閸滐拷 2014-02-20 12:00:00 娴犳牔婊戦惄鎼佹24h
	 * 閸欏牆顩�2014-02-19 12:00:00  閸滐拷 2014-02-20 13:30:00娴犳牔婊戦惄鎼佹25h30m
	 * @return  
	 * @throws ParseException 
	 */
	public static String getTimeDifference(String datea,String dateb) throws ParseException{
	
		SimpleDateFormat sf  = new SimpleDateFormat(TIME_PATTERN);

		Date d1 = sf.parse(datea);

		Date d2 = sf.parse(dateb);

		long stamp = (d2.getTime() - d1.getTime())/1000;
		int MM = (int) stamp / 60; // 閸忚精顓搁崚鍡涙寭閺侊拷
		int hh = (int) stamp / 3600; // 閸忚精顓哥亸蹇旀閺侊拷
		int mm =  (int)MM - hh * 60 ; 

		return hh + " 鐏忓繑妞� " + mm + " 閸掑棝鎸�";
	}
	
	/**
	 * 閼惧嘲褰囬弮鍫曟？瀹割喖绱�
	 * @param date1
	 * @param date2
	 * @return date1娑撳穱ate2閻ㄥ嫭妞傞梻鏉戞▕瀵偊绱欓崚鍡涙寭閿涳拷
	 */
	public static long getTimeDifference(Date date1, Date date2) {
		
		return Math.abs((date2.getTime() - date1.getTime())) / (TOTAL_SEC_PER_MINUTE * MS);
		
	}
	
	public static final void main(String argv[]){
		Date date = new Date();
		boolean mayResetForWeek = isMayResetForWeek(new Date(date.getTime() - TOTAL_MS_PER_DAY ), 3, 18);
		System.out.println(mayResetForWeek);
		
		System.out.println(getTimeInGMT0(date.getTime()));
		System.out.println(date.getTime());
		
//		System.out.println(DateUtil.truncate(new Date()));
//		/*Date a = DateUtil.getWeekDayDate(Calendar.SUNDAY);
//		System.out.println(a);*/
//		
//		System.out.println(formatTime(truncate(new Date())));
//		
//		Date startDate = DateUtil.parseTime("2014-08-01 09:00:00");
//		
//		Date endDate = DateUtil.parseTime("2014-07-28 09:00:00");
//		
//		Calendar calendar=Calendar.getInstance();   
//		calendar.setTime(startDate);
//		System.out.println(calendar.get(Calendar.YEAR));
//		System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
//		System.out.println("=================");
//		calendar.setTime(endDate);
//		System.out.println(calendar.get(Calendar.YEAR));
//		System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
//		
//		System.out.println(isSameWeek(startDate, endDate));
//		
//		System.out.println("==="+DateUtil.addMinute(new Date(), 11));
//		System.out.println(DateUtils.truncate(startDate, Calendar.WEEK_OF_YEAR));
		
        
//        System.out.println(getDiffSecond(startDate, 46));
//        System.out.println(TOTAL_SEC_PER_HOUR);
//		System.out.println(isMayReset(startDate, 19));
//		Date endDate = DateUtil.parseTime("2011-09-23 10:01:30");
//		
//		int days = DateUtil.getDaysBetween(startDate, endDate);
//		System.out.println("days="+days);
//		
//		int nowProsperity = 100;
//		for(int i=1;i<=days;i++){
//			nowProsperity-=nowProsperity/10;
//		}
//		System.out.println(nowProsperity);
//		System.out.println(isToday(new Date()));
//		System.out.println(truncate(new Date()).getTime());
//		System.out.println(new Date().getTime());
//		System.out.println(getHourNow());
	}
	
	/**
	 * 閼惧嘲褰囬弽鐓庣础閺冨爼妫� HHMMss
	 * @param time 000000
	 * @return
	 */
	public static long getTodayFormatTime(int time) {
		return getTodayTimeval() + (time / 10000) * TOTAL_SEC_PER_HOUR * MS + ((time / 100) % 100) * TOTAL_SEC_PER_MINUTE * MS + time % 100 * MS;
	}
	
}
