package idevgame.meteor.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 閺冨爼妫块弮銉︽埂瀹搞儱鍙跨猾锟�
 * 
 * 
 */
public class TimeDateUtil {
	private static Logger logger = LoggerFactory.getLogger(Logger.class);
	public static DateFormat FORMAT_D = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat FORMAT_YYMMDD = new SimpleDateFormat("yyyyMMdd");
	public static DateFormat FORMAT_YYMMDD_HHMMSS = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static DateFormat FORMAT_HHMMSS = new SimpleDateFormat("HH:mm:ss");
	public static DateFormat FORMAT_QQ_UPLOAD_TIME = new SimpleDateFormat(
			"yyyy-MM-dd%20HH:mm:ss");
	public static DateFormat FORMAT_YYNIANMMYUEDDRI_HHSHIMMFEN = new SimpleDateFormat(
			"yyyy楠炵M閺堝潐d閺冾檸H閺冪m閸掞拷");
	public static DateFormat FORMAT_YY_MM_DD_HH_MM = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
	public static DateFormat FORMAT_YY_MM_DD = new SimpleDateFormat("yyyy_MM_dd");
	/**
	 * 娑擄拷婢垛晝娈戦弮鍫曟？
	 */
	public static final long ONEDAY = 3600 * 24 * 1000;
	public static final long ONEHOUR = 3600 * 1000;

	public static final long ONEDAYINT = 3600 * 24;

	/**
	 * 12娑擃亜鐨弮锟�
	 */
	public static final long TWELVE = 3600 * 12 * 1000;
	/**
	 * 閸掑棝鎸�
	 */
	public static final long ONEMIN = 60 * 1000;

	/**
	 * 缁夛拷
	 */
	public static final long ONESECOND = 1000;

	/**
	 * 娑撳啫銇夐弮鍫曟？
	 */
	public static final long SEVEN_DAYS_TIME = 7 * 3600 * 24 * 1000;

	/**
	 * 5娑擃亜鐨弮锟�
	 */
	public static final long FIVE = 3600 * 5 * 1000;

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺堝牅鍞�
	 * 
	 * @return
	 */
	public static int getCurrentMonth(){
		Calendar calendar = Calendar.getInstance();  
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块幋锟�(濮ｎ偆顫�)
	 * 
	 * @name getCurrentTime
	 * @return
	 */
	public static long getCurrentTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块幋锟�(閸掞拷)
	 * 
	 * @name getCurrentTime
	 * @return
	 */
	public static long getCurrentTimeMinute() {
		return Calendar.getInstance().getTimeInMillis() / 60000;
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块幋锟�(閺冿拷)
	 * 
	 * @name getCurrentTime
	 * @return
	 */
	public static long getCurrentTimehour() {
		return Calendar.getInstance().getTimeInMillis() / 3600000;
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块幋锟�(缁夛拷)
	 * 
	 * @name getCurrentTime
	 * @return
	 */
	public static int getCurrentTimeInt() {
		return (int) (Calendar.getInstance().getTimeInMillis() / 1000);
	}

	/**
	 * 閼惧嘲褰囨禒濠傘亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧瞣ng缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getTodayZeroTime() {
		long now = Calendar.getInstance().getTimeInMillis();
		return now - (getHour() * ONEHOUR + now % ONEHOUR);
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块敍鍫濆瀻閿涳拷
	 * 
	 * @return xx楠炵x閺堝澑x閺冾櫨x閺冪x閸掞拷
	 */
	public static String getLogDate() {
		long now = Calendar.getInstance().getTimeInMillis();
		return FORMAT_YYNIANMMYUEDDRI_HHSHIMMFEN.format(now);
	}

	/**
	 * 閼惧嘲褰囬張顒�鎳嗛梿鑸垫閺冨爼妫�(閸涖劋绔�)
	 * 
	 * @return
	 */
	public static long getWeekZeroTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囬張顒�鎳嗛弰鐔告埂閸戠姷娈戦梿鑸垫閺冨爼妫�(1=SunDay)
	 * 
	 * @param week
	 * @return
	 */
	public static long getWeekZeroTime(int week) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, week);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囨稉瀣噯閺勭喐婀￠崙鐘垫畱闂嗗墎鍋ｉ弮鍫曟？
	 * 
	 * @param week 1閺勵垰鎳嗛弮锟�
	 * @return
	 */
	public static long getNextWeekZeroTime(int week) {
		return getWeekZeroTime(week) + SEVEN_DAYS_TIME;
	}
	
	/**
	 * 閼惧嘲褰囬張鍫熸箑鎼存洘妞傞梻锟�
	 * 
	 * @param week
	 * @return
	 */
	public static long getMonthEndTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囬張鍫濆灥0閻愯妞傞梻锟�
	 * 
	 * @param week
	 * @return
	 */
	public static long getMonthZeroTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囬弰搴°亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧瞣ng缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getTomorrowZeroTime() {
		long zeroTime = getTodayZeroTime();
		zeroTime += ONEDAY;
		return zeroTime;
	}

	/**
	 * 閼惧嘲褰囬弰搴°亯娴滄梻鍋ｉ惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧瞣ng缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getTomorrowFiveTime() {
		long zeroTime = getTodayZeroTime();
		zeroTime += ONEDAY + FIVE;
		return zeroTime;
	}

	/**
	 * 閼惧嘲褰囬弰搴°亯閸忣厾鍋ｉ惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧瞣ng缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getTomorrowSixTime(){
		long zeroTime = getTodayZeroTime();
		zeroTime += ONEDAY + ONEHOUR * 6;
		return zeroTime;
	}
	
	/**
	 * 閼惧嘲褰囬弰銊ャ亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧痭t缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static int getYesterdayZeroTime() {
		long zeroTime = getTodayZeroTime();
		zeroTime = zeroTime - ONEDAY;
		return (int) (zeroTime / 1000);
	}

	/**
	 * 閼惧嘲褰囬弰銊ャ亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧瞣ng缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getYesterdayZeroTimeLong() {
		long zeroTime = getTodayZeroTime();
		zeroTime = zeroTime - ONEDAY;
		return zeroTime;
	}

	/**
	 * 閼惧嘲褰囨禒缁樺壈娑擄拷娑擃亝妞傞梻瀵稿仯瑜版挸銇夐惃鍕祩閺冭埖妞傞梻锟�
	 * 
	 * @param theDay
	 * @return
	 */
	public static long getSomedayZeroTime(long theDay) {
		return theDay - (getHour(theDay) * ONEHOUR + theDay % ONEHOUR);
	}

	/**
	 * 閼惧嘲褰囨禒缁樺壈娑擄拷娑擃亝妞傞梻瀵稿仯瑜版挸銇夐弰顖滎儑閸戠姴銇�
	 * 
	 * @param theDay
	 * @return
	 */
	public static int getWillDayTime(long theDay) {
		return (int) ((getTodayZeroTime() - getSomedayZeroTime(theDay)) / ONEDAY) + 1;
	}

	/**
	 * 閼惧嘲褰囨禒濠傘亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧痭t缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static int getTodayZeroTimeReturnInt() {
		long todayZeroTime = getTodayZeroTime();
		return (int) (todayZeroTime / 1000);
	}

	/**
	 * 閼惧嘲褰囬弰搴°亯闂嗚埖妞傞惃鍕闂傦拷
	 * 
	 * @return 闂嗚埖妞傛禒顧痭t缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static int getTomorrowZeroTimeReturnInt() {
		long tomorrowZeroTime = getTomorrowZeroTime();
		return (int) (tomorrowZeroTime / 1000);
	}

	private static int getHour(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static String time2String(long time) {
		return FORMAT_YYMMDD_HHMMSS.format(new Date(time));
	}

	public static String qqUploadTime() {
		return FORMAT_QQ_UPLOAD_TIME.format(new Date(TimeDateUtil
				.getCurrentTime()));
	}

	public static String qqUploadTime(long time) {
		return FORMAT_QQ_UPLOAD_TIME.format(new Date(time));
	}

	public static String time2String(long time, DateFormat format) {
		Date date = new Date(time);
		String printData = format.format(date);
		return printData;
	}

	/**
	 * 閼惧嘲褰囬弮鍫曟？(閺冿拷 娑撳搫宕熸担宥忕窗(閺嶇厧绱�:20130601)閿涳拷
	 * 
	 * @param time
	 * @return
	 */
	public static int getTimeInt(long time) {
		return Integer.valueOf(FORMAT_YYMMDD.format(new Date(time)));
	}

	/**
	 * 鐎涙顑佹稉鑼舵祮閹广垺鍨氶弮銉︽埂閺嶇厧绱�
	 * 
	 * @param dateString
	 *            瀵板懓娴嗛幑銏㈡畱閺冦儲婀＄�涙顑佹稉锟�
	 * @param datePattern
	 *            閺冦儲婀￠弽鐓庣础
	 * @return 鏉烆剚宕查崥搴ｆ畱閺冦儲婀�
	 */
	public static Date string2Date(String dateString, DateFormat format) {
		if (dateString == null || dateString.trim().length() == 0) {
			return new Date();
		}
		try {
			return format.parse(dateString);
		} catch (Exception e) {
			logger.error("鐎涙顑佹稉鍙夋闂傚瓨鐗稿蹇涙晩鐠囷拷:" + dateString);
		}
		return new Date();
	}

	/**
	 * 閼惧嘲褰囨稉瀣╅嚋鐏忓繑妞傞惃鍕殻閻愯妞傞梻锟�
	 * 
	 * @return
	 */
	public static long getNextHourTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR, 1);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囨稉瀣╅嚋鐏忓繑妞傛径姘毌閸掑棝鎸撻弫瀛樻闂傦拷
	 * 
	 * @return
	 */
	public static long getNextMinTime(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR, 1);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囨稉瀣╅嚋瑜版挸澧犳径姘毌閸掑棝鎸撻弫瀛樻闂傦拷
	 * 
	 * @return
	 */
	public static long getMinTime(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 閼惧嘲褰囬幐鍥х暰閺冨爼妫块崷銊ョ秼楠炲娈戦張鍫滃敜
	 * 
	 * @return
	 */
	public static int getMonthOfYear(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * 閼惧嘲褰囬幐鍥х暰閺冨爼妫块弰顖氱秼閺堝牏娈戠粭顒�鍤戞径锟�
	 * 
	 * @return
	 */
	public static int getDayOfMonth(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 閼惧嘲褰囬弰鐔告埂閸戯拷(1=閸涖劍妫�)
	 * 
	 * @return
	 */
	public static int getDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		int result = calendar.get(Calendar.DAY_OF_WEEK);
		return result;
	}

	/**
	 * 閼惧嘲褰囬幐鍥х暰閺冨爼妫块弰顖氭躬閸濐亙绔撮獮锟�
	 * 
	 * @param time
	 * @return
	 */
	public static int getYear(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 瑜版挸澧犻弮鍫曟？閺勵垰鎯侀崷銊︾厙娑擄拷閺冨爼妫垮▓闈涘敶閿涘瞼鏁ゆ禍搴㈩梾濞村妞块崝銊︽闂傚娈戝锟芥慨瀣╃瑢缂佹挻娼� 婵″倿鎸�妤稿吋妞块崝銊ユ躬濮ｅ繑妫╅惃锟�13:00瀵拷婵绱�13:30缂佹挻娼敍宀勫亝娑斿牆鍤遍弫鎵畱鐠嬪啰鏁ょ亸杈ㄦЦ startTime
	 * = 13 * TimeDateUtil.ONEHOUR = 13:00 endTime = 13 * TimeDateUtil.ONEHOUR +
	 * 30 * TimeDateUtil.ONEMIN = 13:30
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isCurrentTimeBetween(long startTime, long endTime) {
		long currentTime = TimeDateUtil.getCurrentTime();
		long todayStartTime = TimeDateUtil.getTodayZeroTime() + startTime;
		long todayEndTime = TimeDateUtil.getTodayZeroTime() + endTime;
		if (currentTime >= todayStartTime && currentTime <= todayEndTime) {
			return true;
		}
		return false;
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲閺冨爼妫块弰顖氭儊閸︺劍鐓囨稉锟介弮鍫曟？濞堥潧鍞�
	 * 
	 * @param startTime 鐢箑鍕炬禒鐣屾畱濞茶濮╅弮鍫曟？
	 * @param endTime
	 * @return
	 */
	public static boolean isCurrentTimeBetweenByActive(long startTime, long endTime){
		long currentTime = TimeDateUtil.getCurrentTime();
		if (currentTime >= startTime && currentTime <= endTime) {
			return true;
		}
		return false;
	}
	
	/**
	 * 閺嶈宓乭h:mm閺嶇厧绱℃潻鏂挎礀閺冨爼妫块崐锟�
	 * 
	 * @param timeStr
	 * @return
	 */
	public static long getTimeByHourMinStr(String timeStr) {
		if (CommonUtils.isNull(timeStr)) {
			return 0;
		}
		if (timeStr.equals("0")) {
			return 0;
		}
		Integer[] array = StringUtils.stringToArray(
				timeStr.replaceAll(":0", ":"), ":", Integer.class);
		long result = array[0] * TimeDateUtil.ONEHOUR;
		if (array.length == 2) {
			result += array[1] * TimeDateUtil.ONEMIN;
		}
		if (array.length == 3) {
			result += array[1] * TimeDateUtil.ONEMIN;
			result += array[2] * TimeDateUtil.ONESECOND;
		}
		return result;
	}

	/**
	 * 閺嶈宓乭h:mm閺嶇厧绱℃潻鏂挎礀瑜版挸銇夐弮鍫曟？閻愶拷
	 * 
	 * @param timeStr
	 * @return
	 */
	public static long getByTodayTime(String timeStr) {
		if (CommonUtils.isNull(timeStr)) {
			return 0;
		}
		if (timeStr.equals("0")) {
			return 0;
		}
		long result = getTimeByHourMinStr(timeStr);
		return getTodayZeroTime() + result;
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾存箑閻ㄥ嫬銇夐弫锟�
	 * 
	 * @return
	 */
	public static int getDayCountOfThisMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 閼惧嘲褰囨禒濠傘亯閸椾椒绨查悙鍦畱閺冨爼妫�
	 * 
	 * @return 娴狀櫜ong缁鐎锋潻鏂挎礀閻ㄥ嫭鏆熼崐锟�
	 */
	public static long getTodayQuinzeTime() {
		return getTodayZeroTime() + getTimeByHourMinStr("15:00");
	}

	/**
	 * 
	 * @param year
	 * @param month
	 *            {link=Calendar}
	 * @param date
	 * @return
	 */
	public static long getDateLongZeroTime(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date);
		return TimeDateUtil.getSomedayZeroTime(calendar.getTimeInMillis());
	}

	public static int getDateIntZeroTime(int year, int month, int date) {
		return (int) (getDateLongZeroTime(year, month, date) / 1000);
	}

	/**
	 * 閼惧嘲褰囨径姘毌婢垛晝娈戦弮鍫曟？
	 * 
	 * @param day
	 *            婢垛晜鏆�
	 * @return
	 */
	public static int getDayTimeInt(int day) {
		return day * 3600 * 24;
	}

	/**
	 * 閼惧嘲褰囧ú璇插З閺嶇厧绱￠弮鍫曟？
	 * 
	 * @param str
	 * @return long 閺冨爼妫块幋锟�
	 */
	public static long getActiveTime(String str){
		
		Date date = string2Date(str, FORMAT_YY_MM_DD_HH_MM);
		return date.getTime();
	}
	
	public static long getTimeYYMMDD(String str){
		Date date = string2Date(str, FORMAT_YY_MM_DD);
		return date.getTime();
	}
	
	public static void main(String[] args) {
		long start = getActiveTime("2018_01_25_00_00");
		long end = getActiveTime("2018_01_26_00_00");
		
		
		System.err.println("start: " + start);
		System.err.println("end: " + end);
		System.err.println("mesc: " + (end - start));
		
		long time = 1516761584716l;
		System.err.println("time: " + FORMAT_YYMMDD_HHMMSS.format(new Date(1516712218994l)) + " time: " + FORMAT_YYMMDD_HHMMSS.format(new Date(1516712228949l)));
		
	}
}