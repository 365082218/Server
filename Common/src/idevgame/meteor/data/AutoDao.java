package idevgame.meteor.data;


import idevgame.meteor.db.Dao;
import idevgame.meteor.db.PoRegister;
import idevgame.meteor.redis.RedisCache;

import javax.sql.DataSource;
import java.util.Set;

/**
 * AutoDao - dao自动匹配
 *
 * @author moon
 * @version 2.0 - 2014-03-20
 */
public class AutoDao {

	/**
	 * 
	 * @param path
	 * @param ds
	 * @param cache
	 * @param checkTable 是否要检查表字段
	 */
	public static void scan(String path, DataSource ds, RedisCache cache,boolean checkTable) {
		Set<Class> classes = PoRegister.scan(path, ds,checkTable);

		for (Class cls : classes) {
			if(cls.getAnnotation(Cacheable.class)!=null){
				CachedDao cachedDao = new CachedDao(cls);
				cachedDao.setCache(cache);
			}
			else {
				new Dao(cls);
			}
		}
		
		System.out.println("數據庫表數據長度:"+classes.size());
	}

}
