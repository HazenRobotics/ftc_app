package org.firstinspires.ftc.teamcode.interfaces;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotTeleOp;

/**
 * A way for motion implementations to access their motors.
 */
public interface IHardware {
    /** {@link RobotTeleOp#idle()} */
    public void idle();

    public void idle(long milliseconds);
    /** Gets the motor with the given name */
    public DcMotor getMotor(String name);
    /** Gets the Servo with the given name*/
    public Servo getServo(String name);
    /** Gets the touch sensor with the given name */
    public DigitalChannel getDigitalChannel(String name);

    public HardwareDevice get(String name);
}
