package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

public final class Dpad extends IButton {
    public final ButtonPair x;
    public final ButtonPair y;

    public final IButton up;
    public final IButton right;
    public final IButton down;
    public final IButton left;

    public Dpad(final ButtonPair x, final ButtonPair y) {
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

    public int x() {
        return x.sign();
    }

    public int y() {
        return y.sign();
    }

    @Override
    public float value() {
        return (float) Math.hypot(x.value(), y.value());
    }

    @Override
    public void step() {
        x.step();
        y.step();
        super.step();
    }

    @Override
    public void update() {
        x.update();
        y.update();
        super.update();
    }
}
