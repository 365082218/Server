package idevgame.meteor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(DirUtils.class);
	
	/**
	 * 鏉╂柨娲栨稉锟芥稉顏嗘窗瑜版洜娈戦弬鍥︽閿涘畮rgs閸欘亝甯撮崣妤侇劀閸掓瑨銆冩潏鎯х础
	 */
	public static File[] ls(String path, String... args) {
		File dir = new File(path);

		if (args.length == 0) {
			return dir.listFiles();
		}

		final Pattern[] patterns = new Pattern[args.length];
		for (int i = 0; i < args.length; i++) {
			patterns[i] = Pattern.compile(args[i]);
		}

		return dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				for (Pattern pattern : patterns) {
					Matcher matcher = pattern.matcher(name);
					if (matcher.find()) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * 闁帒缍婄�涙劗娲拌ぐ鏇＄箲閸ョ偞澧嶉張澶岊儊閸氬牏娈戦弬鍥︽閿涘畮rgs閸欘亝甯撮崣妤侇劀閸掓瑨銆冩潏鎯х础
	 */
	public static File[] recursive(String path, String... args) {
		List<File> resLs = new ArrayList<File>();
		Queue<File> dirs = new LinkedList<File>();

		File file = new File(path);
		dirs.offer(file);

		final Pattern[] patterns = new Pattern[args.length];
		for (int i = 0; i < args.length; i++) {
			patterns[i] = Pattern.compile(args[i]);
		}

		while (dirs.size() != 0) {
			File tempPath = dirs.poll();
			File[] files = tempPath.listFiles();
			// 闁秴宸籪iles,婵″倹鐏夐弰顖滄窗瑜版洜鎴风紒顓熸偝缁憋拷,閺傚洣娆㈤崚娆忓爱闁板秵顒滈崚娆掋�冩潏鎯х础
			for (File tFile : files) {
				if (tFile.isDirectory()) {
					dirs.offer(tFile);
				} else {
					for (Pattern pattern : patterns) {
						Matcher matcher = pattern.matcher(tFile.getName());
						if (matcher.find()) {
							resLs.add(tFile);
							break;
						}
					}
				}
			}
		}

		File[] res = new File[resLs.size()];
		resLs.toArray(res);
		return res;
	}

	/**
	 * 婢跺秴鍩楅崡鏇氶嚋閺傚洣娆�
	 * 
	 * @param to
	 *            韫囧懘銆忛弰顖滄窗瑜帮拷
	 */
	public static boolean cp(String from, String to, String... args) {
		// buffer娑擄拷2M
		int length = 2097152;
		File fromFile = new File(from);
		File toFile = new File(to);
		// 閻╊喖缍嶉惃鍕樈閻╃缍嬫禍宸唒 -r * 濮濓絽鍨悰銊ㄦ彧瀵繐顕В蹇庣鐏炲倿鍏橀張澶嬫櫏
		if (fromFile.isDirectory()) {
			File[] childFiles = ls(from, args);
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					mkdir(to + File.separator + childFile.getName());
				}
				cp(childFile.getPath(),
						to + File.separator + childFile.getName(), args);
			}
			return true;
		} else {
			try {
				FileInputStream in = new FileInputStream(from);
				String toPath = to;
				if (toFile.isDirectory()) {
					toPath += File.separator + fromFile.getName();
				}
				FileOutputStream out = new FileOutputStream(toPath);
				byte[] buffer = new byte[length];
				while (true) {
					int ins = in.read(buffer);
					if (ins == -1) {
						in.close();
						out.flush();
						out.close();
						return true;
					} else
						out.write(buffer, 0, ins);
				}
			} catch (Exception e) {
				logger.error("cp error", e);
				return false;
			}
		}
	}

	/**
	 * 閸掔娀娅庨崡鏇氶嚋閺傚洣娆�
	 */
	public static void rm(String path, String... args) {
		File file = new File(path);
		
		// 閻╊喖缍嶉惃鍕樈閻╃缍嬫禍锟� rm -r path/* 娑撳秳绱伴崚鐘绘珟閺傚洣娆㈡径锟� 濮濓絽鍨悰銊ㄦ彧瀵繐褰х�甸�涚鐏炲倹婀侀弫锟�
		if (file.isDirectory()) {
			File[] childFiles = ls(path, args);
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					rm(childFile.getPath());
				}

				if(!childFile.delete()){
					logger.error("file delete failed. filename="+childFile.getAbsolutePath());
				}
			}
		} else {
			if(!file.delete()){
				logger.error("file delete failed. filename="+file.getAbsolutePath());
			}
		}
	}

	/**
	 * 閸掓稑缂撻弬鍥︽婢讹拷
	 */
	public static boolean mkdir(String path) {
		File file = new File(path);
		return file.mkdirs();
	}

	/**
	 * 閸掔娀娅庣粚鐑樻瀮娴犺泛銇�
	 */
	public static boolean rmdir(String path) {
		File file = new File(path);
		return file.delete();
	}
}
