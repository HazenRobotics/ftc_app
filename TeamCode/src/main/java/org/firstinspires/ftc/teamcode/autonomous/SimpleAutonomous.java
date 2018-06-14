package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.controllers.EncoderMecanumWheels;
import org.firstinspires.ftc.teamcode.controllers.IHardware;
import org.firstinspires.ftc.teamcode.controllers.MecanumWheels;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.models.GyroTurn;
import org.firstinspires.ftc.teamcode.models.Range;
import org.firstinspires.ftc.teamcode.sensors.I2cColorSensor;
import org.firstinspires.ftc.teamcode.sensors.I2cGyroSensor;
import org.firstinspires.ftc.teamcode.sensors.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class SimpleAutonomous extends LinearOpMode implements IHardware {

    //Constants
    protected static final float JEWEL_COLOR_READ_DISTANCE = 7.0f;
    protected static final float JEWEL_FORWARD_DISTANCE = 11.0f;
    protected static final float JEWEL_BACKUP_DISTANCE = 12.5f;
    protected static final float JEWEL_END_DISTANCE = 15.0f;
    protected static int color;

    //Objects and sensors
    protected IHardware hardware;
    protected Telemetry telemetry;
    protected SimpleStartingPosition simpleStartingPosition;
    protected EncoderMecanumWheels motion;
    protected Servo flicker;
    protected I2cColorSensor colorSensor;
    protected I2cRangeSensor rangeSensor;
    protected I2cGyroSensor gyro;

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
        this.motion = new EncoderMecanumWheels(hardware);

        this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
        this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
        this.flicker = hardware.getServo("flicker");
        flicker.setDirection(Servo.Direction.REVERSE);
        flicker.setPosition(0);

        this.gyro = new I2cGyroSensor((I2cDevice) hardware.get("gyro"));
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            idle();
        }
    }

    @Override
    public void runOpMode() {
        initialize();

        currentStep = "Waiting for start";
        telemetry.update();
        waitForStart();

        currentStep = "Running Autonomous";
        telemetry.update();
        knockOverJewel();

        currentStep = "Moving to cryptobox";
        telemetry.update();
        moveToCryptoBox();

        idle();
    }


    private void knockOverJewel() {
        //(Step #1)
        currentStep = "Moving Closer";
        telemetry.update();
        motion.move(new Range(JEWEL_COLOR_READ_DISTANCE, rangeSensor, false), true);
        sleep(1000);

        //(Step #2)
        currentStep = "Reading Color";
        telemetry.update();

        color = colorSensor.readColor();
        currentStep = "Jewel color: " + String.valueOf(color);
        telemetry.update();

        sleep(1000);

        //(Step #3)
        currentStep = "Jewel Color: " + String.valueOf(color);
        motion.move(new Range(JEWEL_BACKUP_DISTANCE, rangeSensor, true), false);

        //(Step #4)
        currentStep = "Opening flicker";
        telemetry.update();
        flicker.setPosition(0.6);
        sleep(1000);

        //(Step #5)
        currentStep = "Moving towards jewel";
        telemetry.update();
        motion.move(new Range(JEWEL_FORWARD_DISTANCE, rangeSensor, false), true);

        sleep(500);

        //Based on the color detected, knock the right or left jewel
        currentStep = "Flicking Jewel";
        telemetry.update();
        if ((color >= 1 && color <= 4 && simpleStartingPosition.getTeamColor() == Color.RED) ||(color >= 9 && color <= 11 && simpleStartingPosition.getTeamColor() == Color.BLUE)) {
            flicker.setPosition(1);
        } else {
            flicker.setPosition(0);
        }

        sleep(1000);

        currentStep = "Moving Back";
        telemetry.update();
        motion.move(new Range(JEWEL_END_DISTANCE, rangeSensor, true), false);

        currentStep = "Closing Flicker";
        telemetry.update();

        flicker.setPosition(0);
    }

    protected void moveToCryptoBox(){
        float movementAngle = simpleStartingPosition.getMovementAngle();
        float baseDistance = simpleStartingPosition.getBaseDistance();

        motion.turn(new GyroTurn(movementAngle, gyro, movementAngle > 0), movementAngle > 0);

        motion.move(baseDistance, DistanceUnit.INCH);
    }


    public void idle(long milliseconds) {
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
