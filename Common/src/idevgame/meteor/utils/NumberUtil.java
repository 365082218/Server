package idevgame.meteor.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberUtil
{
    private static DecimalFormat format = new DecimalFormat("#.#");
    public static String formatRMB(Object rmb)
    {
    	if(rmb==null)
    		return null;
    	format.applyPattern("閿燂拷#,##0.00");
    	return format.format(rmb);
    }
    
    public static String formatRMB(Object number,String pattern)
    {
    	format.applyPattern(pattern);
    	return format.format(number);
    }
    
    /**
     * @author:3Q
     */
    public static List<Integer> partition(Double min,Double max)
    {
    	List<Integer> list=new ArrayList<Integer>();
		
		double step=(max-min)/3;//濮ｅ繐灏梻鎾
		
		double stepPow=Math.floor(Math.log10(step));//閸栨椽妫块梾鏂垮絿10 娑撳搫绨抽惃鍕嚠閺佺増娓堕幒銉ㄧ箮閻ㄥ嫭鏆ｉ弫锟�
		if(stepPow==0l)
		{
			stepPow=1;
		}
		double digital=Math.pow(10, stepPow);//閸栨椽妫块梾鏃�娓舵妯绘殶娴ｏ拷
		step=Math.round(step/digital)*digital;//娴犮儲娓舵妯绘殶娴ｅ秴褰囬弫锟�
		
		if(step<=1)
		{
			step=2;
		}
		//System.out.println(step);
		Double n=Math.ceil(min/digital)*digital;//缁楊兛绔撮崠娲？
		while(n<max)
		{
			list.add(n.intValue());
			n=n+step;
		}
		return list;
    }
    
    public static int getRandomNumber(final int min, final int max){
    	Random rand= new Random(); 
    	int tmp = Math.abs(rand.nextInt());
 	    return tmp % (max - min + 1) + min;
    }
}
