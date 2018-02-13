package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.objects.I2cGyroSensor;


@Disabled
@Autonomous(name = "Gyro Turn Test", group = "Test Classes")
/**
 * Test Class to turn the robot to a certain number of degrees by using the gyro
 */
public class GyroTurnTest extends LinearOpMode implements IHardware{
    protected MechanamMotors motion;
    protected I2cDevice gyro;
    protected I2cGyroSensor gyroSensor;
    protected final int TURN = 90;

    @Override
    public void runOpMode() throws InterruptedException {
         this.motion = new MechanamMotors(this);
         gyro = hardwareMap.i2cDevice.get("gyroSensor");
         gyroSensor = new I2cGyroSensor(gyro);
         sleep(500);
         gyroSensor.calibrate();
         while(gyroSensor.isCalibrating()) {
             telemetry.addData(">", "Calibrating");
             telemetry.update();
             idle();
         }
         waitForStart();
         final int heading = gyroSensor.getIntegratedZ();
         motion.turn(true, new Condition() {
             @Override
             public boolean isTrue() {
                 //Multiplies it by negitive one if doing negative turn
                 return (TURN < 0 ? -1 : 1) * (gyroSensor.getIntegratedZ() - (heading + TURN)) > 0 ;
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
