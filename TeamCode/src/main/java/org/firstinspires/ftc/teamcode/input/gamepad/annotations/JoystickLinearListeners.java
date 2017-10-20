package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A {@link JoystickLinearListener} on multiple joysticks. */
@Retention(RetentionPolicy.RUNTIME)
public @interface JoystickLinearListeners {
	JoystickLinearListener[] value();
}
