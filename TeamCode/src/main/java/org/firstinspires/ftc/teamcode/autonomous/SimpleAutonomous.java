package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;

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
    protected static final float JEWEL_COLOR_READ_DISTANCE = 7.3f;
    protected static final float JEWEL_KNOCK_DISTANCE = 5.4f;
    protected static final float JEWEL_STRAFE_DISTANCE = 6.0f;
    protected static final float JEWEL_FORWARD_DISTANCE = 8.0f;
    protected static final float JEWEL_BACKUP_DISTANCE = 5.5f;
    protected static int color;

    //Objects and sensors
    protected IHardware hardware;
    protected Telemetry telemetry;
    protected SimpleStartingPosition simpleStartingPosition;
    protected MechanamMotors motion;
    protected Servo flicker;
    protected I2cColorSensor colorSensor;
    protected I2cRangeSensor rangeSensor;

    //Variables
    protected String currentStep;
    protected Message stepMessage;

    public SimpleAutonomous(SimpleStartingPosition simpleStartingPosition) {
        super();
        this.simpleStartingPosition = simpleStartingPosition;
    }
    public void initialize() {
        currentStep = "Initializing";
        this.telemetry = new Telemetry(super.telemetry);
        telemetry.add("Step>", new Message.IMessageData() {
            @Override
            public String getMessage() {
                return currentStep;
            }
        });
        telemetry.update();
        this.hardware = this;
        this.motion = new MechanamMotors(hardware);
    }

    @Override
    public void runOpMode() {
        initialize();

        currentStep = "Waiting for start";
        telemetry.update();
        waitForStart();

        currentStep = "Running Autonomous";
        telemetry.update();

        currentStep = "driveForward()";
        telemetry.update();
        driveForward();

        idle();
    }

    protected void driveForward(){
        motion.move(7);
    }

    private void knockOverJewel() {
        //Moves forward to the appropriate distance to read the color of the jewel
        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
                telemetry.update();
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_COLOR_READ_DISTANCE;
            }
        }, DRIVE_SPEED);

        sleep(1000);


        currentStep = "Reading color";
        telemetry.update();
        color = colorSensor.readColor();

        currentStep = "Jewel color: " + String.valueOf(color);
        telemetry.update();

        sleep(1000);

        motion.move(180, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
            }
        }, DRIVE_SPEED);

        flicker.setPosition(0.5);

        sleep(1000);
        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
            }
        }, DRIVE_SPEED);

        sleep(500);

        //Based on the color detected, knock the right or left jewel
        if ((color >= 1 && color <= 4 && simpleStartingPosition.getTeamColor() == Color.RED) ||(color >= 9 && color <= 11 && simpleStartingPosition.getTeamColor() == Color.BLUE)) {
            flicker.setPosition(1);
        } else {
            flicker.setPosition(0);
        }

        sleep(1000);

        motion.move(180, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
            }
        }, DRIVE_SPEED);
        flicker.setPosition(0);
    }

    protected void moveToCryptoBox(){
        float movementAngle = simpleStartingPosition.getMovementAngle();
        float baseDistance = simpleStartingPosition.getBaseDistance();

        motion.turn(movementAngle);
        motion.move(baseDistance);
    }

    public void idle(long milliseconds) {
        // This is probably the wrong way to handle this-- spin loop.
        // However, it's better than Thread.idleFor()-- probably.
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            telemetry.update();
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
