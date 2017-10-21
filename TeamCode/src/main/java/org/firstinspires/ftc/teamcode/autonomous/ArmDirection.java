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
}
