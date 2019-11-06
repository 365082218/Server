package idevgame.meteor.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.data.BasePo;

/**
 * @author moon
 */
public class Dao<T extends BasePo> {

	private final static Map<Class, Dao> daos = new HashMap<>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Class<T> cls;
	protected final BeanHandler<T> beanHandler;
	protected final BeanListHandler<T> beanListHandler;
	protected final TableObject tableObject;

	public static <T extends BasePo> Dao<T> getDao(Class<T> cls) {
		return daos.get(cls);
	}

	public Dao(Class<T> cls) {
		this.cls = cls;
		this.beanHandler = new BeanHandler<>(cls);
		this.beanListHandler = new BeanListHandler<>(cls);
		this.tableObject = PoRegister.getTableObject(cls);
		daos.put(cls, this);
	}
	
	/**
	 * 鐢ㄤ簬閫氳繃涓�涓猟bserver绠＄悊澶氫釜mysql婧�
	 * @param cls
	 * @param table
	 */
	public Dao(Class<T> cls,TableObject table) {
		this.cls = cls;
		this.beanHandler = new BeanHandler<>(cls);
		this.beanListHandler = new BeanListHandler<>(cls);
		this.tableObject = table;
	}

	public T findById(Object... ids) {
		try {
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.query(tableObject.select, beanHandler, ids);
		} catch (Exception e) {
			logger.error("findById ERROR!!!", e);
		}
		return null;
	}

	public List<T> findAll() {
		try {
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.query(tableObject.selectAll, beanListHandler);
		} catch (Exception e) {
			logger.error("findAll ERROR!!!", e);
		}
		return null;
	}

	public List<T> findByProp(String[] propName, Object[] values) {
		try {
			StringBuilder sb = new StringBuilder(tableObject.selectAll);
			sb.append(" WHERE ");
			sb.append(propName[0]).append("=").append("?");
			for (int i = 1; i < propName.length; ++i) {
//				sb.append(",").append(propName[i]).append("=?");
				sb.append(" and ").append(propName[i]).append("=?");
			}
			QueryRunner qr = new QueryRunner(tableObject.ds);
//			System.out.println("sql=" + sb.toString());
			return qr.query(sb.toString(), beanListHandler, values);
		} catch (Exception e) {
			logger.error("findByProp ERROR!!!", e);
		}
		return null;
	}

	/**
	 * @param sql 鍙渶鏉′欢閮ㄥ垎
	 */
	public List<T> findBySQL(String sql, Object[] values) {
		try {
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.query(tableObject.selectAll + " WHERE " + sql, beanListHandler, values);
		} catch (Exception e) {
			logger.error("findBySQL ERROR!!!", e);
		}
		return null;
	}

	public int deleteById(Object... ids) {
		try {
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.update(tableObject.delete, ids);
		} catch (Exception e) {
			logger.error("deleteById ERROR!!!", e);
		}

		return 0;
	}

	public int deleteByProp(String[] propName, Object[] values) {
		try {
			StringBuilder sb = new StringBuilder(tableObject.deleteAll);
			sb.append(" WHERE ");
			sb.append(propName[0]).append("=").append("?");
			for (int i = 1; i < propName.length; ++i) {
				sb.append(" and ").append(propName[i]).append("=?");
			}
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.update(sb.toString(), values);
		} catch (Exception e) {
			logger.error("deleteByProp ERROR!!!", e);
		}
		return 0;
	}

	public int deleteBySQL(String sql, Object[] values) {
		try {
			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.update(tableObject.deleteAll + " WHERE " + sql, values);
		} catch (Exception e) {
			logger.error("deleteBySQL ERROR!!!", e);
		}
		return 0;
	}

	public int delete(T t) {
		return deleteById(t.idValues());
	}

	public boolean update(T t) {
		try {
			Object[] props = t.propValues();
			Object[] ids = t.idValues();
			Object[] objects = new Object[props.length + ids.length];

			System.arraycopy(props, 0, objects, 0, props.length);
			System.arraycopy(ids, 0, objects, props.length, ids.length);

			QueryRunner qr = new QueryRunner(tableObject.ds);
			return qr.update(tableObject.update, objects) > 0;
		} catch (Exception e) {
			logger.error("update ERROR!!!", e);
		}
		return false;
	}

	public int replace(T t) throws SQLException {
		QueryRunner qr = new QueryRunner(tableObject.ds);
		return qr.update(tableObject.replace, t.propValues());
	}
	public void insert(T t) throws SQLException {
		QueryRunner qr = new QueryRunner(tableObject.ds);
		qr.update(tableObject.insert, t.propValues());
	}

	public void execute(String sql, Object... params) throws SQLException {
		QueryRunner qr = new QueryRunner(tableObject.ds);
		qr.update(sql, params);
	}

	public <V> V query(String sql, ResultSetHandler<V> handler, Object... params) throws SQLException {
		QueryRunner qr = new QueryRunner(tableObject.ds);
		return qr.query(sql, handler, params);
	}
	
	public Long insertReturnId(T t) throws SQLException {
		QueryRunner qr = new QueryRunner(tableObject.ds);
		qr.update(tableObject.insert, t.propValues());
		Long id = qr.query("SELECT LAST_INSERT_ID()", new ScalarHandler<Long>(1)); 
		return id;
	}
}
