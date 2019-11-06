package idevgame.meteor.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RedisClient {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected JedisPool pool;

	/**
	 * 鐎电钖勫Ч鐘垫畱婢堆冪毈
	 */
	private final static int MAX_ACTIVE = 1000;
	/**
	 * 閺堬拷婢堆傜箽閹镐胶鈹栭梻鑼Ц閹胶娈戠�电钖�
	 */
	private final static int MAX_IDLE = 100;
	/**
	 * 閼惧嘲褰囧Ч鐘插敶鐎电钖勯張锟芥径褏鐡戝鍛闂傦拷
	 */
	private final static int MAX_WAIT = 1000;
	/**
	 * 瑜版捁鐨熼悽鈺瀘rrow Object閺傝纭堕弮璁圭礉閺勵垰鎯佹潻娑滎攽閺堝鏅ラ幀褎顥呴弻锟�
	 */
	private final static boolean TEST_ON_BORROW = true;
	/**
	 * 瑜版捁鐨熼悽鈺甧turn Object閺傝纭堕弮璁圭礉閺勵垰鎯佹潻娑滎攽閺堝鏅ラ幀褎顥呴弻锟�
	 */
	private final static boolean TEST_ON_RETURN = true;
	
	/**
	 * 鏉╃偞甯寸搾鍛鐠佸墽鐤�
	 */
	private final static int TIME_OUT = 0;

	/**
	 * 鏉╁洦婀￠弮鍫曟？閿涘奔璐�0娑撳秷绻冮張锟�
	 */
	protected int expireTime = 0;
	// default conf end

	// Redis Define Status begin

	/**
	 * ok
	 */
	public static final String OK = "OK";

	/**
	 * 娑撳秴鐡ㄩ崷锟�
	 */
	public static final String NX = "NX";

	/**
	 * 鐎涙ê婀�
	 */
	public static final String XX = "XX";

	/**
	 * 娴犮儳顫楁稉楦跨箖閺堢喎宕熸担锟�
	 */
	public static final String EX = "EX";

	/**
	 * 娴犮儲顕犵粔鎺嶈礋鏉╁洦婀￠崡鏇氱秴
	 */
	public static final String PX = "PX";
	// Redis Define Status end

	/**
	 * 娴ｈ法鏁ゆ妯款吇闁板秶鐤嗛崚娑樼紦Redis鏉╃偞甯�
	 *
	 * @param ip   redis-ip
	 * @param port redis-port
	 * @param expireTime	鏉╁洦婀￠弮鍫曟？
	 * @param password
	 */
	public RedisClient(String ip, int port, int expireTime,String password) {
		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMaxTotal(MAX_ACTIVE);
//		config.setMaxIdle(MAX_IDLE);
//		config.setMaxWaitMillis(MAX_WAIT);
//		config.setTestOnBorrow(TEST_ON_BORROW);
//		config.setTestOnReturn(TEST_ON_RETURN);
//
//		if(password == null || password.length() < 1){
//			pool = new JedisPool(config, ip, port);
//		}else{
//			pool = new JedisPool(config, ip, port, TIME_OUT, password);
//		}
		
		this.expireTime = expireTime;
	}

	/**
	 * 娴ｈ法鏁ら懛顏勭暰娑斿鍘ょ純顔煎灡瀵ょ療edis鏉╃偞甯�
	 *
	 * @param ip        redis-ip
	 * @param port      redis-port
	 * @param maxActive 鐎电钖勫Ч鐘垫畱婢堆冪毈
	 * @param maxIdle   閺堬拷婢堆傜箽閹镐胶鈹栭梻鑼Ц閹胶娈戠�电钖�
	 * @param maxWait   閼惧嘲褰噅edis鐎电钖勯張锟介梹璺ㄧ搼瀵板懏妞傞梻锟�(ms)
	 * @param expireTime	鏉╁洦婀￠弮鍫曟？
	 * @param password
	 */
	public RedisClient(String ip, int port, int maxActive, int maxIdle, int maxWait, int expireTime,String password) {
		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMaxTotal(maxActive);
//		config.setMaxIdle(maxIdle);
//		config.setMaxWaitMillis(maxWait);
//		config.setTestOnBorrow(TEST_ON_BORROW);
//		config.setTestOnReturn(TEST_ON_RETURN);
//
//		if(password == null || password.length() < 1){
//			pool = new JedisPool(config, ip, port);
//		}else{
//			pool = new JedisPool(config, ip, port, TIME_OUT, password);
//		}
//		
		this.expireTime = expireTime;
	}

	/**
	 * 娴犲骸顕挒鈩冪潨閼惧嘲绶辨稉锟芥稉鐚篹dis鏉╃偞甯�
	 *
	 * @return
	 */
	protected Jedis getConnect() {
		return pool.getResource();
	}

	/**
	 * 閸氭垵顕挒鈩冪潨鏉╂柨娲栨稉锟芥稉顏冨▏閻€劌鐣В鏇犳畱鏉╃偞甯�
	 *
	 * @param conn
	 */
	protected void returnConnect(Jedis conn) {
		if (conn != null) {
			pool.returnResource(conn);
		}
	}

	/**
	 * 閸氭垵顕挒鈩冪潨鏉╂柨娲栨稉锟芥稉顏勭磽鐢摜娈戞潻鐐村复
	 *
	 * @param conn
	 */
	protected void returnExceptionConnection(Jedis conn) {
		if (conn != null) {
			pool.returnBrokenResource(conn);
		}
	}

	/**
	 * 濞撳懐鎮妑edis
	 */
	@Deprecated
	public void flushAll(){

		Jedis jedis = null;

		try {
			jedis = getConnect();
			jedis.flushAll();
		} catch (Exception e) {
			returnExceptionConnection(jedis);
		} finally {
			returnConnect(jedis);
		}
	}

	/**
	 * 缁狅拷閸楁洖绨崚妤�瀵�
	 */
	protected String encode0(Object o) {
		return JSON.toJSONString(o, SerializerFeature.WriteClassName);
	}

	/**
	 * 缁狅拷閸楁洖寮芥惔蹇撳灙閸栵拷
	 */
	protected <T> T decode0(String s, Class<T> cls){
		return JSON.parseObject(s, cls);
	}

	/**
	 * 鎼村繐鍨崠锟�
	 */
	protected String encode(Object o){
		if(o instanceof String) {
			return (String) o;
		} else if (o instanceof Integer) {
			return o.toString();
		} else {
			return JSON.toJSONString(o, SerializerFeature.WriteClassName);
		}
	}

	/**
	 * 閸欏秴绨崚妤�瀵�
	 */
	protected <T> T decode(String s, Class<T> cls){
		if(s==null){
			return null;
		}

		if (cls == String.class) {
			return (T) s;
		} else if (cls == Integer.class || cls == int.class) {
			return (T) Integer.valueOf(s);
		} else {
			return JSON.parseObject(s, cls);
		}
	}

	/**
	 * 鎼村繐鍨崠鏍ㄦ殶缂侊拷
	 */
	protected String[] encodeList(List<Object> ls) {
		String[] res = new String[ls.size()];
		for (int i = 0; i < ls.size(); ++i) {
			res[i] = encode0(ls.get(i));
		}
		return res;
	}

	/**
	 * 閸欏秴绨崚妤�瀵查弫鎵矋
	 */
	protected <T> List<T>  decodeList(List<String> ls, Class<T> cls){
		List<T> res = new ArrayList<>();
		for (String s : ls) {
			res.add(decode0(s, cls));
		}
		return res;
	}

	/**
	 * 鎼村繐鍨崠鏉昦p
	 */
	protected <T> Map<String, String> encodeMap(Map<String, T> map) {
		if(map == null) {
			return null;
		}

		Map<String, String> res = new HashMap<>();
		for (Map.Entry<String, T> entry : map.entrySet()) {
			String k = entry.getKey();
			T v = entry.getValue();

			res.put(k, encode0(v));
		}

		return res;
	}

	/**
	 * 閸欏秴绨崚妤�瀵瞞ap
	 */
	protected <T> Map<String, T> decodeMap(Map<String, String> map, Class<T> cls) {
		if(map == null) {
			return null;
		}

		Map<String, T> res = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();

			res.put(k, decode0(v, cls));
		}

		return res;
	}
}
