package idevgame.meteor.data;

import java.sql.SQLException;
import java.util.List;

import idevgame.meteor.db.Dao;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 鍚屾鐗堟暟鎹搷浣�
 * @author moon
 */
public class SyncData implements IData {

	@Override
	public <T extends BasePo> T findById(Class<T> cls, Object... ids) {
		return Dao.getDao(cls).findById(ids);
	}

	@Override
	public <T extends BasePo> List<T> findAll(Class<T> cls) {
		return Dao.getDao(cls).findAll();
	}

	@Override
	public <T extends BasePo> List<T> findByProp(Class<T> cls, String[] props, Object[] values) {
		return Dao.getDao(cls).findByProp(props, values);
	}

	@Override
	public <T extends BasePo> List<T> findBySQL(Class<T> cls, String sql, Object[] values) {
		return Dao.getDao(cls).findBySQL(sql, values);
	}

	@Override
	public <T extends BasePo> void execute(Class<T> cls, String sql, Object[] values) throws SQLException {
		Dao.getDao(cls).execute(sql, values);
	}

	@Override
	public <T extends BasePo, V> V query(Class<T> cls, String sql, ResultSetHandler<V> handler, Object... params) throws SQLException {
		return Dao.getDao(cls).query(sql, handler, params);
	}

	@Override
	public <T extends BasePo> int deleteById(Class<T> cls, Object... ids) {
		return Dao.getDao(cls).deleteById(ids);
	}

	@Override
	public <T extends BasePo> int deleteByProp(Class<T> cls, String[] propName, Object[] values) {
		return Dao.getDao(cls).deleteByProp(propName, values);
	}

	@Override
	public <T extends BasePo> int deleteBySQL(Class<T> cls, String sql, Object[] values) {
		return Dao.getDao(cls).deleteBySQL(sql, values);
	}

	@Override
	public <T extends BasePo> int delete(Class<T> cls, T t) {
		return Dao.getDao(cls).delete(t);
	}

	@Override
	public <T extends BasePo> boolean update(Class<T> cls, T t) {
		return Dao.getDao(cls).update(t);
	}
	
	@Override
	public <T extends BasePo> int replace(Class<T> cls, T t) throws SQLException {
		return Dao.getDao(cls).replace(t);
	}

	@Override
	public <T extends BasePo> void insert(Class<T> cls, T t) throws SQLException {
		Dao.getDao(cls).insert(t);
	}

	@Override
	public <T extends BasePo> void updateWithKey(Class<T> cls, T t, Object oldId) {
		((CachedDao<T>) Dao.getDao(cls)).updateWithKey(t, oldId);
	}

	@Override
	public <T extends BasePo> T get(Class<T> cls, Object id) {
		return ((CachedDao<T>) Dao.getDao(cls)).get(id);
	}

	@Override
	public <T extends BasePo> List<T> getList(Class<T> cls, Object id) {
		return ((CachedDao<T>) Dao.getDao(cls)).getList(id);
	}

	@Override
	public <T extends BasePo> T getOne(Class<T> cls, Object id, Object subId,boolean isFlushCache) {
		return ((CachedDao<T>) Dao.getDao(cls)).getOne(id, subId,isFlushCache);
	}

	@Override
	public <T extends BasePo> T getFromDb(Class<T> cls, Object id) {
		return ((CachedDao<T>) Dao.getDao(cls)).getFromDb(id);
	}

	@Override
	public <T extends BasePo> List<T> getListFromDb(Class<T> cls, Object id) {
		return ((CachedDao<T>) Dao.getDao(cls)).getListFromDb(id);
	}

	@Override
	public boolean patrol() {
//		RedisCache redis = SpringContext.getBean(RedisCache.class);
//		boolean isRunning = redis.check();
//		if (!isRunning) {
//			return false;
//		} else {
//			return true;
//		}
		return false;
	}
	
	@Override
	public <T extends BasePo> void listDelay(Class<T> cls, Object id) {
		((CachedDao<T>) Dao.getDao(cls)).listDelay(id);
	}
	
	@Override
	public <T extends BasePo> Long insertReturnId(Class<T> cls, T t)
			throws SQLException {
		return Dao.getDao(cls).insertReturnId(t);
	}
	
}
