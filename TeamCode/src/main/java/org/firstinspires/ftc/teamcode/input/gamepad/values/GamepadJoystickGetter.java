package org.firstinspires.ftc.teamcode.input.gamepad.values;

/** Crappy hack. Forgive me. */
public class GamepadJoystickGetter implements IGamepadJoystick {
	private final IValueGetter<Float> internalX;
	private final IValueGetter<Float> internalY;
	
	public GamepadJoystickGetter(IValueGetter<Float> internalX, IValueGetter<Float> internalY) {
		this.internalX = internalX;
		this.internalY = internalY;
	}

	@Override
	public float getPositionX() {
		return internalX.get();
	}
	
	@Override
	public float getPositionY() {
		return internalY.get();
	}
}
