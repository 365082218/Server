package idevgame.meteor.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtils {
	
	public final static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String DATA_SEP = "|";
	
	
	/**
	 * 鏉╂柨娲杄xcel閸楁洖鍘撻弽鑲╂畱int閸婏拷
	 * @param cell
	 * @return 
	 */
	public static Integer getIntValue(HSSFCell cell)  {
		
		
		int val = -1;
		if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			Double dou = cell.getNumericCellValue();
			val = dou.intValue();
		} else {
			String cellStr = cell.toString().trim();
			if (cellStr.indexOf('.') > 0) {//鐏忔繆鐦亸蹇旀殶鏉烆剚宕�
				Double dou = Double.parseDouble(cellStr);
				val = dou.intValue();
			} else {	//姒涙顓婚弰鐥爊t
				val = Integer.parseInt(cellStr);
			}
		}
		return val;
	}
	
	/**
	 * 閸旂姾娴囬崷銊ュ灙閺嶅洭顣介惃鍒cel, 姒涙顓婚崝鐘烘祰缁楊兛绔存稉鐚籬eet
	 * @param path
	 * @param stringBuffer 鐏忓攢xcel 閺佺増宓佺�涙ɑ鏂佽ぐ鎼抰ringBuffer
	 * @return
	 */
	public static List<RowData> loadExcelWtihColumnName(String path, StringBuffer stringBuffer) {
		
		HSSFWorkbook hssfWorkbook = null;
		try {
			hssfWorkbook = new HSSFWorkbook(new FileInputStream(path));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
		if (hssfSheet == null) {
			throw new RuntimeException("缁楊兛绔存稉鐚籬eet 娑撳秴鐡ㄩ崷锟�");
		}
		
		// 鐠囪鍙嗛弽鍥暯
		HSSFRow hssfRowTitle = hssfSheet.getRow(0);
		if (hssfRowTitle == null) {
			throw new RuntimeException("閸掓鐖ｆ０妯圭瑝鐎涙ê婀�");
		}
		
		List<String> columnNames = new ArrayList<String>();
		int maxCellNum = 0;
		for (int i=0; i<=hssfRowTitle.getLastCellNum(); i++) {
			HSSFCell xh = hssfRowTitle.getCell(i);
			if (xh == null || xh.toString().trim().length()==0) {
				maxCellNum = i;
				break;
			}
			columnNames.add(xh.toString().trim());
			
			if(i<hssfRowTitle.getLastCellNum() - 1){
				stringBuffer.append(xh.toString() + DATA_SEP);
			}else{
				stringBuffer.append(xh.toString());
			}
		}
		
		stringBuffer.append("\n");
		
		List<RowData> rowDatasList = new ArrayList<>();
		
		for (int rowNum=1; rowNum<=hssfSheet.getLastRowNum(); rowNum++) {
			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
			
			if (hssfRow == null) break;
			
			if (hssfRow.getCell(0) == null){
				break;
			}else{
				Object obj0 = null;
				try {
					obj0 = getCellValueObject(hssfRow.getCell(0), columnNames.get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(obj0 == null || obj0.toString().length() < 1){
					System.out.println("閺堝鈹栭弫鐗堝祦:" + path);
					break;
				}
				
//				if(path.indexOf("mission.") >= 0){
//					System.out.println(rowNum + "=" + obj0 + "@");
//				}
			}
			
//			if(path.indexOf("mission.") >= 0){
//				System.out.println(rowNum + "=" + path);
//			}
			
			RowData rowData = new RowData(columnNames);
			List<Object> columnValues = new ArrayList<Object>();
			rowData.setColumnValues(columnValues);
			rowDatasList.add(rowData);
			
			for (int i=0; i<maxCellNum; i++) {
				HSSFCell xh = hssfRow.getCell(i);
				String columnName = columnNames.get(i);
				Object obj = null;
				try {
					obj = getCellValueObject(xh, columnName);
				} catch (Exception e) {
					throw new RuntimeException(path + ":" + columnName + " row:" + rowNum +" 閺佺増宓侀柨娆掝嚖:" + e.toString());
				}
				columnValues.add(obj);
				
				String str = null;
				if (obj instanceof Date) {
					String dataStr = DEFAULT_DATE_FORMAT.format((java.util.Date)obj);
					str = dataStr;
				} else {
					str = obj.toString();
				}
				
				if(i<maxCellNum - 1){
					stringBuffer.append(str + DATA_SEP);
				}else{
					stringBuffer.append(str);
				}
			}
			
			if(rowNum<hssfSheet.getLastRowNum()){
				stringBuffer.append("\n");
			}
		}
		
		
		return rowDatasList;
		
	}
	
	
	/**
	 * 閸旂姾娴囬崷銊ュ灙閺嶅洭顣介惃鍒cel, 姒涙顓婚崝鐘烘祰缁楊兛绔存稉鐚籬eet
	 * @param path
	 * @return
	 */
	public static List<RowData> loadExcelWtihColumnName(String path) {
		HSSFWorkbook hssfWorkbook = null;
		try {
			hssfWorkbook = new HSSFWorkbook(new FileInputStream(path));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
		if (hssfSheet == null) {
			throw new RuntimeException("缁楊兛绔存稉鐚籬eet 娑撳秴鐡ㄩ崷锟�");
		}
		
		// 鐠囪鍙嗛弽鍥暯
		HSSFRow hssfRowTitle = hssfSheet.getRow(0);
		if (hssfRowTitle == null) {
			throw new RuntimeException("閸掓鐖ｆ０妯圭瑝鐎涙ê婀�");
		}
		
		List<String> columnNames = new ArrayList<String>();
		int maxCellNum = 0;
		for (int i=0; i<=hssfRowTitle.getLastCellNum(); i++) {
			HSSFCell xh = hssfRowTitle.getCell(i);
			if (xh == null || xh.toString().trim().length()==0) {
				maxCellNum = i;
				break;
			}
			columnNames.add(xh.toString().trim());
		}
		
		
		List<RowData> rowDatasList = new ArrayList<>();
		
		for (int rowNum=1; rowNum<=hssfSheet.getLastRowNum(); rowNum++) {
			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
			
			if (hssfRow == null) break;
			
			if (hssfRow.getCell(0) == null){
				break;
			}else{
				Object obj0 = null;
				try {
					obj0 = getCellValueObject(hssfRow.getCell(0), columnNames.get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(obj0 == null || obj0.toString().length() < 1){
					System.out.println("閺堝鈹栭弫鐗堝祦:" + path);
					break;
				}
				
			}
			
			RowData rowData = new RowData(columnNames);
			List<Object> columnValues = new ArrayList<Object>();
			rowData.setColumnValues(columnValues);
			rowDatasList.add(rowData);
			
			for (int i=0; i<maxCellNum; i++) {
				HSSFCell xh = hssfRow.getCell(i);
				String columnName = columnNames.get(i);
				Object obj = null;
				try {
					obj = getCellValueObject(xh, columnName);
				} catch (Exception e) {
					throw new RuntimeException(path + ":" + columnName + " row:" + rowNum +" 閺佺増宓侀柨娆掝嚖:" + e.toString());
				}
				columnValues.add(obj);
			}
		}
		
		return rowDatasList;
		
	}
	
	/**
	 * 瀵版鍩岄弻鎰嚋閺嶇厧鐡欓惃鍕殶閸婏拷:閺嶈宓侀幋鎴滄粦妞ゅ湱娲伴惃鍕暰娑旓拷
	 * @param cell
	 * @param titleType
	 * @return 瑜帮拷 cell=null 閺冿拷 int 姒涙顓�0; string 姒涙顓�""; date 姒涙顓籲ull;
	 * @throws Exception 
	 */
	public static Object getCellValueObject(HSSFCell cell,String titleType) {
		
		if (cell == null) {
//			throw new RuntimeException("娴肩姴鍙嗛惃鍒ll 娑撹櫣鈹�");
			System.out.println("Excel娴肩姴鍙嗛惃鍒ll娑撹櫣鈹�,titleType=" + titleType);
			return "";
		}
		
		String typeName = getCellValueTypeName(titleType);
		
		if	(typeName.endsWith("Date"))	{
			return cell.getDateCellValue();
		} else if (typeName.endsWith("Integer")) {
			return  ExcelUtils.getIntValue(cell);
		} else if(typeName.endsWith("String"))	{
			String str = cell.toString();
			String newStr = null;
			if(isIntNumber(str)){//缁绢垱鏆熺�涙琚崹锟�,鐠囪鍤弶銉ュ讲閼宠姤婀佺亸蹇旀殶閻愶拷,閸樼粯甯�0閸氬酣娼伴惃鍕毈閺佹壆鍋�
				double dou = Double.parseDouble(str);
				int i = (int)dou;
				newStr = "" + i;
			}else{
				newStr = str;
			}
			
			return newStr;
		} else {	//閺堫亞鐓＄猾璇茬��
			return cell.toString();
		}
		
	}
	
	/**
	 * 閺勵垰鎯乮nt
	 * @param str
	 * @return
	 */
	private static boolean isIntNumber(String str) {
		int i0 = '0';//48
		int i9 = '9';//57
		if(str == null || str.length() < 1){
			return false;
		}
		
		for (int i = 0; i < str.length(); i++) {//閻婀呴弰顖氭儊閺勵垱鏆熺�涳拷
			char ch = str.charAt(i);
			if(ch == '.'){//鐏忓繑鏆熼悙锟�
				continue;
			}
			
			if(ch > i9 || ch < i0){//闂堢偛鐨弫鎵仯閻ㄥ嫬鍙炬禒鏍х摟缁楋拷
				return false;
			}
		}
		
		//int閹存牞锟藉寴ouble
		double dou = Double.parseDouble(str);
		int i = (int)dou;
		if(dou == i){//鐏忓繑鏆熼悙鐟版倵闂堛垺妲�0
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 閺嶈宓佺痪锕�鐣鹃惃鍕摟濞堥潧鎮曠粔鏉跨暰娑斿鈥樼�规艾宕熼崗鍐╃壐閺佹澘鐡х猾璇茬��
	 * @param fag
	 * @return
	 */
	public static String getCellValueTypeName(String fag) {
		String type = null;
		if (fag.startsWith("str")) {
			type = "String";
		} else if (fag.startsWith("date_") || fag.startsWith("time_")) {
			type = "Date";
		} else {
			type = "Integer";
		}
		
		return type;
	}
	
	/**
	 * 鐞涘本鏆熼幑锟�,閸栧懎鎯堝В蹇撳灙閻ㄥ嫬鐡у▓锟�
	 */
	public static class RowData {
		
		/** 閸掓鎮� */
		private final List<String> columnNames;
		
		/** 閸掓锟斤拷*/
		private List<Object> columnValues;
		
		public RowData(List<String> columnNames) {
			this.columnNames = columnNames;
		}
		
		public List<String> getColumnNames() {
			return columnNames;
		}

		public List<Object> getColumnValues() {
			return columnValues;
		}

		public void setColumnValues(List<Object> columnValues) {
			this.columnValues = columnValues;
		}
	} 
	
	public static void main(String[] args)  {
		StringBuffer buffer = new StringBuffer();
		List<RowData>  list = null;
		try {
			list = loadExcelWtihColumnName("E:\\moon2\\game-server\\res\\excel\\achievement.xls", buffer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println(list == null);
		
	}
}


