package org.firstinspires.ftc.teamcode.input;

/**
 * A button tracks an input and performs actions when that input is pressed or released.
 * 
 * At least one of {@link #onPress()} or {@link #onRelease()} should be implemented.
 */
public abstract class Button {
	/** @return Is the button currently pressed? */
	public abstract boolean isInputPressed();
	/** Optional behavior when the button is pressed. */
	public void onPress() { }
	/** Optional behavior when the button is released. */
	public void onRelease() { }
	/** Makes a toggle button that behaves like this one. */
	public Toggle toToggle() {
		return new Toggle() {
			@Override
			public void onActivate() {
				Button.this.onPress();
            }

			@Override
			public void onDeactivate() {
				Button.this.onRelease();
			}

			@Override
			public boolean isInputPressed() {
				return Button.this.isInputPressed();
			}
		};
	}
}
