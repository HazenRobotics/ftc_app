package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.FieldAccessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** A mapping (wrapper) of the FTC gamepad buttons into our button system. */
public final class Gamepad {
    public final Button a;
    public final Button b;
    public final Button x;
    public final Button y;

    public final ButtonPair bumpers;
    public final Button left_bumper;
    public final Button right_bumper;

    public final Trigger left_trigger;
    public final Trigger right_trigger;

    public final Button left_stick_button;
    public final Button right_stick_button;
    public final Joystick left_stick;
    public final Joystick right_stick;

    public final Dpad dpad;

    private static final List<IButton> buttons = new ArrayList<>();

    public Gamepad(com.qualcomm.robotcore.hardware.Gamepad gamepad) {
        try {
            this.a = new Button(FieldAccessor.boola("a", gamepad));
            this.b = new Button(FieldAccessor.boola("b", gamepad));
            this.x = new Button(FieldAccessor.boola("x", gamepad));
            this.y = new Button(FieldAccessor.boola("y", gamepad));

            this.left_bumper = new Button(FieldAccessor.boola("left_bumper", gamepad));
            this.right_bumper = new Button(FieldAccessor.boola("right_bumper", gamepad));
            this.bumpers = new ButtonPair(left_bumper, right_bumper);

            this.left_trigger = new Trigger(FieldAccessor.floata("left_bumper", gamepad));
            this.right_trigger = new Trigger(FieldAccessor.floata("right_bumper", gamepad));

            this.left_stick_button = new Button(FieldAccessor.boola("left_joystick_button", gamepad));
            this.right_stick_button = new Button(FieldAccessor.boola("right_joystick_button", gamepad));
            this.left_stick = new Joystick(FieldAccessor.floata("left_joystick_x", gamepad), FieldAccessor.floata("left_joystick_y", gamepad));
            this.right_stick = new Joystick(FieldAccessor.floata("right_joystick_x", gamepad), FieldAccessor.floata("right_joystick_y", gamepad));

            this.dpad = new Dpad(
                    FieldAccessor.boola("dpad_up", gamepad),
                    FieldAccessor.boola("dpad_right", gamepad),
                    FieldAccessor.boola("dpad_down", gamepad),
                    FieldAccessor.boola("dpad_left", gamepad)
            );
        } catch(NoSuchFieldException e) {
            // If the official Gamepad has changed, there's nothing we can do.
            throw new RuntimeException(e);
        }

        for(Field field : Gamepad.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(Button.class)) {
                try {
                    buttons.add((Button) field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ButtonPair makePair(IButton pos, IButton neg) {
        ButtonPair pair = new ButtonPair(pos, neg);
        buttons.add(pair);
        return pair;
    }

    /** Run all of the button event listeners. */
    public void update() {
        for(IButton button : buttons)
            button.step();
        for(IButton button : buttons)
            button.update();
    }
}
