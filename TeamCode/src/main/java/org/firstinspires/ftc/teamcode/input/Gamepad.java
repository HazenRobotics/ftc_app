package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.Field;

public class Gamepad {
    public final Button a;
    public final Button b;
    public final Button x;
    public final Button y;

    public final Button leftBumper;
    public final Button rightBumper;

    public final Trigger leftTrigger;
    public final Trigger rightTrigger;

    public final Joystick leftJoystick;
    public final Button leftJoystickButton;
    public final Joystick rightJoystick;
    public final Button rightJoystickButton;

    public Gamepad(com.qualcomm.robotcore.hardware.Gamepad gamepad) throws NoSuchFieldException, IllegalAccessException {
        this.a = Button.generate(gamepad, "a");
        this.b = Button.generate(gamepad, "b");
        this.x = Button.generate(gamepad, "x");
        this.y = Button.generate(gamepad, "y");

        this.leftBumper = Button.generate(gamepad, "left_bumper");
        this.rightBumper = Button.generate(gamepad, "right_bumper");

        this.leftTrigger = Trigger.generate(gamepad, "left_trigger");
        this.rightTrigger = Trigger.generate(gamepad, "right_trigger");

        this.leftJoystick = Joystick.generate(gamepad, "left_joystick_x", "left_joystick_y");
        this.leftJoystickButton = Button.generate(gamepad, "left_stick_button");
        this.rightJoystick = Joystick.generate(gamepad, "right_joystick_x", "right_joystick_y");
        this.rightJoystickButton = Button.generate(gamepad, "right_stick_button");
    }

    public void update() {
        // Rather than manually updating every field, just update all of the buttons declared in this class
        for(Field field : Gamepad.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(Button.class)) {
                try {
                    ((Button) field.get(this)).update();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
