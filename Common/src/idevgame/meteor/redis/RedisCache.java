package idevgame.meteor.redis;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * 閸╄櫣顢卌ache閸旂喕鍏�
 * @author moon
 */
public class RedisCache extends RedisClient {

	public RedisCache(String ip, int port, int expireTime,String password) {
		super(ip, port, expireTime,password);
	}

	public RedisCache(String ip, int port, int maxActive, int maxIdle, int maxWait, int expireTime,String password) {
		super(ip, port, maxActive, maxIdle, maxWait, expireTime,password);
	}


	/*------------------------------------------
	 * 閸╄櫣顢卌ache
	 *------------------------------------------*/
	
	public boolean check() {
		Jedis jedis = null;
		try {
			jedis = getConnect();
		} catch (Exception e) {
			return false;
		} finally {
			returnConnect(jedis);
		}
		
		return true;
	}

	/**
	 * 濡拷閺岊櫛ey閺勵垰鎯佺�涙ê婀�
	 */
	public boolean exists(String key){
		Jedis jedis = null;

		try {
			jedis = getConnect();

			if (expireTime==0) {
				return jedis.exists(key);
			} else {
				return jedis.expire(key, expireTime) == 1;
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * set
	 */
	public void set(String key, Object value) {
		Jedis jedis = null;
		String v = encode(value);
		try {
			jedis = getConnect();

			if (expireTime==0) {
				jedis.set(key, v);
			} else {
				jedis.setex(key, expireTime, v);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * get
	 */
	public <T> T get(String key, Class<T> cls){
		Jedis jedis = null;

		try {
			jedis = getConnect();
			String value = jedis.get(key);
			return decode(value, cls);

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * add
	 */
	public boolean add(String key, Object value){
		Jedis jedis = null;
		String v = encode(value);
		String res;
		try {
			jedis = getConnect();
			if (expireTime==0) {
				res = jedis.set(key, v, RedisClient.NX);
			} else {
				res = jedis.set(key, v, RedisClient.NX, RedisClient.EX, expireTime);
			}
			return res == null ? false : res.equals("OK");
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * update
	 */
	public boolean update(String key, Object value){
		Jedis jedis = null;
		String v = encode(value);
		String res;
		try {
			jedis = getConnect();
			if (expireTime==0) {
				res = jedis.set(key, v);
			} else {
				res = jedis.setex(key, expireTime, v);
			}
			return res.equals("OK");
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * delete
	 */
	public boolean delete(String key){
		Jedis jedis = null;
		try {
			jedis = getConnect();
			Long res = jedis.del(key);
			return res == 1;
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * increase
	 */
	public Long increase(String key){
		Jedis jedis = null;
		try {
			jedis = getConnect();
			return jedis.incr(key);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * decrease
	 */
	public Long decrease(String key){
		Jedis jedis = null;
		try {
			jedis = getConnect();
			return jedis.decr(key);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/*------------------------------------------
	 * map cache
	 *------------------------------------------*/

	/**
	 * set one key in a map
	 */
	public void mapSet(String key, String subkey, Object value){
		Jedis jedis = null;
		String v = encode0(value);
		try {
			jedis = getConnect();
			jedis.hset(key, subkey, v);
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception, key="+key+",subkey="+subkey, e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * set a map totally
	 */
	public <T> void mapSetAll(String key, Map<String, T> map){
		Jedis jedis = null;

		Map<String, String> map1 = encodeMap(map);
		try {
			jedis = getConnect();
			jedis.del(key);
			jedis.hmset(key, map1);
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception key="+key, e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}
	
	/**
	 * Delay for one key
	 * 
	 */
	public void mapSetDelay(String key){
		Jedis jedis = null;
		try {
			jedis = getConnect();
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * get one key in the map
	 */
	public <T> T mapGet(String key, String subkey, Class<T> cls){
		Jedis jedis = null;

		try {
			jedis = getConnect();
			String s = jedis.hget(key, subkey);
			return decode0(s, cls);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * get a map totally
	 */
	public <T> Map<String, T> mapGetAll(String key, Class<T> cls) {
		Jedis jedis = null;

		try {
			jedis = getConnect();
			Map<String, String> res = jedis.hgetAll(key);
			return decodeMap(res, cls);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * delete one key in map
	 */
	public boolean mapDelete(String key, String subkey){
		Jedis jedis = null;

		try {
			jedis = getConnect();
			Long res = jedis.hdel(key, subkey);
			return res == 1;
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/*------------------------------------------
	 * list cache
	 *------------------------------------------*/

	/**
	 * push to the head of list
	 */
	public void listLeftPush(String key, Object obj) {
		Jedis jedis = null;
		String v = encode0(obj);
		try {
			jedis = getConnect();
			jedis.lpush(key, v);
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * push to the tail of list
	 */
	public void listRightPush(String key, Object obj) {
		Jedis jedis = null;
		String v = encode0(obj);
		try {
			jedis = getConnect();
			jedis.rpush(key, v);
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * pop from the head of list
	 */
	public <T> T listLeftPop(String key, Class<T> cls){
		Jedis jedis = null;

		try {
			jedis = getConnect();
			String s = jedis.lpop(key);
			return decode0(s, cls);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * pop from the tail of list
	 */
	public <T> T listRightPop(String key, Class<T> cls){
		Jedis jedis = null;

		try {
			jedis = getConnect();
			String s = jedis.rpop(key);
			return decode0(s, cls);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * set a list
	 */
	public void listPushAll(String key, List<Object> ls){
		Jedis jedis = null;
		String[] ls1 = encodeList(ls);
		try {
			jedis = getConnect();
			jedis.del(key);
			jedis.rpush(key, ls1);
			if (expireTime!=0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * get a range from list
	 */
	public <T> List<T> listRange(String key, int begin, int end, Class<T> cls) {
		Jedis jedis = null;
		try {
			jedis = getConnect();
			List<String> ls = jedis.lrange(key, begin, end);
			return decodeList(ls, cls);

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}
}
