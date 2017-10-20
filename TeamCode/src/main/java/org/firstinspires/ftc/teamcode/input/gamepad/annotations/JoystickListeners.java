package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A {@link JoystickListener} on multiple joysticks. */
@Retention(RetentionPolicy.RUNTIME)
public @interface JoystickListeners {
	JoystickListener[] value();
}
