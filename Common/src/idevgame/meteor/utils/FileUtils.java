package idevgame.meteor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * read a file to a string
	 */
	public static String readFile(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return null;
	}

	/**
	 * read a file to a string
	 */
	public static String readFile(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return null;
	}

	/**
	 * get md5 of file
	 */
	public static String fileMD5(String filePath) {
		FileInputStream fis = null;
		String md5 = null;
		try {
			fis = new FileInputStream(new File(filePath));
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(fis);
		}
		return md5;
	}

	/**
	 * get md5 of file
	 */
	public static String fileMD5(File file) {
		FileInputStream fis = null;
		String md5 = null;
		try {
			fis = new FileInputStream(file);
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(fis);
		}
		return md5;
	}

	/**
	 * Function name:saveChatTxt Description: 鐏忓棗鐡х粭锕傛肠閸氬牆鍟撻崗銉︽瀮娴狅拷
	 * @param chats :
	 * @param path :
	 * @return
	 */
	public static boolean saveChatTxt(List<String> chats, String path) {
		if (chats == null || chats.size() < 1) {
			return true;
		}
		boolean ok = false;
		File fo = null;
		FileOutputStream to = null;
		PrintWriter out = null;
		try {
			fo = new File(path);
			if (fo.exists()) { // 閺傚洣娆㈢�涙ê婀�,閸忓牆鍨归梽锟�
				fo.delete();
			}

			fo.createNewFile();// 閸掓稑缂撻弬鐗堟瀮娴狅拷

			to = new FileOutputStream(fo, false); // 閸掓稑缂撻弬鍥︽鏉堟挸鍤ù锟�
			out = new PrintWriter(to); // 鏉堟挸鍤ù锟�

			for (String msg : chats) {
				out.println(msg);
			}
			// out.println("------------------------鐎电厧鍤幋鎰,閸忥拷:" + chats.size() +
			// "閺壜ゎ唶瑜帮拷.");
			ok = true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		} catch (NullPointerException ee) {
			ee.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				out.close(); // 閸忔娊妫村ù锟�
				to.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ok;
	}
	
	// 閸掔娀娅庨弬鍥︽婢讹拷
	// param folderPath 閺傚洣娆㈡径鐟扮暚閺佸绮风�电鐭惧锟�
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 閸掔娀娅庣�瑰矂鍣烽棃銏″閺堝鍞寸�癸拷
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 閸掔娀娅庣粚鐑樻瀮娴犺泛銇�
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 閸掔娀娅庨幐鍥х暰閺傚洣娆㈡径閫涚瑓閹碉拷閺堝鏋冩禒锟�
	// param path 閺傚洣娆㈡径鐟扮暚閺佸绮风�电鐭惧锟�
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + tempList[i]);// 閸忓牆鍨归梽銈嗘瀮娴犺泛銇欓柌宀勬桨閻ㄥ嫭鏋冩禒锟�
				delFolder(path + File.separator + tempList[i]);// 閸愬秴鍨归梽銈団敄閺傚洣娆㈡径锟�
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 閹跺﹥鏋冩禒鑸靛瘻鐞涘矁顕伴崗銉ヨ嫙鏉╂柨娲杔ist
	 * @param filePath
	 * @return
	 */
	public static List<String> readFileReturnList(String filePath) {
		List<String> rs = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line = br.readLine();

			while (line != null) {
				rs.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return rs;
	}
	
	 /**
     * @param path
     * @param data
     * @param append:閺勵垰鎯佹潻钘夊閸掔増鏋冩禒鑸垫汞鐏忥拷
     * @return
     */
    public static boolean saveBytes(String path,byte[] data,boolean append){
    	File ff = new File(path);
    	if(ff.exists()){
    		System.out.println("------------鐠�锕�鎲�,鐟曞棛娲婇弬鍥︽:" + path);
    	}
        boolean r = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path, append);
            out.write(data);
            r = true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return r;
    }
    
    /**
     * 閸旂姾娴囬弬鍥︽,鏉╂柨娲栫�涙濡弫鎵矋
     * @param path
     * @return
     */
	public static byte[] loadBytes(String path) {
		byte[] msgData = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			if (in != null && in.available() > 0) {
				msgData = new byte[in.available()];
				in.read(msgData);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return msgData;
	}
}
