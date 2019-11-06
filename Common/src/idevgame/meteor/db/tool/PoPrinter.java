package idevgame.meteor.db.tool;


import com.google.common.base.Charsets;
import com.google.common.io.Files;

import idevgame.meteor.utils.DirUtils;
import idevgame.meteor.utils.StringUtils;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * PO鎵撳嵃鏈�4.2
 *
 * @author moon
 *
 */
public class PoPrinter {

	/**
	 * 鎵撳嵃
	 *
	 * @param ds
	 *            鏁版嵁婧�
	 * @param printPath
	 *            鎵撳嵃璺緞
	 * @param packageName
	 *            甯屾湜鎵撳嵃鐨勫寘鍚�
	 *
	 * 鎵撳嵃缁撴灉浼氭寜鐓bNamePO鍛藉悕
	 */
	public static void print(DataSource ds, String printPath,
							 String packageName) throws Exception {

		try {
			System.out.println("鎵撳嵃璺緞锛�"+printPath+",甯屾湜鎵撳嵃鐨勫寘鍚�:"+packageName);
			
			Connection conn = ds.getConnection();

			ResultSet tbRs = conn.getMetaData().getTables(null, null, null, null);

			List<String> tbNames = new LinkedList<>();

			while(tbRs.next()) {
				String tName = tbRs.getString("TABLE_NAME");
				tbNames.add(tName);
			}

			for (String tbName : tbNames) {

				ResultSet rs = conn.getMetaData().getColumns(null, null, tbName, null);

				String poName = StringUtils.underlineToUpperCamal(tbName)+"Po";
				String basePoName = "Base"+poName;

				List<String> colNames = new ArrayList<>();
				List<String> colTypes = new ArrayList<>();
				List<String> staticColNames = new ArrayList<>();
				List<String> remarks = new ArrayList<>();
				List<String> defaults = new ArrayList<>();

				while (rs.next()) {
					String cname = rs.getString("COLUMN_NAME");
					String ctype = rs.getString("TYPE_NAME");
					ctype = sqlTypeToJavaType(ctype);
					colNames.add(StringUtils.underlineToLowerCamal(cname));
					colTypes.add(ctype);
					staticColNames.add("PROP_"+StringUtils.camalToUnderline(cname).toUpperCase());
					remarks.add(rs.getString("REMARKS"));

					String def = rs.getString("COLUMN_DEF");
					if (def != null) {
						if (ctype.contains("Long")) {
							def += "L";
						}

						if (ctype.contains("String")) {
							def = "\"" + def + "\"";
						}
					}

					defaults.add(def);
				}

				// 妫�鏌ヨ矾寰�
				DirUtils.mkdir(printPath + File.separator + "base");

				// 寮�濮嬫墦鍗癰ase绫�
				BufferedWriter writer = Files.newWriter(new File(printPath + File.separator + "base" + File.separator + basePoName + ".java"), Charsets.UTF_8);
				PrintWriter out = new PrintWriter(writer);
				out.println("package "+packageName +".base;");
				out.println();
				out.println("import net.moon.frame.data.*;");
				out.println();
				out.println("/**");
				out.println(" * this class file is auto output by net.moon.frame.db.tool.PoPrinter");
				out.println(" * @author moon");
				out.println(" * @email chaopeng@chaopeng.me");
				out.println(" * @see net.moon.frame.db.tool.PoPrinter");
				out.println(" */");
				out.println("public abstract class " + basePoName + " extends BasePo {");
				out.println();
				// 鎵撳嵃闈欐�佸瓧娈�
				for (int i = 0; i < staticColNames.size(); i++) {
					out.println("\tpublic static final String " + staticColNames.get(i) + " = \"" + colNames.get(i) + "\";");
				}
				out.println();
				// 鎵撳嵃瀵硅薄瀛楁
				for (int i = 0; i < colNames.size(); i++) {
					out.println("\t/**"+remarks.get(i)+"*/");
					out.println("\tprivate " + colTypes.get(i) + " " + colNames.get(i) + " = " + defaults.get(i) + ";" );
				}
				out.println();

				// 鎵撳嵃getset
				for (int i = 0; i < colNames.size(); i++) {
					out.println("\t/**get "+remarks.get(i)+"*/");
					out.println("\tpublic " + colTypes.get(i) + " get" + StringUtils.upFirst1(colNames.get(i)) + "(){");
					out.println("\t\treturn " + colNames.get(i) + ";");
					out.println("\t}");
					out.println();

					out.println("\t/**set "+remarks.get(i)+"*/");
					out.println("\tpublic void set" + StringUtils.upFirst1(colNames.get(i)) + "(" + colTypes.get(i) + " " + colNames.get(i) + "){");
					out.println("\t\tthis." + colNames.get(i) + " = " + colNames.get(i) + ";");
					out.println("\t}");
					out.println();
				}

				// 鎵撳嵃props()
				out.println("\t@Override");
				out.println("\tpublic String[] props() {");
				out.println("\t\treturn new String[]{\"`" + StringUtils.join("`\" ,\"`", colNames) + "`\"};");
				out.println("\t}");

				// 鎵撳嵃propValues()
				out.println("\t@Override");
				out.println("\tpublic Object[] propValues() {");
				out.println("\t\treturn new Object[]{" + StringUtils.join(" ,", colNames) + "};");
				out.println("\t}");

				out.println("}");
				out.close();
				writer.close();

				File poFile = new File(printPath+File.separator+poName+".java");
				if(!poFile.exists()) {

					writer = Files.newWriter(poFile, Charsets.UTF_8);
					out = new PrintWriter(writer);
					out.println("package " + packageName + ";");
					out.println();
					out.println("import " + packageName + ".base." + basePoName + ";");
					out.println("import net.moon.frame.data.*;");
					out.println();
					out.println("/**");
					out.println(" * this class file is auto output by net.moon.frame.db.tool.POPrinter");
					out.println(" * @author moon");
					out.println(" * @email chaopeng@chaopeng.me");
					out.println(" * @see net.moon.frame.db.tool.PoPrinter");
					out.println(" */");
					out.println("@PO(\"" + tbName + "\")");
					out.println("public class " + poName + " extends " + basePoName + "{");
					out.println();

					// 鎵撳嵃ids()
					out.println("\t@Override");
					out.println("\tpublic String[] ids() {");
					out.println("\t\t//TODO");
					out.println("\t}");

					// 鎵撳嵃idValues()
					out.println("\t@Override");
					out.println("\tpublic Object[] idValues() {");
					out.println("\t\t//TODO");
					out.println("\t}");

					out.println("}");
					out.close();
					writer.close();
				}
				System.out.println("鐢熸垚琛細"+conn.getCatalog()+"."+tbName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static String sqlTypeToJavaType(String type){

		if(type.equals("BIGINT")){
			return "java.lang.Long";
		}
		if(type.equals("SMALLINT")){
			return "java.lang.Short";
		}
		if(type.contains("INT")){
			return "java.lang.Integer";
		}
		if(type.equals("FLOAT")){
			return "java.lang.Float";
		}
		if(type.equals("DOUBLE")){
			return "java.lang.Float";
		}
		if(type.contains("CHAR") || type.contains("TEXT")){
			return "java.lang.String";
		}
		if(type.contains("BINARY") || type.contains("BLOB")){
			return "byte[]";
		}
		if(type.contains("DATE") || type.contains("TIME")){
			return "java.util.Date";
		}

		// 鍏朵粬绫诲瀷鎷滄墭涓嶈鐢ㄤ簡
		throw new RuntimeException("unsupported type = " + type);
	}

}