package idevgame.meteor.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import idevgame.meteor.utils.FileUtils;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class ZipUtils_7zip {

    /**
     * 閸樺缂夐弬鍥︽婢讹拷
     * @param file
     * @param outfilename
     */
    public static void compress(File file,String outfilename) {
    	Item7Zip[] _items = createItems(file);
    	compress(_items, new File(outfilename));
    }
    
    /** 
     * 閸樺缂夐幐鍥х暰閺傚洣娆�
     * @param sources 鐠у嫭绨弬鍥︽閸掓銆�
     * @param zipPath 閻╊喗鐖ｉ弬鍥︽閸氾拷
     * @param 鐎电懓绨查惃鍕カ濠ф劖鏋冩禒璺烘倳
     * @return
     */
    public static boolean compress(List<File> sources,  File outfilename, List<String> paths) {
    	Item7Zip[] _items = new Item7Zip[sources.size()];
    	for (int i = 0; i < _items.length; i++) {
    		File file = sources.get(i);
			String path2 = paths.get(i);//鐎涙劖鏋冩禒鍓佹畱閻╃顕捄顖氱窞
    		byte[] content = null;
    		if(file.isDirectory() == false){//閺傚洣娆�
    			content = FileUtils.loadBytes(file.getPath());
    		}
    		
    		_items[i] = new Item7Zip(path2, content);
		}
    	return compress(_items, outfilename);
    }

    /**
     * 閸樺缂夐弬鍥︽婢讹拷
     * @param items
     * @param outfilename
     */
    public static boolean compress(Item7Zip[] items,File outfile) {
        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchive7z outArchive = null;
        try {
            raf = new RandomAccessFile(outfile, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchive7z();

            // Configure archive
            outArchive.setLevel(5);
            outArchive.setSolid(true);

            // Create archive
            MyCreateCallback callBack = new MyCreateCallback();
            callBack.setItem(items);
            outArchive.createArchive(new RandomAccessFileOutStream(raf),items.length, callBack);

            success = true;
        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    success = false;
                }
            }
        }
        
        return success;
    }
    
    /**
     * 鐟欙絽甯囬弫鎵矋
     * @param datas
     * @param outputFilepath
     */
	public static void extractile(byte[] datas, String outputFilepath) {
		ByteArrayStream byteArrayStream = null;
		try {
			byteArrayStream = new ByteArrayStream(datas, false);
			IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
					byteArrayStream);
			
			extractileByStandard(inArchive, outputFilepath);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
    /**
	 * 鐟欙絽甯囬弬鍥︽
	 * @param inputFilepath
	 * @param outputFilepath
     * @throws Exception 
	 */
	public static void extractile(String inputFilepath,final String outputFilepath){
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(inputFilepath, "r");
			IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
					new RandomAccessFileInStream(randomAccessFile));
			
			extractileByStandard(inArchive, outputFilepath);
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    /**
	 * 鐟欙絽甯囬弬鍥︽:缁狅拷閸楁洘鏌熷锟�,閺佸牏宸奸幈锟�,閸欘亪锟藉倸鎮庣亸蹇旀瀮娴狅拷.娑撳秴缂撶拋顔诲▏閻拷
	 * @param inputFilepath
	 * @param outputFilepath
	 */
	private static void extractileBySimpleD(IInArchive inArchive,final String outputFilepath) {
		try {
			// Getting simple interface of the archive inArchive
			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

//			System.out.println("  Hash  |  Size  | Filename");
//			System.out.println("----------+------------+---------");

			//鐟欙絽甯囬弬鍥︽
			for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
				final int[] hash = new int[] { 0 };
				System.out.println("鐟欙絽甯囬弬鍥︽:item.getPath()=" + item.getPath());
				File file = new File(outputFilepath + "/" + item.getPath());
				if(file.getParentFile().exists() == false){
					file.getParentFile().mkdirs();
				}
				if (!item.isFolder()) {
					ExtractOperationResult result;
					final long[] sizeArray = new long[1];
					result = item.extractSlow(new ISequentialOutStream() {
						public int write(byte[] data) throws SevenZipException {

							// Write to file
							FileOutputStream fos;
							try {
								File file = new File(outputFilepath + "/" + item.getPath());
								// error occours below
								// file.getParentFile().mkdirs();
								fos = new FileOutputStream(file);
								fos.write(data);
								fos.close();

							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							hash[0] ^= Arrays.hashCode(data); // Consume data
							sizeArray[0] += data.length;
							return data.length; // Return amount of consumed
												// data
						}
					});
					if (result == ExtractOperationResult.OK) {
//						System.out.println(String.format("%9X | %10s | %s", //
//								hash[0], sizeArray[0], item.getPath()));
					} else {
						System.err.println("Error extracting item: " + result);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inArchive != null) {
				try {
					inArchive.close();
				} catch (SevenZipException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 鐟欙絽甯�:閺嶅洤鍣弬鐟扮础
	 * @param inArchive
	 * @param outputFilepath
	 */
	public static void extractileByStandard(IInArchive inArchive,final String outputFilepath) {
        try {
//            System.out.println("   Hash   |    Size    | Filename");
//            System.out.println("----------+------------+---------");

            int[] in = new int[inArchive.getNumberOfItems()];
            for (int i = 0; i < in.length; i++) {
                in[i] = i;
            }
            inArchive.extract(in, false, // Non-test mode
                    new MyExtractCallback(inArchive,outputFilepath));
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
        }
    }
	
	/**
	 * 濞村鐦紒鍕矏閸樺缂夐弬鍥︽
	 * @return
	 */
	public static Item7Zip[] createTestItems() {

        //     <root>
        //     |
        //     +- info.txt
        //     +- random-100-bytes.dump
        //     +- dir1
        //     |  +- file-in-a-directory1.txt
        //     +- dir2
        //        +- file-in-a-directory2.txt

		Item7Zip[] items = new Item7Zip[5];

        items[0] = new Item7Zip("info.txt", "This is the info");

        byte[] content = new byte[100];
        new Random().nextBytes(content);
        items[1] = new Item7Zip("random-100-bytes.dump", content);

        // dir1 doesn't have separate archive item
        items[2] = new Item7Zip("dir1" + File.separator + "file1.txt", "This file located in a directory 'dir'");

        // dir2 does have separate archive item
        items[3] = new Item7Zip("dir2" + File.separator, (byte[]) null);
        items[4] = new Item7Zip("dir2" + File.separator + "file2.txt", "This file located in a directory 'dir'");
        return items;
    }
	
	/**
	 * 闁俺绻冮幐鍥х暰閺傚洣娆㈤弻銉﹀,闁秴宸婚惄顔肩秿
	 * @param file
	 * @return
	 */
	private static Item7Zip[] createItems(File file) {
		List<Item7Zip> list = new ArrayList<>();
		String rootPath = file.getParent();//閺嶅湱娲拌ぐ锟�
		findItem(list,file,rootPath);
		
		Item7Zip[] items = new Item7Zip[list.size()];
		list.toArray(items);
		return items;
	}

	/**
	 * 闁秴宸婚弬鍥︽
	 * @param list
	 * @param file
	 */
	private static void findItem(List<Item7Zip> list, File file,String rootPath) {
		//閼奉亜绻�
		String path = file.getPath();//鐎瑰本鏆ｇ捄顖氱窞
		String path2 = path.substring(rootPath.length());//鐎涙劖鏋冩禒鍓佹畱閻╃顕捄顖氱窞
		byte[] content = null;
		if(file.isDirectory() == false){//閺傚洣娆�
			content = FileUtils.loadBytes(path);
		}
		Item7Zip item = new Item7Zip(path2, content);
		list.add(item);
		
		if(file.isDirectory()){//閺傚洣娆㈡径锟�,缂佈呯敾闁秴宸�
			for (File f : file.listFiles()) {
				if(f.isDirectory()){
					findItem(list, f,rootPath);
				}else{
					path = f.getPath();//鐎瑰本鏆ｇ捄顖氱窞
					path2 = path.substring(rootPath.length());//鐎涙劖鏋冩禒鍓佹畱閻╃顕捄顖氱窞
					content = FileUtils.loadBytes(path);
					item = new Item7Zip(path2, content);
					list.add(item);
				}
			}
		}
	}

}

class Item7Zip {
	private String path;
    private byte[] content;

    public Item7Zip(String path, String content) {
        this(path, content.getBytes());
    }

    public Item7Zip(String path, byte[] content) {
        this.path= path;
        this.content= content;
    }

    public String getPath() {
        return path;
    }

    public byte[] getContent() {
        return content;
    }
}

/**
 * 閸樺缂夐崶鐐剁殶缁拷
 */
class MyCreateCallback implements IOutCreateCallback<IOutItem7z> {
	private Item7Zip[] items;
	
    public void setOperationResult(boolean operationResultOk)
            throws SevenZipException {
        // Track each operation result here
    }

    public void setTotal(long total) throws SevenZipException {
        // Track operation progress here
    }

    public void setCompleted(long complete) throws SevenZipException {
        // Track operation progress here
    }

    int getItemInformationPer = 0;
    public IOutItem7z getItemInformation(int index,OutItemFactory<IOutItem7z> outItemFactory) {//婢舵氨鍤庣粙瀣殶閻拷
    	getItemInformationPer++;
    	if(getItemInformationPer == 1 || getItemInformationPer%10 == 0 || getItemInformationPer == items.length){
    		System.out.println("7zip鏉╂稑瀹�-getItemInformation:" + getItemInformationPer + "/" + items.length);
    	}
        IOutItem7z item = outItemFactory.createOutItem();

        if (items[index].getContent() == null) {
            // Directory
            item.setPropertyIsDir(true);
        } else {
            // File
            item.setDataSize((long) items[index].getContent().length);
        }

        item.setPropertyPath(items[index].getPath());

        return item;
    }

    int getStreamPer = 0;
    public ISequentialInStream getStream(int i) throws SevenZipException {//婢舵氨鍤庣粙瀣殶閻拷
    	getStreamPer++;
    	if(getStreamPer == 1 || getStreamPer%10 == 0 || getStreamPer == items.length){
    		System.out.println("7zip鏉╂稑瀹�-getStream:" + getStreamPer + "/" + items.length);
    	}
        if (items[i].getContent() == null) {
            return null;
        }
        return new ByteArrayStream(items[i].getContent(), true);
    }

	public void setItem(Item7Zip[] items2) {
		this.items = items2;
		for (Item7Zip item7Zip : items2) {
			if(item7Zip.getContent() == null){
				getStreamPer++;//閸樺缂夋潻娑樺,閺傚洣娆㈡径閫涚瑝闂囷拷鐟曪拷
			}
		}
	}
}

/**
 * 鐟欙絽甯囬崶鐐剁殶缁拷
 * @author moon
 * 2017楠烇拷1閺堬拷13閺冿拷
 */
class MyExtractCallback implements IArchiveExtractCallback {
    private int hash = 0;
    private int size = 0;
    private int index;
    private boolean skipExtraction;
    private IInArchive inArchive;
    private String outputFilepath;
    private BufferedOutputStream bos = null;//鏉堟挸鍤�
    private int itemCount = 0;//閺傚洣娆㈤幀缁樻殶

    public MyExtractCallback(IInArchive inArchive,String outputFilepath) {
        this.inArchive = inArchive;
        this.outputFilepath = outputFilepath;
        try {
        	itemCount = inArchive.getNumberOfItems();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}
    }

    public ISequentialOutStream getStream(int index, 
            ExtractAskMode extractAskMode) throws SevenZipException {
        this.index = index;
        skipExtraction = (Boolean) inArchive
                .getProperty(index, PropID.IS_FOLDER);
        
        String path = (String) inArchive.getProperty(index, PropID.PATH);
        File file = new File(outputFilepath + "/" + path);
		if(file.getParentFile().exists() == false){
			file.getParentFile().mkdirs();
		}
		try {
			if(bos == null && skipExtraction == false){
				FileOutputStream out = new FileOutputStream(file);
				bos = new BufferedOutputStream(out);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
            return null;
        }
        return new ISequentialOutStream() {
            public int write(byte[] data) throws SevenZipException {
                hash ^= Arrays.hashCode(data);
                size += data.length;
                try {
					bos.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
                return data.length; // Return amount of proceed data
            }
        };
    }

    public void prepareOperation(ExtractAskMode extractAskMode) 
            throws SevenZipException {
    }

    int setOperationResultPer = 0;
    public void setOperationResult(ExtractOperationResult 
            extractOperationResult) throws SevenZipException {
    	setOperationResultPer++;
    	if(setOperationResultPer == 1 || setOperationResultPer%10 == 0 || setOperationResultPer == itemCount){
    		System.out.println("un7zip鏉╂稑瀹�:" + setOperationResultPer + "/" + itemCount);
    	}
    	
        if (skipExtraction) {
            return;
        }
        if (extractOperationResult != ExtractOperationResult.OK) {
            System.err.println("Extraction error");
        } else {
//        	String path = (String) inArchive.getProperty(index, PropID.PATH);
//            System.out.println(String.format("%9X | %10s | %s", hash, size,path));
//            System.out.println("鐟欙絽甯囬弬鍥︽:item.getPath()=" + path);
			
			if (bos != null) {//閺傚洣娆�,閸愶拷
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				bos = null;
			}
            
            hash = 0;
            size = 0;
        }
    }

    public void setCompleted(long completeValue) throws SevenZipException {
    }

    public void setTotal(long total) throws SevenZipException {
    }
}
