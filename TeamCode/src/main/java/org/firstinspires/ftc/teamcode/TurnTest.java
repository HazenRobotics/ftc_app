package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Condition;

/**
 * Created by Robotics on 12/10/2017.
 */

@Autonomous(name = "Turn Test", group = "Test")
public class TurnTest extends LinearOpMode implements IHardware{
    protected MechanamMotors motion;
    protected ModernRoboticsI2cGyro gyro;
    @Override
    public void runOpMode() throws InterruptedException {
        this.motion = new MechanamMotors(this);
        gyro = (ModernRoboticsI2cGyro) get("gyro");

        sleep(500);
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            telemetry.addData(">", "Calibrating");
            telemetry.update();
            idle();
        }
        waitForStart();

        final int heading = gyro.getIntegratedZValue();
        final int turn = 90;
        motion.turn(true, new Condition() {
            @Override
            public boolean isTrue() {
                //Multiplies it by negitive one if doing negative turn
                return (turn < 0 ? -1 : 1) * (gyro.getIntegratedZValue() - (heading + turn)) > 0 ;
            }
        });
    }

    //Hardware
    public void idle(long milliseconds) {
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            super.idle();
        }
    }

    @Override
    public DcMotor getMotor(String name) {
        return hardwareMap.dcMotor.get(name);
    }

    @Override
    public Servo getServo(String name) {
        return hardwareMap.servo.get(name);
    }

    @Override
    public DigitalChannel getDigitalChannel(String name) {
        return hardwareMap.digitalChannel.get(name);
    }

    @Override
    public HardwareDevice get(String name) {
        return hardwareMap.get(name);
    }
}
