package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A {@link ButtonListener} on multiple buttons. */
@Retention(RetentionPolicy.RUNTIME)
public @interface ButtonListeners {
	ButtonListener[] value();
}
