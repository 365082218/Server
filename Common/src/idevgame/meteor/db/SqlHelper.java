package idevgame.meteor.db;

import com.google.common.base.Joiner;

public class SqlHelper {
	
	public static String selectAll(String tbName){
		return "SELECT * FROM " + tbName;
	}

	public static String select(String tbName, String[] idNames){
		StringBuilder sb = new StringBuilder("SELECT * FROM ").append(tbName).append(" WHERE ");
		sb.append(idNames[0]).append("=?");
		for (int i = 1; i < idNames.length; i++) {
			sb.append(" and ").append(idNames[i]).append("=?");
		}

		return sb.toString();
	}

	public static String deleteAll(String tbName){
		return "DELETE FROM " + tbName;
	}

	public static String delete(String tbName, String[] idNames){
		StringBuilder sb = new StringBuilder("DELETE FROM ").append(tbName).append(" WHERE ");
		sb.append(idNames[0]).append("=?");
		for (int i = 1; i < idNames.length; i++) {
			sb.append(" and ").append(idNames[i]).append("=?");
		}

		return sb.toString();
	}

	public static String update(String tbName, String[] colNames, String[] idNames){
		StringBuilder sb = new StringBuilder("UPDATE ").append(tbName).append(" SET ");
		sb.append(colNames[0]).append("=?");
		for (int i = 1; i < colNames.length; i++) {
			sb.append(",").append(colNames[i]).append("=?");
		}
		sb.append(" WHERE ");
		sb.append(idNames[0]).append("=?");
		for (int i = 1; i < idNames.length; i++) {
			sb.append(" and ").append(idNames[i]).append("=?");
		}
		return sb.toString();
	}

	public static String insert(String tbName, String[] colNames){
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(tbName);
		Joiner joiner = Joiner.on(",");
		String cols = joiner.join(colNames);
		sb.append(" (").append(cols).append(") VALUES (?");
		int size = colNames.length;
		for (int i = 1; i < size; i++) {
			sb.append(",?");
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * replace 濡傛灉瀛樺湪瀵瑰簲涓婚敭 鍏堝垹闄�,鍦ㄦ彃鍏�
	 * @param name
	 * @param colNames
	 * @return
	 */
	public static String replace(String tbName, String[] colNames) {
		
		StringBuilder sb = new StringBuilder("REPLACE INTO ").append(tbName);
		Joiner joiner = Joiner.on(",");
		String cols = joiner.join(colNames);
		sb.append(" (").append(cols).append(") VALUES (?");
		int size = colNames.length;
		for (int i = 1; i < size; i++) {
			sb.append(",?");
		}
		sb.append(")");
		return sb.toString();
	}
}
