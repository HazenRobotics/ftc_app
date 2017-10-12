package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Alex on 9/23/2017.
 */

@TeleOp(name="TeleOp", group="TeleOp")
@Disabled
public class RobotTeleOp extends LinearOpMode {

    //Add all global objects and lists
    protected ArrayList<Button> ControlList = new ArrayList<Button>();

    //Lift Vars
    protected boolean autoMainLiftRunning = false;
    protected int lift_position;


    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;

    //Lift Objects
    protected DcMotor mainLift;
    protected Servo smallLift;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;

    //Lift Constants
    protected static final double SMALL_LIFT_LOWER_POS = 0.0, SMALL_LIFT_UPPER_POS = 0.0;
    protected static final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double LIFT_GEAR_REDUCTION = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double LIFT_INCHES_PER_REV = 0.0;// Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;
    protected static final int MAIN_LIFT_ERROR_RANGE = 20;

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        //Add any further initialization (methods) here



        waitForStart();

        while (opModeIsActive()) {
            toggleLogic();

            lift();

            //Add any non-toggles here
            lift_position = mainLift.getCurrentPosition();
            telemetry.addData("main lift position","MainLift Position:"+String.format("%.2f",lift_position));
            telemetry.update();
            idle();
        }
    }

    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/
        mainLift = hardwareMap.dcMotor.get("mainLift");
        mainLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


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


        ControlList.add(new Button() {
            protected boolean input() {return gamepad2.dpad_up;}
            protected void turnOn() {
                calculateTargetPositionUP();
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mainLift.setPower(MAIN_LIFT_SPEED);
                autoMainLiftRunning = true;}
            protected void debug() {}
        });

        ControlList.add(new Button() {
            protected boolean input() {return gamepad2.dpad_down;}
            protected void turnOn() {
                calculateTargetPositionDOWN();
                mainLift.setPower(-MAIN_LIFT_SPEED);
                autoMainLiftRunning = true;}
            protected void debug() {}
        });
    }
    //

    protected void toggleLogic() {
        for(Button b: ControlList) {
            b.logic();
            b.debug();
        }
    }

    //Add new methods for functionality down here

    //Sets new position for main life when using the up d pad by using the current position to figure out what height marker it is inbetween.

    protected void calculateTargetPositionUP() {
        lift_position = mainLift.getCurrentPosition();
        int glyphRow = lift_position / COUNT_PER_GLYPH_HEIGHT;

        int errorOver = lift_position % COUNT_PER_GLYPH_HEIGHT;
        int errorUnder = COUNT_PER_GLYPH_HEIGHT - errorOver;

        if (errorUnder<MAIN_LIFT_ERROR_RANGE) {
            mainLift.setTargetPosition(COUNT_PER_GLYPH_HEIGHT*(glyphRow+2));
        }
	    else {
            mainLift.setTargetPosition(COUNT_PER_GLYPH_HEIGHT * (glyphRow + 1));
        }
    }


    protected void calculateTargetPositionDOWN() {
        lift_position = mainLift.getCurrentPosition();
        int glyphRow = lift_position / COUNT_PER_GLYPH_HEIGHT;

        int errorOver = lift_position % COUNT_PER_GLYPH_HEIGHT;
        //int errorUnder = COUNT_PER_GLYPH_HEIGHT - errorOver;

        if (errorOver < MAIN_LIFT_ERROR_RANGE) {
            mainLift.setTargetPosition(COUNT_PER_GLYPH_HEIGHT * (glyphRow - 1));
        } else{
            mainLift.setTargetPosition(COUNT_PER_GLYPH_HEIGHT * glyphRow);
        }
    }

    protected void lift() {
        //Main Lift Power using Triggers
        if((gamepad2.right_trigger > 0) && (gamepad2.left_trigger == 0)) {
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(gamepad2.right_trigger);
            autoMainLiftRunning = false;
        }
        else if((gamepad2.right_trigger == 0) && (gamepad2.left_trigger > 0)){
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(-gamepad2.left_trigger);
            autoMainLiftRunning = false;
        }
        else if (!autoMainLiftRunning){
            mainLift.setPower(0.0);
        }

        //D Pad used to control Main Lift (Added as buttons), stops here
        if(!(mainLift.isBusy() && autoMainLiftRunning)){
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(0);
            autoMainLiftRunning = false;
        }

        //Small Lift Power
        if (gamepad2.right_bumper){
            smallLift.setPosition(SMALL_LIFT_LOWER_POS);
        }
        else if (gamepad2.left_bumper){
            smallLift.setPosition(SMALL_LIFT_UPPER_POS);
        }
    }
}
