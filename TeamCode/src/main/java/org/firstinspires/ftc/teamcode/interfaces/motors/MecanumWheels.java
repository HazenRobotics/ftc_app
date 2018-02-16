package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.models.Condition;

public class MecanumWheels implements IWheels {
    protected IHardware hardware;

    //Declare motor Variables
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;
    
    protected static final float DRIVE_SPEED = 0.5f;
    protected static final float TURN_SPEED = 0.3f;
    protected static final AngleUnit DEFAULT_ANGLE_UNIT = AngleUnit.DEGREES;

    public MecanumWheels(IHardware hardware) {
        this.hardware = hardware;
        
        leftFront = hardware.getMotor("leftFront");
        leftBack = hardware.getMotor("leftBack");
        rightFront = hardware.getMotor("rightFront");
        rightBack = hardware.getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void move(Condition condition, boolean positiveDir) {
        move(condition, positiveDir ? 0f : 180f, DEFAULT_ANGLE_UNIT, DRIVE_SPEED);
    }

    public void move(Condition condition, float strafeAngle, AngleUnit unit, float speed) {
        double strafeAngleRadians = unit.toRadians(strafeAngle);
        double x = Math.sin(strafeAngleRadians);
        double y = Math.cos(strafeAngleRadians);
        
        //Used to determine the greatest possible value of y +/- x to scale them
        double magnitude = Math.abs(y) + Math.abs(x); 
        //Used to prevent setting motor to power over 1
        double scale = Math.max(1, magnitude); 

        double leftFrontPower = (y - x) / scale;
        double rightFrontPower = (y - x) / scale;
        double leftBackPower = (y + x) / scale;
        double rightBackPower = (y - x) / scale;

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (!condition.isTrue()) {
            hardware.idle();
        }
        
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }

    @Override
    public void turn(Condition condition, boolean positiveDir) {
        turn(condition, positiveDir, TURN_SPEED);
    }
    
    public void turn(Condition condition, boolean positiveDir, float speed) {
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

        if (positiveDir) {
            leftFrontPower = -DRIVE_SPEED;
            leftBackPower = -DRIVE_SPEED;
            rightFrontPower = DRIVE_SPEED;
            rightBackPower = DRIVE_SPEED;
        } else {
            leftFrontPower = DRIVE_SPEED;
            leftBackPower = DRIVE_SPEED;
            rightFrontPower = -DRIVE_SPEED;
            rightBackPower = -DRIVE_SPEED;
        }

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (!condition.isTrue()) {
            hardware.idle();
        }
        
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }
}
