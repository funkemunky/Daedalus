package anticheat.detections;

/**
 * Created by XtasyCode on 11/08/2017.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChecksListener {

	@SuppressWarnings("rawtypes")
	Class[] events() default {};

}