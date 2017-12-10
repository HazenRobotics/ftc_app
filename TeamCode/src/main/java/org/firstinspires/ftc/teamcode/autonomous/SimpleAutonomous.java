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
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

import java.io.PrintWriter;
import java.io.StringWriter;

@Autonomous(name="SimpleAutonomous", group="Autonomous")
public class 	SimpleAutonomous extends LinearOpMode implements IHardware {

    //Constants
    protected static final float DRIVE_SPEED = 0.25f;
    protected static final float JEWEL_READ_DISTANCE = 7.3f;
    protected static final float JEWEL_KNOCK_DISTANCE = 5.4f;
    protected static final float JEWEL_STRAFE_DISTANCE = 6.0f;
    protected static final float JEWEL_FORWARD_DISTANCE = 8.0f;
    protected static final float JEWEL_BACKUP_DISTANCE = 5.5f;
    protected static int COLOR;
    protected static int MAXINUMDIFFERENCE = 50;

    //Objects and sensors
    protected IHardware hardware;
    protected Telemetry telemetry;
    protected StartingPosition startingPosition;
    protected MechanamMotors motion;
    protected Servo flicker;
    protected I2cColorSensor colorSensor;
    protected I2cRangeSensor rangeSensor;

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
        driveForward();



        idle();
    }

    protected void driveForward(){
        motion.move(7);
    }

    private void knockOverJewel() {
        //Moves forward to the appropriate distance to read the color of the jewel
        currentStep = "Reading color";

        final double startingUltrasonicReading = rangeSensor.readUltrasonic(DistanceUnit.INCH);

        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
                t.update();
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_READ_DISTANCE;
            }
        }, DRIVE_SPEED);

        sleep(1000);

        COLOR = colorSensor.readColor();

        currentStep = "Jewel color: " + String.valueOf(COLOR);
        t.update();

        sleep(1000);

        currentStep = "Knocking Jewel";
        t.update();


        motion.move(180, 3);

        currentStep = "Opening flicker";
        t.update();

        flicker.setPosition(0.5);

        sleep(1000);

        currentStep = "Moving towards jewel";
        t.update();

        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
            }
        }, DRIVE_SPEED);

        sleep(500);

        //Based on the color detected, knock the right or left jewel
		if ((COLOR >= 1 && COLOR <= 4 && startingPosition.getTeamColor() == Color.RED) ||(COLOR  >= 9 && COLOR <= 11 && startingPosition.getTeamColor() == Color.BLUE)) {
			flicker.setPosition(0);
		} else {
            flicker.setPosition(1);
        }

        sleep(1000);

        currentStep = "Moving back";
        t.update();
        motion.move(180, new Condition() {
            @Override
            public boolean isTrue() {
                t.addData(">", "Moving Back");
                t.update();
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
            }
        }, DRIVE_SPEED);

        flicker.setPosition(0);



        //	OLD ROBOTIC CODE, I THINK
		/*
        //Drop Small Lift
		currentStep = "Drop Small Lift";
		t.update();
		lift.setScoopBottomHeight(SCOOP_BALL_HEIGHT);

		sleep(5000);

		//Forward under other colored ball
		currentStep = "Move under ball";
		t.update();
		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(5000);

		//motion.move(0, JEWEL_FORWARD_DISTANCE);

		//Flip the ball
		currentStep = "Flip the ball";
		t.update();
		lift.raiseScoop();

		t.update();
		sleep(5000);

		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Back");
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(5000);*/
    }



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
