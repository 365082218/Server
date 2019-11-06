package idevgame.meteor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ftp 娑撳﹣绱�
 * 
 * @author chen 2014楠烇拷11閺堬拷19閺冿拷
 * 
 */
public class FtpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);
	
	/**
	 * ftp 閺堝秴濮熼崳銊や繆閹拷
	 * @author chen 2014楠烇拷11閺堬拷19閺冿拷
	 *
	 */
	public static class FtpServer {
		String ip;
		int port; 
		String user; 
		String password;
		boolean isSSL;
	}
	
	/**
	 * 
	 * @param serverInfo
	 * @param workingDir 瀹搞儰缍旈惄顔肩秿
	 * @param files
	 * @param fileType  閺傚洣娆㈡稉濠佺炊缁鐎� 婵★拷 FTP.BINARY_FILE_TYPE
	 * @throws Exception 
	 */
	public static void ftpUpload(FtpServer serverInfo, String workingDir, File[] files, int fileType) throws Exception {
		
		FTPClient client = null;
		if (serverInfo.isSSL) {
			client = new FTPSClient();
		} else {
			client = new FTPClient();
		}
		try {
			client.connect(serverInfo.ip, serverInfo.port);
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new Exception("ftp 閺堝秴濮熼崳銊︽￥濞夋洝绻涢幒锟� ip = " + serverInfo.ip + " port = " + serverInfo.port);
			}
			
			boolean isLogin = client.login(serverInfo.user, serverInfo.password);
			client.setFileType(fileType);
			if (isLogin) {
				client.mkd(workingDir);
				boolean isCd = client.changeWorkingDirectory(workingDir);
				
				if (!isCd) {
					throw new Exception("閺冪姵纭堕弨鐟板綁瀹搞儰缍旈惄顔肩秿 " + workingDir);
				}
				for (File file : files) {
					try (InputStream in = new FileInputStream(file)) {
						boolean isup = client.storeFile(new String(file.getName().getBytes(), "utf8"), in);
						if (!isup) {
							throw new Exception("鏉╂瑤閲滈弬鍥︽娑撳﹣绱舵径杈Е閿涳拷" + file.getName());
						}
					} 
				} 
				
			} else {
				throw new Exception("閺冪姵纭堕惂璇茬秿ftp 閺堝秴濮熼崳锟�");
			}
		} finally {
			if (client.isConnected()) {
				client.disconnect();
			}
		}
		

	
		
		
		
	}
	public final static void main(String args[]) throws Exception {
		String serverIp = "118.178.125,0";
		String user = "moon2test";
		String passwd = "moon2test8888";
		String workdir = "res";
		FTPClient client = new FTPSClient(false);
//		FTPClient client = new FTPClient();

		try {
			client.connect(serverIp, 21);
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				logger.error("FTPReply.isPositiveCompletion false 閿涳拷" + serverIp);
			}
			System.out.println(client.getReplyString());
			System.out.println("port" + client.getRemotePort());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("鏉╃偞甯撮幋鎰");
		try {
			boolean isLogin = client.login(user, passwd);
			if (isLogin) {
				client.changeWorkingDirectory("/");
				System.out.println(client.printWorkingDirectory());
				boolean isCd = client.changeWorkingDirectory(workdir);
				System.out.println("iscd=" + isCd);
				
				System.out.println(client.printWorkingDirectory());
				boolean isMk = client.makeDirectory("140110112");
				
				int reply = client.getReplyCode();
				
				System.out.println("isMk " + isMk + " reply=" + reply);
				client.changeWorkingDirectory("140110112");
				System.out.println("workdir:" + client.printWorkingDirectory());
				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
				File file = new File("E:\\test.zip");
				try (InputStream in = new FileInputStream(file)) {
					boolean isup = client.storeFile(new String(file.getName().getBytes(), "utf8"), in);
					if (!isup) {
						System.out.println(client.getReplyCode());
						throw new Exception("鏉╂瑤閲滈弬鍥︽娑撳﹣绱舵径杈Е閿涳拷" + file.getName());
					}
				} 
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//
//		File file = new File("C:\\Users\\Administrator\\Downloads\\123.zip");
//		try (InputStream in = new FileInputStream(file)) {
//			boolean result = client.storeFile(new String(file.getName()
//					.getBytes(), "utf8"), in);
//			System.out.println("upload " + result);
//			client.logout();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			client.disconnect();
//		}
	}

}
