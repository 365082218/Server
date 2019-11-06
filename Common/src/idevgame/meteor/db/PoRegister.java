package idevgame.meteor.db;

import idevgame.meteor.data.BasePo;
import idevgame.meteor.utils.ClassPathScanner;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * po娉ㄥ唽
 *
 * @author moon
 */
public class PoRegister {

	private final static Map<Class, TableObject> TABLE_OBJECTS = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(PoRegister.class);

	public static Set<Class> scan(String path, DataSource ds,boolean checkTable) {
		boolean isErr = false;
		Set<Class> classes = ClassPathScanner.scan(path, false, true, false, null);
		try {
			Connection conn = ds.getConnection();
			for (Class cls : classes) {
				TableObject table = new TableObject((Class<? extends BasePo>) cls, ds);
				synchronized (TABLE_OBJECTS) {
					TABLE_OBJECTS.put(cls, table);
				}
				// 妫�鏌ュ瓧娈垫坊鍔犳暟鎹簱浜嗘病
				ResultSet rst = conn.getMetaData().getColumns(null, null, table.tbName, null);
				Map<String, String> columnNames = new HashMap<String, String>();
				while (rst.next()) {
					String cname = rst.getString("COLUMN_NAME");
					columnNames.put(cname, cname);
				}
				for (String columnName : table.props) {
					columnName = columnName.replace("`", "");
					if (columnNames.get(columnName) == null) {
						isErr = true;
						logger.error("*************鏁版嵁缂哄皯瀛楁,tableName=: " +path+"."+ table.tbName+ ",columnName = " + columnName);
					}
				}
				//鏆傛椂鍙敤鍒版暟鎹簱琛ㄧ粨鏋勫姣旓紝妫�鏌ュ悗璁剧疆null
				table.props = null;
				rst.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(isErr && checkTable){//琛ㄥ瓧娈典笉瀵瑰簲鐩存帴杩斿洖null鎶ラ敊鍚�
			throw new RuntimeException("鏁版嵁搴撳瓧娈典笉瀵瑰簲,璇锋鏌�! ");
		}
		return classes;
	}

	public static TableObject getTableObject(Class<?> cls){
		return TABLE_OBJECTS.get(cls);
	}

}
