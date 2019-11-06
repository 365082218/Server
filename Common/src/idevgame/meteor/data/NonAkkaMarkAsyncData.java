package idevgame.meteor.data;

import idevgame.meteor.db.Dao;
import idevgame.meteor.utils.OrderedThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NonAkkaAsyncData
 *
 * 涓嶄繚璇佺敤鎴锋暟鎹寜鎻愪氦椤哄簭鎵ц锛屽彧淇濊瘉鍚屼竴鏉℃暟鎹寜椤哄簭鎵ц锛屾敮鎸佺姸鎬佸悎骞�
 *
 * @author moon
 * @version 2.0 - 2014-05-30
 */
public final class NonAkkaMarkAsyncData extends SyncData {

	private final static Logger logger = LoggerFactory.getLogger(NonAkkaMarkAsyncData.class);

	private final Map<String, BasePo> map = new ConcurrentHashMap<>();
	private final OrderedThreadPoolExecutor executor = OrderedThreadPoolExecutor.newFixesOrderedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public <T extends BasePo> int delete(Class<T> cls, final T t) {
		Dao<T> dao = Dao.getDao(cls);
		if(dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;
			String markKey = d.getMarkKey(t);
			try {
				int res = d.asyncDelete1(t);

				map.remove(markKey);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override
					public void run() {
						d.asyncDelete2(t);
					}
				});
				return res;
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				return d.asyncDelete2(t);
			}
		}
		else {
			return super.delete(cls, t);
		}
	}

	@Override
	public <T extends BasePo> boolean update(Class<T> cls, final T t) {
		Dao<T> dao = Dao.getDao(cls);
		if(dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;
			final String markKey = d.getMarkKey(t);
			try {
				boolean res = d.asyncUpdate1(t);

				map.put(markKey, t);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override
					public void run() {
						Object o = map.remove(markKey);
						if (o!=null) {
							d.asyncUpdate2((T) o);
						}
					}
				});
				return res;
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				return d.asyncUpdate2(t);
			}
		}
		else {
			return super.update(cls, t);
		}
	}
	
	@Override
	public <T extends BasePo> int replace(Class<T> cls, T t) throws SQLException {
		Dao<T> dao = Dao.getDao(cls);
		if(dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;
			final String markKey = d.getMarkKey(t);
			try {
				Boolean res = d.asyncUpdate1(t);
				map.put(markKey, t);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override
					public void run() {
						Object o = map.remove(markKey);
						if (o!=null) {
							d.asynReplace((T) o);
						}
					}
				});
				return 0;
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				d.asynReplace(t);
				return 0;
			}
		}
		else {
			return super.replace(cls, t);
		}
		
	}

	@Override
	public <T extends BasePo> void updateWithKey(Class<T> cls, T t, Object oldId) {
		Dao<T> dao = Dao.getDao(cls);
		if(dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;
			String oldMarkKey = d.getOldMarkKey(oldId, t);
			map.remove(oldMarkKey);
			final String markKey = d.getMarkKey(t);

			try {
				d.asyncUpdateWithKey1(t, oldId);

				map.put(markKey, t);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override
					public void run() {
						Object o = map.remove(markKey);
						if (o!=null) {
							d.asyncUpdate2((T) o);
						}
					}
				});
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				d.asyncUpdate2(t);
			}
		} else {
			throw new RuntimeException("un cached dao is not support updateWithKey, use update instand.");
		}
	}

	public void shutdown(){
		this.executor.shutdown();
	}
}
