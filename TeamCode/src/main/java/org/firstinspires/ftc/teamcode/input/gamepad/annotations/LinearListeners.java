package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A {@link LinearListener} on multiple variables. */
@Retention(RetentionPolicy.RUNTIME)
public @interface LinearListeners {
	LinearListener[] value();
}
