package org.firstinspires.ftc.teamcode.interfaces;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.RobotTeleOp;

/**
 * A way for motion implementations to access their motors.
 */
public interface IHardware {
    /** {@link RobotTeleOp#idle()} */
    public void idle();
    /** Gets the motor with the given name */
    public DcMotor getMotor(String name);
    /** Gets the touch sensor with the given name */
    public DigitalChannel getDigitalChannel(String name);
}
