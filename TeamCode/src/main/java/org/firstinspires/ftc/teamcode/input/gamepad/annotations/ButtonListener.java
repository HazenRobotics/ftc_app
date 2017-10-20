package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.firstinspires.ftc.teamcode.input.gamepad.ButtonEvent;

/**
 * Listens for events relating to a registered button.
 * 
 * Used to annotate a void() function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ButtonListeners.class)
public @interface ButtonListener {
	/** The button to listen for */
	public String button();
	/** The event, pressing or releasing, that this responds to */
	public ButtonEvent event() default ButtonEvent.PRESS;
}
