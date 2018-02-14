package org.firstinspires.ftc.teamcode.models;

/**
 * A Condition an abstract class that is used to tell a function when some parameter has been met.
 * The user can either define their own condition as an anonymous class, or use one of the many subclasses that extend it
 */
public abstract class Condition {
    /**
     * This abstract function can be overridden to allow a custom condition to be made
     * @return If the condition is true
     */
    public abstract boolean isTrue();
}
