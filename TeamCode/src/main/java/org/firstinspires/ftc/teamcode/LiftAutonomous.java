package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Robotics on 10/17/2017.
 */





@Disabled
public class LiftAutonomous extends LinearOpMode {

    protected static final double SMALL_LIFT_LOWER_POS = 0.0, SMALL_LIFT_UPPER_POS = 0.0;
    protected static final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double LIFT_GEAR_REDUCTION = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double LIFT_INCHES_PER_REV = 0.0;// Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;
    protected static final int MAIN_LIFT_ERROR_RANGE = 20;

    protected static double lift_position;


    protected DcMotor mainLift;
    protected Servo smallLift;

    @Override
    public void runOpMode() {

        setupHardware();
        //Add any further initialization (methods) here

        waitForStart();

        while (opModeIsActive()) {

        }
    }


    protected void setupHardware(){
        mainLift = hardwareMap.dcMotor.get("mainLift");
        mainLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



    }

    public void setMainLiftTo(double glyphHeightMultiplier) {
        if (glyphHeightMultiplier > 4) {
            glyphHeightMultiplier = 4;
        }
        else {
            int nextPosition = (int)(GLYPH_HEIGHT * glyphHeightMultiplier);
            mainLift.setTargetPosition(nextPosition);
        }
        if (glyphHeightMultiplier < 0) {
            glyphHeightMultiplier = 0;
        }
        else {
            int nextPosition = (int)(GLYPH_HEIGHT * glyphHeightMultiplier);
            mainLift.setTargetPosition(nextPosition);
        }
    }
    /* no needed
    public void changeMainLiftBy(double glyphHeightChange){
        lift_position = mainLift.getCurrentPosition();
        if (glyphHeightChange <0) {
            if (lift_position + glyphHeightChange >= 0) {
        }
        else if (glyphHeightChange > 0){

        }
        else
            //don't change anything, it was set to 0
    }
    */

    public void setSmallLiftTop(){
        smallLift.setPosition(SMALL_LIFT_UPPER_POS);
    }

    public void setSmallLiftBottom(){
        smallLift.setPosition(SMALL_LIFT_LOWER_POS);
    }



}
