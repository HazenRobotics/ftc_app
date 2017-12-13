package org.firstinspires.ftc.teamcode.input;

public enum Sign {
    POSITIVE,
    ZERO,
    NEGATIVE;

    public int asInt() {
        if(this == POSITIVE)
            return 1;
        if(this == NEGATIVE)
            return -1;
        return 0;
    }

    public Sign opposite() {
        if(this == POSITIVE) return NEGATIVE;
        if(this == NEGATIVE) return POSITIVE;
        return ZERO;
    }
}
