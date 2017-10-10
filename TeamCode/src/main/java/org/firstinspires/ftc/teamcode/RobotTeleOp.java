package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Alex on 9/23/2017.
 */

@TeleOp(name="TeleOp", group="TeleOp")
@Disabled
public class RobotTeleOp extends LinearOpMode {

    //Add all global objects and lists
    protected ArrayList<Toggle> ToggleList = new ArrayList<Toggle>();


    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;
    protected DcMotor mainLift;
    protected Servo smallLift;

    //Motors for each wheel
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final double SMALL_LIFT_LOWER_POS = 0.0, SMALL_LIFT_UPPER_POS = 0.0;
    protected final double GLYPH_HEIGHT = 0.0;

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        //Add any further initialization (methods) here

        double currentPosition = 0.0, nextPosition = 0.0;
        int glyphOne  , glyphTwo  , glyphThree  , glyphFour  ,glyphZero; //glyphZero is the ground

        waitForStart();

        while (opModeIsActive()) {
            toggleLogic();

            //Add any non-toggles here

            //Main Lift Power
            if((gamepad2.right_trigger>0) && (gamepad2.left_trigger==0)){

                mainLift.setPower(gamepad2.right_trigger);
            }
            else if((gamepad2.right_trigger==0) && (gamepad2.left_trigger>0)){

                mainLift.setPower(-(gamepad2.left_trigger));
            }
            else {
                mainLift.setPower(0.0);
            }

            //Small Lift Power
            if (gamepad2.right_bumper == true){
                smallLift.setPosition(SMALL_LIFT_LOWER_POS);
            }
            else if (gamepad2.left_bumper == true){
                smallLift.setPosition(SMALL_LIFT_UPPER_POS);
            }

            //nextPosition
            /*if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphOne;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphTwo;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphThree;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphFour;
            }

            //D Pad used to control Main Lift
            if((gamepad2.dpad_up == false) && (gamepad2.dpad_down == false)) {
                DPadMoving = false;
            }


            if(gamepad2.dpad_up == true) {
                DPadMoving = true
                mainLift.setPower(nextPosition-currentPosition);
            }*/

            drive();

            telemetry.update();
            idle();
        }
    }

    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/

        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightBack = hardwareMap.dcMotor.get("rightBack");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);


    }

    protected void setupToggleList() {
        //Add any mechanics that can be controlled by a toggle here
        /*EX:
        ToggleList.add(new Toggle() {
            protected boolean input() {return gamepad1.a;}
            protected void turnOn() {motor.setPower(MOTOR_POWER);}
            protected void turnOff() {motor.setPower(0);}
            protected void debug() {telemetry.addData("Motor", "On: %b, Power: %.2f", isOn(), (isOn() ? MOTOR_POWER : 0.0));}
        })*/



    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }

    protected void drive() {

        //left stick controls movement
        //right stick controls turning

        double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x); //total sum of all inputs
        double scale = Math.max(1, magnitude); //determines whether magnitude or 1 is greater (prevents from setting motor to power over 1)


        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower =(y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;

        //setting power for each of the 4 wheels
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    /*
    calculate power stuff
        y = sin(angle)
        x = cos(angle)
    calculate count distance
    find target postion by:
        target position = current pos + counts * wheel power
    set target position
    run using encoders
    set power
    */

    //Add new methods for functionality down here
    
}
