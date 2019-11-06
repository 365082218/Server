package idevgame.meteor.data;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import idevgame.meteor.db.Dao;
import idevgame.meteor.redis.RedisCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缂撳瓨Dao锛屽淇敼绫荤殑
 *
 * @author moon
 * @see Cacheable
 */
public class CachedDao<T extends BasePo> extends Dao<T> {

	private Logger logger = LoggerFactory.getLogger(CachedDao.class);
	protected boolean isMany;
	protected Field keyField;
	protected Field subkeyField;
	protected String manyInitSql;
	protected RedisCache cache;

	public CachedDao(Class<T> cls) {
		super(cls);
		try {
			Cacheable cacheable = cls.getAnnotation(Cacheable.class);
			this.manyInitSql = cacheable.manyInitSql();
			this.keyField = cls.getSuperclass().getDeclaredField(cacheable.key());
			this.keyField.setAccessible(true);
			if(cacheable.subkey().length() > 0) {
				this.isMany = true;
				this.subkeyField = cls.getSuperclass().getDeclaredField(cacheable.subkey());
				this.subkeyField.setAccessible(true);
			}
		} catch (Exception e) {
			logger.error("!!!", e);
		}
	}

	@Deprecated
	@Override
	public T findById(Object... ids) {
		throw new RuntimeException("findById is blocked in CachedDao!");
	}

	@Override
	public List<T> findByProp(String[] propName, Object[] values) {
		logger.warn("findByProp is not recommended in CachedDao!");
		return super.findByProp(propName, values);
	}

	@Override
	public List<T> findBySQL(String sql, Object[] values) {
		logger.warn("findBySQL is not recommended in CachedDao!");
		return super.findBySQL(sql, values);
	}

	@Override
	public int deleteById(Object... ids) {
		throw new RuntimeException("deleteById is blocked in CachedDao!");
	}

	@Deprecated
	@Override
	public int deleteByProp(String[] propName, Object[] values) {
		throw new RuntimeException("deleteByProp is blocked in CachedDao!");
	}

	@Deprecated
	@Override
	public int deleteBySQL(String sql, Object[] values) {
		throw new RuntimeException("deleteBySQL is blocked in CachedDao!");
	}

	@Override
	public int delete(T t) {
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);
			cache.mapDelete(key, subkey);
		}
		else {
			String key = getKey(t);
			cache.delete(key);
		}

		return super.deleteById(t.idValues());
	}
	
	

	@Override
	public boolean update(T t) {
		if (isMany) {
			
			check(t);
			
			String key = getKey(t);
			String subkey = getSubkey(t);
			
			try {
				cache.mapSet(key, subkey, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		else {
			String key = getKey(t);

			try {
				if(!cache.update(key, t)){
					return false;
				}
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		return super.update(t);
	}
	
	@Override
	public int replace(T t) throws SQLException {
		if (isMany) {
			
			check(t);
			
			String key = getKey(t);
			String subkey = getSubkey(t);
			try {
				cache.mapSet(key, subkey, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		} else {
			
			String key = getKey(t);

			try {
				cache.set(key, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}
		
		return super.replace(t);
	}

	@Override
	public void insert(T t) throws SQLException {
		super.insert(t);

		if (isMany) {
			
			check(t);
			
			String key = getKey(t);
			String subkey = getSubkey(t);

			try {
				cache.mapSet(key, subkey, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		else {
			String key = getKey(t);

			try {
				cache.add(key, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}
	}

	/**
	 * 涓�瀵瑰鍏崇郴涓秹鍙婁富key鏇存柊鐨剈pdate
	 */
	public void updateWithKey(T t, Object oldId){
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}
		
		check(t);

		String oldKey = getKey(oldId);
		String newKey = getKey(t);
		String subkey = getSubkey(t);

		cache.mapDelete(oldKey, subkey);
		cache.mapSet(newKey, subkey, t);

		super.update(t);
	}

	@Override
	public void execute(String sql, Object... params) throws SQLException {
		super.execute(sql, params);
		logger.warn("execute is not recommended in CachedDao!");
	}

	/**
	 * 閫氳繃id鑾峰彇涓�鏉℃暟鎹紝閫傜敤浜嶤acheType.ONE
	 * 寮哄埗浠庢暟鎹簱鍒锋柊缂撳瓨
	 *
	 * @param id
	 * @return
	 */
	public T getFromDb(Object id){
		if (isMany) {
			throw new RuntimeException("get is blocked in CacheType MANY!");
		}
		// 浠庢暟鎹簱load鏁版嵁
		T t = super.findById(id);
		if (t != null) {
			logger.debug("load data from db, key=" + getKey(t));
			try {
				cache.set(getKey(t), t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		return t;
	}

	/**
	 * 閫氳繃id鑾峰彇涓�鏉℃暟鎹紝閫傜敤浜嶤acheType.ONE
	 *
	 * @param id
	 * @return
	 */
	public T get(Object id) {
		if (isMany) {
			throw new RuntimeException("get is blocked in CacheType MANY!");
		}

		T t = null;
		try {
			t = cache.get(getKey(id), cls);
		} catch (RuntimeException e) {
			logger.error("cache error cache is null?="+(cache == null), e);
		}

		// 浠庢暟鎹簱load鏁版嵁
		if (t == null) {
			t = super.findById(id);
			if (t != null) {
				logger.debug("load data from db, key="+getKey(t));
				try {
					cache.set(getKey(t), t);
				} catch (RuntimeException e) {
					logger.error("cache error cache is null?="+(cache == null), e);
				}
			}
		}

		return t;
	}

	/**
	 * 閫氳繃id鑾峰彇涓�缁勬暟鎹紝閫傜敤浜嶤acheType.MANY
	 * 寮哄埗鍒锋柊缂撳瓨
	 *
	 * @param id
	 * @return
	 */
	public List<T> getListFromDb(Object id) {
		// 浠庢暟鎹簱load鏁版嵁
		Map<String, T> map = new HashMap<>();
		List<T> ls = super.findBySQL(manyInitSql, new Object[]{id});
		if (ls != null && ls.size() > 0) {
			logger.debug("load data from db, key=" + getKey(id));

			for (T t : ls) {
				map.put(getSubkey(t), t);
			}
			try {
				cache.mapSetAll(getKey(id), map);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}else{
			cache.delete(getKey(id));
		}

		return new LinkedList<>(map.values());
	}

	/**
	 * 閫氳繃id鑾峰彇涓�缁勬暟鎹紝閫傜敤浜嶤acheType.MANY
	 *
	 * @param id
	 * @return
	 */
	public List<T> getList(Object id){
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}

		Map<String, T> map = null;
		try{
			map = cache.mapGetAll(getKey(id), cls);
		} catch (RuntimeException e) {
			logger.error("cache error", e);
		}

		// 浠庢暟鎹簱load鏁版嵁
		if (map == null || map.size() == 0) {
			map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[]{id});
			if (ls != null && ls.size()>0) {
				logger.debug("load data from db, key=" + getKey(id));

				for (T t : ls) {
					map.put(getSubkey(t), t);
				}
				try {
					cache.mapSetAll(getKey(id), map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}
		}else{
			cache.mapSetDelay(getKey(id));//鑷姩寤舵湡
		}
		return new LinkedList<>(map.values());
	}

	/**
	 * 浠庝竴瀵瑰鍏崇郴涓幏鍙栦竴鏉℃暟鎹�
	 * @param id
	 * @param subId
	 * @return
	 */
	public T getOne(Object id, Object subId,boolean isFlushCache){
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}

		String key = getKey(id);
		String subkey = getSubkey(subId);

		boolean exists = false;
		try {
			exists = cache.exists(key);
			if (exists) {
				T rsObj = cache.mapGet(key, subkey, cls);
				if(rsObj != null){
					return rsObj;
				}//鍚﹀垯灏变粠鏁版嵁搴撴煡涓�娆�
			}
		} catch (RuntimeException e) {
			logger.error("cache error", e);
		}
		// 浠庢暟鎹簱load鏁版嵁
		if(isFlushCache){
			HashMap<String, T> map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[]{id});
			if (ls != null && ls.size()>0) {
				logger.debug("load data from db, key=" + getKey(id));
				for (T t : ls) {
					map.put(getSubkey(t), t);
				}
				try {
					cache.mapSetAll(getKey(id), map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}
			return map.get(getSubkey(subId));
		}else{
			return super.findById(subId);
		}
	}

	/**
	 * 寮傛鍒犻櫎 1.鍒犻櫎缂撳瓨
	 *
	 * @param t
	 * @return
	 */
	public int asyncDelete1(T t) {
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);
			return cache.mapDelete(key, subkey) ? 1 : 0;
		}
		else {
			String key = getKey(t);
			return cache.delete(key) ? 1 : 0;
		}
	}

	/**
	 * 寮傛鍒犻櫎 2.鍒犻櫎鏁版嵁搴�
	 *
	 * @param t
	 * @return
	 */
	public int asyncDelete2(T t) {
		return super.deleteById(t.idValues());
	}

	/**
	 * 寮傛鏇存柊 1.鏇存柊缂撳瓨
	 *
	 * @param t
	 * @return
	 */
	public boolean asyncUpdate1(T t) {
		if (isMany) {
			
			check(t);
			
			String key = getKey(t);
			String subkey = getSubkey(t);

			cache.mapSet(key, subkey, t);
		}
		else {
			String key = getKey(t);

			if(!cache.update(key, t)){
				return false;
			}
		}
		return true;
	}

	/**
	 * 寮傛鏇存柊 2.鏇存柊鏁版嵁搴�
	 *
	 * @param t
	 * @return
	 */
	public boolean asyncUpdate2(T t) {
		return super.update(t);
	}
	
	public int asynReplace(T t)  {
		try {
			return super.replace(t);
		} catch (SQLException e) {
			logger.error("cache error", e);
		}
		return -1;
	}
	/**
	 * 寮傛 涓�瀵瑰鍏崇郴涓秹鍙婁富key鏇存柊鐨剈pdate 1. 鏇存柊缂撳瓨
	 */
	public void asyncUpdateWithKey1(T t, Object oldId){
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}
		
		check(t);

		String oldKey = getKey(oldId);
		String newKey = getKey(t);
		String subkey = getSubkey(t);

		cache.mapDelete(oldKey, subkey);
		cache.mapSet(newKey, subkey, t);
	}

	protected String getKey(T obj) {
		try {
			return cls.getSimpleName() + "_" + keyField.get(obj);
		} catch (IllegalAccessException e) {
			logger.error("!!!", e);
		}

		return "";
	}

	protected String getSubkey(T obj) {
		if(isMany) {
			try {
				return String.valueOf(subkeyField.get(obj));
			} catch (IllegalAccessException e) {
				logger.error("!!!", e);
			}
		}
		return "";
	}

	protected String getKey(Object id) {
		return cls.getSimpleName() + "_" + id;
	}

	protected String getSubkey(Object subId) {
		return String.valueOf(subId);
	}

	protected String getMarkKey(Object... ids) {
		if (isMany) {
			if (ids.length != 2) {
				throw new RuntimeException("must give key and subkey");
			}

			String key = getKey( ids[0]);
			String subkey = getSubkey( ids[1]);
			return key+subkey;
		}
		else {
			if (ids.length != 1) {
				throw new RuntimeException("must give key");
			}

			String key = getKey( ids[0]);
			return key;
		}
	}

	protected String getMarkKey(T t) {
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);

			return key+subkey;
		}
		else {
			String key = getKey(t);

			return key;
		}
	}

	protected String getOldMarkKey(Object oldId, T t) {
		if (isMany) {
			String key = getKey(oldId);
			String subkey = getSubkey(t);

			return key+subkey;
		}
		else {
			throw new RuntimeException("one to one unsupperted getOldMarkKey");
		}
	}

	public void setCache(RedisCache cache) {
		this.cache = cache;
	}
	
	/**
	 * 寤舵湡澶勭悊
	 * @param id 瀵硅薄id
	 */
	public void listDelay(Object id){
		cache.mapSetDelay(getKey(id));
	}
	
	/**
	 * 鐢ㄤ簬妫�鏌ist鍦ㄧ紦瀛樹腑鏄惁瀛樺湪锛屼笉瀛樺湪鍒欎粠鏁版嵁搴撴崬
	 * @param t
	 */
	public void check(T t){
		if(!isMany){
			return;
		}
		String key = getKey(t);
		boolean isExist = cache.exists(key);
		
		if(!isExist){ //涓嶅瓨鍦ㄥ彲鑳借杩囨湡绉婚櫎浜嗭紝闇�瑕侀噸鏂颁粠鏁版嵁搴撴崬涓�閬�
			Object id = null;
			try {
				id = keyField.get(t);
			} catch (IllegalAccessException e) {
				logger.error("CacheDao.check", e);
				return;
			}
			Map<String, T> map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[]{id});
			if (ls != null && ls.size()>0) {
				logger.debug("load data from db, key=" + getKey(id));

				for (T t1 : ls) {
					map.put(getSubkey(t1), t1);
				}
				try {
					cache.mapSetAll(key, map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}
		}
	}
}
