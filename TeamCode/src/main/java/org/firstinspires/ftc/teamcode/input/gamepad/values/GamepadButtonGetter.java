package org.firstinspires.ftc.teamcode.input.gamepad.values;

/** Crappy hack. Forgive me. */
public class GamepadButtonGetter implements IGamepadButton {
	private final IValueGetter<Boolean> internal;
	
	public GamepadButtonGetter(IValueGetter<Boolean> internal) {
		this.internal = internal;
	}

	@Override
	public boolean isPressed() {
		return internal.get();
	}
}
