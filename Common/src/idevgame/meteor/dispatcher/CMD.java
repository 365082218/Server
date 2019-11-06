package idevgame.meteor.dispatcher;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CMD
 *
 * @author moon
 * @version 2.0 - 2014-03-27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CMD {
	int id();
}
