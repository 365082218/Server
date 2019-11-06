package idevgame.meteor.data;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * 鏁版嵁鎺ュ彛
 * @author moon
 */
public interface IData {

	/**
	 * 闈炵紦瀛樼被璁块棶:閫氳繃id鑾峰彇
	 * @param cls
	 * @param ids
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> T findById(Class<T> cls, Object... ids);

	/**
	 * 闈炵紦瀛樼被璁块棶:鑾峰彇鍏ㄨ〃
	 * @param cls
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> List<T> findAll(Class<T> cls);

	/**
	 * 闈炵紦瀛樼被璁块棶:閫氳繃灞炴�ф煡鎵�
	 * @param cls
	 * @param props
	 * @param values
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> List<T> findByProp(Class<T> cls, String[] props, Object[] values);

	/**
	 * 闈炵紦瀛樼被璁块棶:閫氳繃sql鏌ユ壘
	 * @param cls
	 * @param sql
	 * @param values
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> List<T> findBySQL(Class<T> cls, String sql, Object[] values);

	/**
	 * 闈炵紦瀛樼被璁块棶:鎵цsql
	 * @param cls
	 * @param sql
	 * @param values
	 * @param <T>
	 */
	<T extends BasePo> void execute(Class<T> cls, String sql, Object[] values) throws SQLException;

	/**
	 * 闈炵紦瀛樼被璁块棶:鏌ヨsql
	 * @param sql 闇�瑕佹暣鏉ql,绀轰緥 "select max(id) from xxx where aaa=? and bbb=?"
	 * @param handler <a href="http://commons.apache.org/proper/commons-dbutils/examples.html">鐢ㄦ硶鐪嬭繖閲�</a>
	 */
	<T extends BasePo, V> V query(Class<T> cls, String sql, ResultSetHandler<V> handler, Object... params) throws SQLException;

	/**
	 * 闈炵紦瀛樼被璁块棶:鍒犻櫎
	 * @param cls
	 * @param ids
	 * @return 鍙楀奖鍝嶇殑琛屾暟
	 */
	<T extends BasePo> int deleteById(Class<T> cls, Object... ids);

	/**
	 * 闈炵紦瀛樼被璁块棶:閫氳繃灞炴�у垹闄�
	 * @param cls
	 * @param propName
	 * @param values
	 * @return 鍙楀奖鍝嶇殑琛屾暟
	 */
	<T extends BasePo> int deleteByProp(Class<T> cls, String[] propName, Object[] values);

	/**
	 * 闈炵紦瀛樼被璁块棶:閫氳繃sql鍒犻櫎
	 * @param cls
	 * @param sql
	 * @param values
	 * @return 鍙楀奖鍝嶇殑琛屾暟
	 */
	<T extends BasePo> int deleteBySQL(Class<T> cls, String sql, Object[] values);

	/**
	 * 閫氱敤璁块棶:鍒犻櫎
	 * @param cls
	 * @param t
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> int delete(Class<T> cls, T t);

	/**
	 * 閫氱敤璁块棶:鏇存柊
	 * @param cls
	 * @param t
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> boolean update(Class<T> cls, T t);
	
	
	/**
	 * 閫氱敤璁块棶:鏇挎崲
	 * @param cls
	 * @param t
	 * @return
	 * @throws SQLException 
	 */
	<T extends BasePo> int replace(Class<T> cls, T t) throws SQLException;

	/**
	 * 閫氱敤璁块棶:鎻掑叆锛屼笉鏀寔鑷姩涓婚敭
	 *
	 * @param cls
	 * @param t
	 * @return
	 */
	<T extends BasePo> void insert(Class<T> cls, T t) throws SQLException;

	/**
	 * 缂撳瓨绫昏闂�:涓�瀵瑰鍏崇郴涓秹鍙婁富key鏇存柊鐨剈pdate
	 */
	<T extends BasePo> void updateWithKey(Class<T> cls, T t, Object oldId);

	/**
	 * 缂撳瓨绫昏闂�:鑾峰彇涓�瀵逛竴鍏崇郴涓殑涓�鏉℃暟鎹�
	 * @param cls
	 * @param id key
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> T get(Class<T> cls, Object id);

	/**
	 * 缂撳瓨绫昏闂�:鑾峰彇涓�瀵瑰鍏崇郴涓殑涓�缁勬暟鎹�<br>
	 * 鍙互鐞嗚В涓� Map[String:Map] 涓殑map.get()
	 *
	 *
	 * @param cls
	 * @param id 涓�绾ey
	 * @return
	 */
	<T extends BasePo> List<T> getList(Class<T> cls, Object id);

	/**
	 * 缂撳瓨绫昏闂�:鑾峰彇涓�瀵瑰鍏崇郴涓殑涓�涓暟鎹�<br>
	 * 涓嶅湪缂撳瓨涓椂浼氬皾璇曟煡璇b
	 * 鍙互鐞嗚В涓� Map[String:Map] 涓殑map.get().get()
	 *
	 *
	 * @param cls
	 * @param id 涓�绾ey
	 * @param subId 浜岀骇key
	 * @param isFlushCache 灏濊瘯鏌ヨdb鍚庢槸鍚﹀悓鏃跺埛鏂扮紦瀛�
	 * @return
	 */
	<T extends BasePo> T getOne(Class<T> cls, Object id, Object subId,boolean isFlushCache);

	/**
	 * 缂撳瓨绫昏闂�:鑾峰彇涓�瀵逛竴鍏崇郴涓殑涓�鏉℃暟鎹�
	 * 寮哄埗鍒锋柊缂撳瓨
	 *
	 * @param cls
	 * @param id key
	 * @param <T>
	 * @return
	 */
	<T extends BasePo> T getFromDb(Class<T> cls, Object id);

	/**
	 * 缂撳瓨绫昏闂�:鑾峰彇涓�瀵瑰鍏崇郴涓殑涓�缁勬暟鎹�<br>
	 * 鍙互鐞嗚В涓� Map[String:Map] 涓殑map.get()
	 * 寮哄埗鍒锋柊缂撳瓨
	 *
	 * @param cls
	 * @param id 涓�绾ey
	 * @return
	 */
	<T extends BasePo> List<T> getListFromDb(Class<T> cls, Object id);
	
	/**
	 * 宸℃煡redis
	 * @return
	 */
	public boolean patrol();
	
	/**
	 * 瀵瑰垪琛ㄧ紦瀛樺仛寤舵湡
	 * @param cls
	 * @param id
	 */
	<T extends BasePo> void listDelay(Class<T> cls, Object id);
	
	/**
	 * 闈炵紦瀛樼骇鍒闂�:鎻掑叆锛屽苟杩斿洖鎻掑叆鐨勪富閿�
	 *
	 * @param cls
	 * @param t
	 * @return id 
	 */
	<T extends BasePo> Long insertReturnId(Class<T> cls, T t) throws SQLException;
}
