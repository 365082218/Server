package idevgame.meteor.data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * 
* @ClassName: ListBeanSethandler 
* @Description: 鎶婄粨鏋滃皝瑁呮垚bean鏀惧叆list
* @author 鍒橀敠鏂�
* @date 2015骞�8鏈�19鏃� 涓婂崍11:59:15 
*
 */
public class ListBeanSethandler implements ResultSetHandler, Serializable {

	/** 
	* 
	*/ 
	private static final long serialVersionUID = 3102987864352269823L;
	private Class<?> clazz;
    public ListBeanSethandler(Class<?> clazz){
	         this.clazz = clazz;
	     }
    @Override
    public Object handle(ResultSet rs) throws SQLException {
	        try{
	            List<Object> list = new ArrayList<Object>();
	            while(rs.next()){
	                 Object bean = clazz.newInstance();
	                 
	                ResultSetMetaData  metadata = rs.getMetaData();
	                 int count = metadata.getColumnCount();
	                 for(int i=0;i<count;i++){
	                    String name = metadata.getColumnName(i+1);
	                    Object value = rs.getObject(name);
                    
	                     Field f = bean.getClass().getDeclaredField(name);
	                     f.setAccessible(true);
	                     f.set(bean, value);
	                 }
	                 list.add(bean);
	             }
	            return list.size()>0?list:null;
	         
	         }catch (Exception e) {
	             throw new RuntimeException(e);
	         }
	     }
	 }

