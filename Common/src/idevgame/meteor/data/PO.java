package idevgame.meteor.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * po鐨勬敞瑙�
 * @author moon
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PO {
	/**琛ㄥ悕*/
	String value();
}
