package idevgame.meteor.data;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import com.mysql.jdbc.ResultSetMetaData;

/**
 * 数据库查询的基础返回
 * @author Administrator
 *
 */
public class BaseResultSetHandler implements ResultSetHandler<Object>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2887559731557977083L;

	@Override
	public Object handle(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }
    
        int cols = 0;
        Object metaDate = rs.getMetaData();
        if(metaDate instanceof com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxyImpl){
        	com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxyImpl meta = (com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxyImpl) metaDate;
            cols = meta.getColumnCount();
        }else{
        	ResultSetMetaData meta = (ResultSetMetaData) metaDate;
        	cols = meta.getColumnCount();
        }
        
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

}