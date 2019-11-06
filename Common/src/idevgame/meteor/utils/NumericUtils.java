package idevgame.meteor.utils;

public class NumericUtils {

	/**
	 * 婢х偟娉惂鎯у瀻濮ｆ棁顓哥粻锟�
	 * @param value:閺佹澘锟斤拷
	 * @param enhance閿涙艾顤冮惄濠勬閸掑棙鐦崐锟�
	 * @return int閸婏拷
	 */
	public static int d100_EnhanceCount(int value, int enhance){
		return (int) (value *= (1 + (enhance * 0.01f)));
	}
	
	/**
	 * 婢х偟娉稉鍥у瀻濮ｆ棁顓哥粻锟�
	 * @param value:閺佹澘锟斤拷
	 * @param enhance閿涙艾顤冮惄濠佺閸掑棙鐦崐锟�
	 * @return int閸婏拷
	 */
	public static int d10000_EnhanceCount(int value, int enhance){
		return (int) (value *= (1 + (enhance * 0.0001f)));
	}
	
	/**
	 * 婢х偟娉惂鎯у瀻濮ｆ梻绨跨涵顔款吀缁狅拷
	 * @param value:閺佹澘锟斤拷
	 * @param enhance閿涙艾顤冮惄濠勬閸掑棙鐦崐锟�
	 * @return float閸婏拷
	 */
	public static float d100_EnhanceCountFloat(float value, int enhance){
		return value *= (1 + (enhance * 0.01f));
	}
	
	/**
	 * 婢х偟娉稉鍥у瀻濮ｆ梻绨跨涵顔款吀缁狅拷
	 * @param value:閺佹澘锟斤拷
	 * @param enhance閿涙艾顤冮惄濠佺閸掑棙鐦崐锟�
	 * @return float閸婏拷
	 */
	public static float d10000_EnhanceCountFloat(float value, int enhance){
		return value *= (1 + (enhance * 0.0001f));
	}
	
	/**
	 * 閻ф儳鍨庡В鏃囶吀缁狅拷
	 * @param value:閺佹澘锟斤拷
	 * @param enhance閿涙艾顤冮惄濠勬閸掑棙鐦崐锟�
	 * @return int閸婏拷
	 */
	public static int d100_CalculateCount(int value, int enhance){
		return (int) Math.rint(value * enhance * 0.01);
	}
}
