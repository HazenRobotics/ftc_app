package org.firstinspires.ftc.teamcode.input.gamepad.values;

/** Crappy hack so I can write the function {@link org.firstinspires.ftc.teamcode.input.ButtonManager#makeFieldGetter}. Forgive me. */
public class LinearValueGetter implements ILinearValue {
	private final IValueGetter<Float> internal;
	
	public LinearValueGetter(IValueGetter<Float> internal) {
		this.internal = internal;
	}

	@Override
	public float getValue() {
		return internal.get();
	}
}
