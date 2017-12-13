package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

public final class Dpad extends Button {
    public final ButtonPair x;
    public final ButtonPair y;

    public final Button up;
    public final Button right;
    public final Button down;
    public final Button left;

    public Dpad(ButtonPair x, ButtonPair y) {
        super(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return x.pressed() || y.pressed();
            }
        });
        this.x = x;
        this.left = x.neg;
        this.right = x.pos;
        this.y = y;
        this.up = y.pos;
        this.down = y.neg;
    }

    public Dpad(Button up, Button right, Button down, Button left) {
        this(new ButtonPair(right, left), new ButtonPair(up, down));
    }

    public Dpad(Supplier<Boolean> up, Supplier<Boolean> right, Supplier<Boolean> down, Supplier<Boolean> left) {
        this(new Button(up), new Button(right), new Button(down), new Button(left));
    }

    public boolean left() {
        return left.pressed();
    }

    public boolean right() {
        return right.pressed();
    }

    public boolean up() {
        return up.pressed();
    }

    public boolean down() {
        return down.pressed();
    }

    public Sign x() {
        return x.sign();
    }

    public Sign y() {
        return y.sign();
    }

    @Override
    public EventType update() {
        x.update();
        y.update();

        return super.update();
    }
}
