package org.firstinspires.ftc.teamcode.autonomous;

public enum ArmDirection {
    EXTEND, ZERO, RETRACT;

    public int toSign() {
        switch(this) {
            case EXTEND:
                return 1;
            case RETRACT:
                return -1;
        }
        return 0;
    }

    public static ArmDirection fromSign(float value) {
        if(value > 0)
            return EXTEND;
        if(value < 0)
            return RETRACT;
        return ZERO;
    }

    public static ArmDirection fromButtons(boolean extend, boolean retract) {
        if(!(extend ^ retract))
            return ArmDirection.ZERO;
        if(extend)
            return ArmDirection.EXTEND;
        // This is not a default value: we just have eliminated all other possibilities
        return ArmDirection.RETRACT;
    }
}
