package co.lijero.react;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A method or field marked with this becomes a reactive variable in a reactive system when registered. */
@Retention(RetentionPolicy.RUNTIME)
public @interface Reactive {
	/** A name for the variable instead of the method or field name. */
    public String name() default "";
    /** The variables that a method depends on. Must be left blank if a field. */
    public String[] depends() default {};
}
