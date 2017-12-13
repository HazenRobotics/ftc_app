package org.firstinspires.ftc.teamcode.input;

public class SignEvent extends ButtonEvent {
    public final ButtonPair pair;
    public final Sign sign;
    public final boolean pos_pressed;
    public final boolean neg_pressed;
    public final Sign previous;

    public SignEvent(EventType type, ButtonPair pair, Sign previous) {
        super(type, pair);
        this.pair = pair;
        this.sign = pair.sign();
        this.pos_pressed = pair.pos.pressed();
        this.neg_pressed = pair.neg.pressed();
        this.previous = previous;
    }
}
