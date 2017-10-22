package org.firstinspires.ftc.teamcode;

/** A kind of motion: generally positive/forward/up, nothing, or negative/retract/down. */
public enum MotionType {
	/** Forward motion, upward motion, or otherwise positive motion. */
    EXTEND,
    /** No motion. */
    ZERO,
    /** Backward motion, returning motion, downward motion, or otherwise negative motion. */
    RETURN;

	/** Gets +1, -1, or 0, depending upon whether a motion type is generally considered positive or negative. */
    public int toSign() {
        switch(this) {
            case EXTEND:
                return 1;
            case RETURN:
                return -1;
        }
        return 0;
    }

    /** Gets the generally positive or negative motion type associated with a value. */
    public static MotionType fromSign(float value) {
        if(value > 0)
            return EXTEND;
        if(value < 0)
            return RETURN;
        return ZERO;
    }

    /** Takes two buttons, extend and retract, and makes a resultant motion based on which ones are pressed. */
    public static MotionType fromButtons(boolean extend, boolean retract) {
        if(!(extend ^ retract))
            return MotionType.ZERO;
        if(extend)
            return MotionType.EXTEND;
        // This is not a default value: we just have eliminated all other possibilities
        return MotionType.RETURN;
    }
}
