package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.controllers.IHardware;
import org.firstinspires.ftc.teamcode.sensors.I2cColorSensor;
import org.firstinspires.ftc.teamcode.sensors.I2cRangeSensor;

@Disabled
@TeleOp(name="TeleOpOld", group="TeleOp")
public class RobotTeleOpOld extends LinearOpMode implements IHardware {

    //Add all global objects and lists
    protected ButtonManager buttons = new ButtonManager();

    //Scoop Vars

    protected boolean scoopUp = true;

    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;

    //Sensors
    I2cRangeSensor range;

    I2cColorSensor color;

    //Wheel Motors
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Lift Objects
    protected Servo scoop;

    //Flicker
    protected Servo flicker;

    //Add all Constants here

    //Scoop Constants
    protected static final double SCOOP_LOWERED_POSITION = 0.0; //Servo position to which the scoop will move to when lowering
    protected static final double SCOOP_RAISED_POSITION = 0.75; //Servo position to move to to raise scoop to correct height


    @Override
    public void runOpMode() {

        setupHardware();
        setupButtons();
        //Add any further initialization (methods) here

        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            //Add any non-toggles here
            scoop();
            drive();
            flickerControl();

            telemetry.addData("color>", color.readColor());
            telemetry.addData("Range>", range.readUltrasonic(DistanceUnit.INCH));

            telemetry.update();
            idle();
        }
    }



    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = getMotor("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/
        scoop = getServo("scoop");

        leftFront = getMotor("leftFront");
        rightFront = getMotor("rightFront");
        leftBack = getMotor("leftBack");
        rightBack = getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        flicker = getServo("flicker");

        scoop.setPosition(SCOOP_RAISED_POSITION);

        flicker.setPosition(1.0);

        range = new I2cRangeSensor((I2cDevice) get("rangeSensor"));
        color = new I2cColorSensor((I2cDevice) get("jewelSensor"));
    }

    //claw function, run by servo
    protected void setupButtons() {

        //Toggles the scoop being up or down by default
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.right_bumper;
            }

            @Override
            public void onPress() {
                scoopUp = !scoopUp;
            }
        });
    }

    protected void drive() {
        telemetry.addData("servo pos: ", flicker.getPosition());

        //left stick controls movement
        //right stick controls turning

        double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double magnitude = Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.left_stick_x) + Math.abs(turn_x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.right_stick_y;


        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower = (y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;

        //setting power for each of the 4 wheels
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    //Add new methods for functionality down here

    protected void scoop() {
        //Right Trigger Controls The Scoop
        if(scoopUp) {
            scoop.setPosition(SCOOP_RAISED_POSITION - gamepad2.right_trigger * SCOOP_RAISED_POSITION);
        } else {
            scoop.setPosition(gamepad2.right_trigger * SCOOP_RAISED_POSITION);
        }

        telemetry.addData("Right Trigger >", gamepad2.right_trigger);
    }

    protected void flickerControl()
    {
        //Down by default, left trigger moves it up
        flicker.setPosition(1.0 - gamepad2.left_trigger);
    }

    @Override
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
