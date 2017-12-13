package org.firstinspires.ftc.teamcode.input;

/** The type of a button-related event we care about, e.g. a button press or release. */
public enum EventType {
    /** Call this with the button state regardless of anything. */
    ANY,
    /** Call this if the button is pressed or released, but I don't care which. */
    CHANGED,
    /** Call this if the button is pressed, but it wasn't before. */
    PRESS,
    /** Call this while the button is pressed, regardless if this is new. */
    ACTIVE,
    /** Call this if the button is released, and it was pressed before. */
    RELEASE,
    /** Call this while the button is not pressed. */
    INACTIVE;

    /** Is the button pressed, whether this just happened or not. */
    public boolean isActive() {
        return this == ACTIVE || this == PRESS;
    }

    /** Is the button released, whether this just happened or not. */
    public boolean isInactive() {
        return this == INACTIVE || this == RELEASE;
    }

    /** Did the button state change, regardless of whether it was pressed or released. */
    public boolean isChanged() {
        return this == CHANGED || this == PRESS || this == RELEASE;
    }

    /** If I have a listener with the event type "other", does it care about this event? */
    public boolean compatibleWith(EventType other) {
        return other == ANY
                || other == this
                || (other == ACTIVE && isActive())
                || (other == INACTIVE && isInactive())
                || (other == CHANGED && isChanged());
    }
}
