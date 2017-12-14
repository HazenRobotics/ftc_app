package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

/**
 * Created by Robotics on 11/9/2017.
 */
public class BackUpAutonomous extends LinearOpMode{

    protected DcMotor leftFront;
    protected DcMotor leftBack;
    protected DcMotor rightFront;
    protected DcMotor rightBack;
    protected I2cRangeSensor rangeSensor;

    protected double motorPower;


    public void runOpMode(){
        setupHardware();

        waitForStart();

    }
    public void setupHardware()
    {
        leftFront = hardwareMap.dcMotor.get("leftFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        rightBack = hardwareMap.dcMotor.get("rightBack");

        motorPower = 1;

    }
    public void driveForward(){
        leftFront.setPower(motorPower);
        leftBack.setPower(motorPower);
        rightFront.setPower(motorPower);
        rightBack.setPower(motorPower);
    }
}

