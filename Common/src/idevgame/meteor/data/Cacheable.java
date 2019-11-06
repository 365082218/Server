package idevgame.meteor.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鍙紦瀛樻帴鍙ｏ紝缁ф壙杩欎釜鎺ュ彛骞朵娇鐢–achedDao鐨勪細鑷姩杩涜缂撳瓨澶勭悊
 * 濡備娇鐢ㄦ鎺ュ彛锛岃鍦╥d() idValues()鍑芥暟涔熸寜鐓ey subkey椤哄簭濉啓
 *
 * @see CachedDao
 *
 * @author moon
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

	/**杩斿洖瀵硅薄鐨刱ey*/
	String key();

	/**杩斿洖瀵硅薄鐨剆ubkey*/
	String subkey() default "";

	/**涓�瀵瑰鍏崇郴涓垵濮嬪寲鐨剆ql*/
	String manyInitSql() default "";

}
