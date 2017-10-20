package org.firstinspires.ftc.teamcode.input.gamepad.values;

/** Crappy hack so I can write the function {@link org.firstinspires.ftc.teamcode.input.ButtonManager#makeFieldGetter}. Forgive me. */
public interface IValueGetter<T> {
	public T get();
}
