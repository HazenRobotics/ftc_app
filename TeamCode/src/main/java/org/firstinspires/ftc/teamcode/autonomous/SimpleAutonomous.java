package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MecanumWheels;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class SimpleAutonomous extends LinearOpMode implements IHardware {

    //Constants
    protected static final float JEWEL_COLOR_READ_DISTANCE = 7.0f;
    protected static final float DRIVE_SPEED = 0.2f;
    protected static final float JEWEL_FORWARD_DISTANCE = 11.0f;
    protected static final float JEWEL_BACKUP_DISTANCE = 12.5f;
    protected static final float JEWEL_END_DISTANCE = 15.0f;
    protected static int color;

    //Objects and sensors
    protected IHardware hardware;
    protected Telemetry telemetry;
    protected SimpleStartingPosition simpleStartingPosition;
    protected MecanumWheels motion;
    protected Servo flicker;
    protected I2cColorSensor colorSensor;
    protected I2cRangeSensor rangeSensor;
    protected ModernRoboticsI2cGyro gyro;

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
        this.motion = new MecanumWheels(hardware);

        this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
        this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
        this.flicker = hardware.getServo("flicker");
        flicker.setDirection(Servo.Direction.REVERSE);
        flicker.setPosition(0);

        gyro = (ModernRoboticsI2cGyro) get("gyro");
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
        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
                telemetry.update();
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_COLOR_READ_DISTANCE;
            }
        }, DRIVE_SPEED);
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
        motion.move(180, new Condition() {
            @Override
            public boolean isTrue() {
                currentStep = "Moving Back";
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
            }
        }, DRIVE_SPEED);

        //(Step #4)
        currentStep = "Opening flicker";
        telemetry.update();
        flicker.setPosition(0.6);
        sleep(1000);

        //(Step #5)
        currentStep = "Moving towards jewel";
        telemetry.update();
        motion.move(0, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
            }
        }, DRIVE_SPEED);

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
        motion.move(180, new Condition() {
            @Override
            public boolean isTrue() {
                return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_END_DISTANCE;
            }
        }, DRIVE_SPEED);


        currentStep = "Closing Flicker";
        telemetry.update();

        flicker.setPosition(0);
    }

    protected void moveToCryptoBox(){
        float movementAngle = simpleStartingPosition.getMovementAngle();
        float baseDistance = simpleStartingPosition.getBaseDistance();

        gyroTurn(movementAngle);
        motion.move(baseDistance);
    }

    public void gyroTurn(final float turn){
        final int heading = gyro.getIntegratedZValue();

        motion.turn(turn > 0, new Condition() {
            @Override
            public boolean isTrue() {
                currentStep = "turning " + turn + " degrees.  Degrees left: " + (turn < 0 ? -1 : 1) * (gyro.getIntegratedZValue() - (heading + turn));
                telemetry.update();
                return (turn < 0 ? -1 : 1) * (gyro.getIntegratedZValue() - (heading + turn)) > 0;
            }
        });
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
