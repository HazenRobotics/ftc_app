package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.firstinspires.ftc.teamcode.input.gamepad.JoystickDimension;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JoystickLinearListeners.class)
public @interface JoystickLinearListener {
	public String joystick();
	public JoystickDimension variable();
}
