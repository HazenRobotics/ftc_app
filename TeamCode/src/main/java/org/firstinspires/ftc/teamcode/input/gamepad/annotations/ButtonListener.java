package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.firstinspires.ftc.teamcode.input.gamepad.ButtonEvent;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ButtonListeners.class)
public @interface ButtonListener {
	public String button();
	public ButtonEvent event() default ButtonEvent.PRESS;
}
