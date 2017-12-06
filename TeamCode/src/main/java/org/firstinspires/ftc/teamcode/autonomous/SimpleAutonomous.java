package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

import java.io.PrintWriter;
import java.io.StringWriter;

@Autonomous(name="SimpleAutonomous", group="Autonomous")
public class 	SimpleAutonomous extends LinearOpMode implements IHardware {

    //Constants
    protected static final float DRIVE_SPEED = 0.25f;

    //Objects and sensors
    protected IHardware hardware;
    protected Telemetry telemetry;
    protected StartingPosition startingPosition;
    protected MechanamMotors motion;

    //Variables
    protected String currentStep;
    protected Message stepMessage;
    org.firstinspires.ftc.robotcore.external.Telemetry t;

    public void initialize() {
        currentStep = "Initializing";
        t = super.telemetry;
        this.hardware = this;
        this.telemetry = new Telemetry(super.telemetry);
        this.motion = new MechanamMotors(hardware);
    }

    @Override
    public void runOpMode() {
        initialize();

        currentStep = "Waiting for start";
        t.update();

        waitForStart();

        currentStep = "Running Autonomous";
        t.update();

        currentStep = "driveForward()";
        t.update();
        motion.move(7);

        currentStep = "finished driveForward()";
        t.update();

        idle();
    }

    /*protected void driveForward(){
        motion.move(7);
    }*/

    public void idle(long milliseconds) {
        // This is probably the wrong way to handle this-- spin loop.
        // However, it's better than Thread.idleFor()-- probably.
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            t.update();
            hardware.idle();
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
