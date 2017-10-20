package org.firstinspires.ftc.teamcode.input.gamepad.values;

/** Tracks two float variables. */
public interface IGamepadJoystick {
	/** How far has the joystick been pushed to the side? */
	public float getPositionX();
	/** How far has the joystick been pushed up? */
	public float getPositionY();
}
