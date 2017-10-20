package org.firstinspires.ftc.teamcode.input.gamepad.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LinearListeners.class)
public @interface LinearListener {
	public String name();
}
