package co.lijero.react;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Reactive {
    public String name() default "";
    public String[] depends() default {};
}
