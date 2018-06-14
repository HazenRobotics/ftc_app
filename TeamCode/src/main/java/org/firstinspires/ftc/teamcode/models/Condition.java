package org.firstinspires.ftc.teamcode.models;

/**
 * A Condition an abstract class that is used to tell a function when some parameter has been met.
 * The user can either define their own condition as an anonymous class, or use one of the many subclasses that extend it
 * @see Range
 * @see Timer
 * @see GyroTurn
 */
public abstract class Condition {
    private boolean wasTrue = false;
    protected final boolean rememberTrue = true;
    /**
     * This abstract function can be overridden to allow a custom condition to be made
     * @return The result of the condition operation
     */
    protected abstract boolean condition();

    /**
     * This is the control function for a condition
     * @return If the condition is true/the object it in a true state
     */
    public boolean isTrue() {
        if (wasTrue && rememberTrue) {
            return true;
        } else {
            wasTrue = condition();
            return wasTrue;
        }
    }
}
