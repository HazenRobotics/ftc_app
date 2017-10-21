package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Listens for events relating to a single registered float variable. */
@Retention(RetentionPolicy.RUNTIME)
public @interface LinearListener {
	/** The variable to listen to. */
	public String name();
}
