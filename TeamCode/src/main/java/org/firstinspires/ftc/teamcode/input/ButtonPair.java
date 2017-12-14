package org.firstinspires.ftc.teamcode.input;

public final class ButtonPair extends IButton {
    public final IButton pos;
    public final IButton right;
    public final IButton up;

    public final IButton neg;
    public final IButton left;
    public final IButton down;

    public ButtonPair(IButton pos, IButton neg) {
        this.pos = pos;
        this.right = pos;
        this.up = pos;

        this.neg = neg;
        this.left = neg;
        this.down = neg;
    }

    @Override
    public float value() {
        return pos.value() - neg.value();
    }
}
