package idevgame.meteor.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 灏嗙粨鏋滄斁鍦�2缁磍ist
 * @author chen 2014骞�11鏈�25鏃�
 *
 */
public class ListResultSetHandler implements ResultSetHandler<Object>, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -46070344702969983L;
	
	private int col;//杩斿洖缁撴灉涓�琛岀殑闀垮害
	
	/**
	 * 鍒楁暟,杩斿洖缁撴灉涓�琛岀殑闀垮害
	 * @param col
	 */
	public ListResultSetHandler(int col){
		this.col = col;		
	}

	@Override
	public Object handle(ResultSet rs) throws SQLException {
		
		List<ArrayList<Object>> total = new ArrayList<>();
		while (rs.next()) {
			ArrayList<Object> oneElementList = new ArrayList<>();
			for (int i = 1; i <= col; i++) {
				oneElementList.add(rs.getObject(i));
			}
		    total.add(oneElementList);
		}
		return total;
	}

}
