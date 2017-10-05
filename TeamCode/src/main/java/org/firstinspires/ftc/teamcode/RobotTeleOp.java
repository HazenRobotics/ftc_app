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
    protected ArrayList<Toggle> ToggleList = new ArrayList<Toggle>();


    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;
    protected DcMotor mainLift;
    protected Servo smallLift;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final double SMALL_LIFT_LOWER_POS = 0.0, SMALL_LIFT_UPPER_POS = 0.0;
    protected final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        //Add any further initialization (methods) here

        double currentPosition = 0.0, nextPosition = 0.0;

        waitForStart();

        while (opModeIsActive()) {
            toggleLogic();

            //Add any non-toggles here


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



    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }

    //Add new methods for functionality down here


    protected void lift() {
        //Encoder for Main Lift
        double currentNumberOfRotations = mainLift.getCurrentPosition()/COUNTS_PER_MOTOR_REV;

        //Main Lift Power using Triggers
        if((gamepad2.right_trigger>0) && (gamepad2.left_trigger==0)){

            mainLift.setPower(gamepad2.right_trigger);
        }
        else if((gamepad2.right_trigger==0) && (gamepad2.left_trigger>0)){

            mainLift.setPower(-(gamepad2.left_trigger));
        }
        else {
            mainLift.setPower(0.0);
        }


        //set next target position
/**        For the following .setTargetPositions, insert position of each stacked glyph**/
            if ((mainLift.getCurrentPosition() > GLYPH_HEIGHT * 0) && (mainLift.getCurrentPosition() < GLYPH_HEIGHT * 1)) {
//                mainLift.setTargetPosition();
            }
            else if ((mainLift.getCurrentPosition() > GLYPH_HEIGHT * 1) && (mainLift.getCurrentPosition() < GLYPH_HEIGHT * 2)) {
//                mainLift.setTargetPosition();
            }
            else if ((mainLift.getCurrentPosition() > GLYPH_HEIGHT * 2) && (mainLift.getCurrentPosition() < GLYPH_HEIGHT * 3)) {
//                mainLift.setTargetPosition();
            }
            else if ((mainLift.getCurrentPosition() > GLYPH_HEIGHT * 3) && (mainLift.getCurrentPosition() < GLYPH_HEIGHT * 4)) {
//                mainLift.setTargetPosition();
            }

            //D Pad used to control Main Lift

            if(!(mainLift.isBusy())){
                mainLift.setPower(0); //Redundant???
            }


            if(gamepad2.dpad_up == true) {
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mainLift.setPower(0.5);
            }

            if(gamepad2.dpad_down == true) {
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mainLift.setPower(-0.5);
            }





            //Small Lift Power
            if (gamepad2.right_bumper == true){
                smallLift.setPosition(SMALL_LIFT_LOWER_POS);
            }
            else if (gamepad2.left_bumper == true){
                smallLift.setPosition(SMALL_LIFT_UPPER_POS);
            }



    }

}
