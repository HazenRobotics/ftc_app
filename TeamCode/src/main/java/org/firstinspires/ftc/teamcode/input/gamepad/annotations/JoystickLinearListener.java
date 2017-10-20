package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.firstinspires.ftc.teamcode.input.gamepad.JoystickDimension;

/** Listens for events relating to a registered joystick's specific dimension. */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JoystickLinearListeners.class)
public @interface JoystickLinearListener {
	/** The joystick to listen for. */
	public String joystick();
	/** The dimension of the joystick we're listening to. */
	public JoystickDimension variable();
}
