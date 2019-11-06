package idevgame.meteor.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

	public static Random getRandom(){
		return ThreadLocalRandom.current();
	}

	/**
	 * 
	 * @param middle
	 *            娑擃參妫块崐锟�
	 * @param min
	 *            閺堬拷鐏忓繐锟界》绱濋崣顖氬瘶閸氾拷
	 * @param max
	 *            閺堬拷婢堆冿拷纭风礉閸欘垰瀵橀崥锟�
	 * @param period
	 *            濮濓絾锟戒浇娉曟惔锟�
	 * @return
	 */
	public static int gaussianRandom(int middle, int min, int max, int period) {

		double g = getRandom().nextGaussian();

		int res = middle;
		res = (int) Math.round(middle + g * period);

		if (res < min) {
			res = min;
		} else if (res > max) {
			res = max;
		}
		return res;
	}
	
	/**
	 * 闂呭繑婧�閼煎啫娲块崐锟�
	 * @param min >=
	 * @param max <=
	 * @return
	 */
	public static int getRandomInt(int min,int max){
		int abs = Math.abs(max - min) + 1;
		return (min + getRandom().nextInt(abs));
	}
	
	public static void main(String[] args) {
		
		int res;
		
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		
		for (int i = 50; i < 76; i++) {
			map.put(i, 0);
		}
		
		for (int i = 0; i < 1000000; i++) {
			res=RandomUtil.gaussianRandom(66, 50, 75, 5);
			
			map.put(res, map.get(res)+1);
			
		}
		
		
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey()+" "+(entry.getValue()/10000f)+"%");
		}
	}
	
}
