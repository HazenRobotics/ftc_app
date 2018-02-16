package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

public final class Dpad {
    public final Button sum;

    public final Button x;
    public final Button y;

    public final Button up;
    public final Button right;
    public final Button down;
    public final Button left;

    public Dpad(final SignInput x, final SignInput y) {
        this.x = x;
        this.left = x.neg;
        this.right = x.pos;
        this.y = y;
        this.up = y.pos;
        this.down = y.neg;
        this.sum = new VectorInput(x.asScalar(), y.asScalar());
    }

    public Dpad(BinaryInput up, BinaryInput right, BinaryInput down, BinaryInput left) {
        this(new SignInput(right, left), new SignInput(up, down));
    }

    public Dpad(Supplier<Boolean> up, Supplier<Boolean> right, Supplier<Boolean> down, Supplier<Boolean> left) {
        this(new BinaryInput(up), new BinaryInput(right), new BinaryInput(down), new BinaryInput(left));
    }
}
