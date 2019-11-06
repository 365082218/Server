package idevgame.meteor.db.tool;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * @author moon
 */
public class EncodePassword {
	public static void main(String[] args) throws Exception {
//		String dbPwd = "hzdbserver";//鍘熷瘑鐮�
//		String dbPwd = "652124";//鍘熷瘑鐮�
		String dbPwd = "Moon2Sun";//鍘熷瘑鐮�
		if(args != null && args.length > 0){//鍙互杈撳叆
			dbPwd = args[0];
		}
		
		System.out.println("input:");
		System.out.println(dbPwd);
		System.out.println("output:");
		ConfigTools.main(new String[]{dbPwd});
	}
}
