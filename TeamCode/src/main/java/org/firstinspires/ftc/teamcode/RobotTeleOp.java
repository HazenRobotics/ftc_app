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


            telemetry.update();
            idle();
        }
    }

    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/


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
//


    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }

    //Add new methods for functionality down here
    
}
