package org.firstinspires.ftc.teamcode.input;

public enum EventType {
    ANY,
    CHANGED,
    PRESS,
    ACTIVE,
    RELEASE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE || this == PRESS;
    }

    public boolean isInactive() {
        return this == INACTIVE || this == RELEASE;
    }

    public boolean isChanged() {
        return this == CHANGED || this == PRESS || this == RELEASE;
    }

    public boolean compatibleWith(EventType other) {
        return other == ANY
                || other == this
                || (other == ACTIVE && isActive())
                || (other == INACTIVE && isInactive())
                || (other == CHANGED && isChanged());
    }
}
