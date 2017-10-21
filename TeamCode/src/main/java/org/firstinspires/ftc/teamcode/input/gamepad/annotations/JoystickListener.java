package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Listens for events relating to a registered joystick.
 * 
 * Used to annotate a void(float x, float y) function to track an active joystick, or a void() to track the joystick's release.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JoystickListener {
	/** The joystick to listen to */
	public String joystick();
}
