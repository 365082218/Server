package idevgame.meteor.utils;

/**
 * 娴ｅ秵鎼锋担婊冾槱閻炲棴绱濈悰銉ュ弿娑擄拷0閻ㄥ嫪缍呴敍宀冪殶閺佹挳銆庢惔锟�
 * @author chenzl
 * 2016-03-31
 */
public class BitManipulationUtils {

	public static int[] process(int length, int value) throws Exception{
		if(length <= 0 || value < 0){
			throw new Exception("閸欏倹鏆熼柨娆掝嚖");
		}
		
		int temp = (int)Math.pow(2L, length * 1L);
		if(value > temp){
			throw new Exception("閸欏倹鏆熼柨娆掝嚖");
		}
		
		String str = Integer.toBinaryString(value);
		String[] strs = str.split("");

		int[] bits = new int[length];
		for (int i = strs.length - 1; i >= 0; i--) {
			bits[strs.length - i - 1] = Integer.parseInt(strs[i]);
		}
		if(bits.length > strs.length){
			for (int i = strs.length; i < bits.length; i++) {
				bits[i] = 0;
			}
		}
		return bits;
	}
}
