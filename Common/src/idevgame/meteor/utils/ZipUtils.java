package idevgame.meteor.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void zipBytes(byte[] data ,String fileName,String zippath) {
    	try {
            File zipFile = new File(zippath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(fileName));
            for (int i = 0; i < data.length; i++) {
            	 zipOut.write(data[i]);
			}
            zipOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private static void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			base = (base.length() == 0 ? "" : base + "/");
			for (int i = 0; i < files.length; i++) {
				zip(out, files[i], base + files[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
			int length = 0 ;
		    byte[] buf = new byte[1024];  //瀵よ櫣鐝涚紓鎾崇摠閺佹壆绮嶉敍宀�绱︾�涙ɑ鏆熺紒鍕畱婢堆冪毈娑擄拷閼割剟鍏橀弰锟�1024閻ㄥ嫭鏆ｉ弫鏉匡拷宥忕礉閻炲棜顔戞稉濠呯Ш婢堆勬櫏閻滃洩绉烘總锟�
		    while((length = in.read(buf)) != -1){
		    	out.write(buf, 0, length);
		    }
			in.close();
		}
	}

    /**
     * 閸樺缂�
     * @param inputFileName
     * @param zipFileName
     * @throws Exception
     */
    public static void zip(File inputFileName, String zipFileName) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFileName, inputFileName.getName());
		out.close();
	}

	/**
	 * 閸樺缂夐弬鍥︽閿涘nputFileName鐞涖劎銇氱憰浣稿竾缂傗晝娈戦弬鍥︽閿涘牆褰叉禒銉よ礋閻╊喖缍嶉敍锟�,zipFileName鐞涖劎銇氶崢瀣級閸氬海娈憐ip閺傚洣娆�
	 * @param inputFileName
	 * @param zipFileName
	 * @throws Exception
	 */
	public static void zip(String inputFileName, String zipFileName) throws Exception {
		zip(new File(inputFileName), zipFileName);
	}

	/**
	 * 鐟欙絽甯�
	 * @param buf 娴滃矁绻橀崚鑸垫殶閹癸拷
	 * @param destDir  娣囨繂鐡ㄩ惄顔肩秿
	 * @throws IOException
	 */
	public static void unZip(byte[] buf, String destDir) throws Exception {
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(buf);
		ZipInputStream in = new ZipInputStream(arrayInputStream);
		unZip(in, destDir); 
	}
	
	/**
	 * 鐟欙絽甯�
	 * @param zipFileName
	 * @param unzipDir
	 * @throws Exception
	 */
	public static void unZip(String zipFileName, String unzipDir) throws Exception {
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
		unZip(in, unzipDir);
	}
	
	/**
	 * 鐟欙絽甯�
	 * @param in
	 * @param unzipDir
	 * @throws Exception
	 */
	public static void unZip(InputStream in, String unzipDir) throws Exception {
		unZip(new ZipInputStream(in), unzipDir);
	}
	/**
	 * 鐟欙絽甯�,zipFileName鐞涖劎銇氬鍛靶掗崢瀣畱zip閺傚洣娆㈤敍瀵�nzipDir鐞涖劎銇氱憴锝呭竾閸氬孩鏋冩禒璺虹摠閺�鍓ф窗瑜帮拷
	 * @param zipFileName
	 * @param unzipDir
	 * @throws Exception
	 */
	public static void unZip(ZipInputStream in, String unzipDir) throws Exception {
		ZipEntry zipEntry;
		while ((zipEntry = in.getNextEntry()) != null) {
			String fileName = zipEntry.getName();
//			System.out.println("fileName=" + fileName);
			// 閺堝鐪扮痪褏绮ㄩ弸鍕剁礉鐏忓崬鍘涢崚娑樼紦閻╊喖缍�
			String tmp;
			int index = fileName.lastIndexOf('/');
			if (index != -1) {
				tmp = fileName.substring(0, index);
				tmp = unzipDir + "/" + tmp;
				File f = new File(tmp);
				f.mkdirs();
			}

			// 閸掓稑缂撻弬鍥︽
			fileName = unzipDir + "/" + fileName;
			File file = new File(fileName);
			if(file.isDirectory() == false){
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(out);
				int length = 0 ;
			    byte[] buf = new byte[1024];  //瀵よ櫣鐝涚紓鎾崇摠閺佹壆绮嶉敍宀�绱︾�涙ɑ鏆熺紒鍕畱婢堆冪毈娑擄拷閼割剟鍏橀弰锟�1024閻ㄥ嫭鏆ｉ弫鏉匡拷宥忕礉閻炲棜顔戞稉濠呯Ш婢堆勬櫏閻滃洩绉烘總锟�
			    while((length = in.read(buf)) != -1){
			    	bos.write(buf, 0, length);
			    }
			    
				bos.close();
			}
		}
		in.close();
	}

	public static void main(String[] args) {
//		String name = "e:\\test.zip";
//		String destDir = "e:\\ziptest";
//		
//		try (FileInputStream in = new FileInputStream(name)) {
//			byte[] buff = IOUtils.toByteArray(in);
//			ZipUtils.unzip(buff, destDir);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		String filepath = "E:/tempsw3/1-1469674101765002101.rp";
		String zippath = "E:/tempsw3/1-1469674101765002101.zip";
		
//		zipFile(filepath,zippath);
		
//		unZipMultiFile(zippath, "E:/tempsw3/");
		
		zipBytes(FileUtils.loadBytes(filepath), "1-14.rp", zippath);
	}

}
