package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;

/**
 * Created by Robotics on 12/1/2017.
 */

@TeleOp(name="MechMotorTest", group="Testing")
public class MecMotorTest extends LinearOpMode implements IHardware {

    //Add Motors, Servos, Sensors, etc here
    // EX: protected DcMotor motor;

    //Claw and Arm Objects
    protected DcMotor armMotor;
    protected DcMotor claw;

    //Wheel Motors
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    protected MechanamMotors motion;


    //Lift Objects
    protected DcMotor lift;

    //Flicker Objects
    protected Servo flicker;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final double CLAW_POWER = 0.2;
    protected final double LIFT_POWER = 0.3;
    protected final double JOYSTICK_ERROR_RANGE = 0.1;


    @Override
    public void runOpMode() {

        setupHardware();
        //Add any further initialization (methods) here

        waitForStart();



        while (opModeIsActive()) {

            motion.move(24);
            idle();
            break;
            /*leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);

            double leftFrontPower = 0.2;
            double rightFrontPower = 0.2;
            double leftBackPower = 0.2;
            double rightBackPower = 0.2;


            //leftFront
            if(gamepad1.right_stick_y>0){
                leftFront.setPower((leftFrontPower));
            }
            else{
                leftFront.setPower(0);
            }

            //rightFront
            if(gamepad1.right_stick_y>0){
                leftFront.setPower((leftFrontPower));
            }
            else{
                leftFront.setPower(0);
            }

            //leftBack
            if(gamepad1.right_stick_y>0){
                leftFront.setPower((leftFrontPower));
            }
            else{
                leftFront.setPower(0);
            }

            //rightBack
            if(gamepad1.right_stick_y>0){
                leftFront.setPower((leftFrontPower));
            }
            else{
                leftFront.setPower(0);
            }


            //setting power for each of the 4 wheels
            leftFront.setPower(leftFrontPower);
            rightFront.setPower(rightFrontPower);
            leftBack.setPower(leftBackPower);
            rightBack.setPower(rightBackPower);*/

           // double flickerpos = flicker.getPosition();

            /*if(flickerpos > 1){
                flickerpos = 1;
            }
            else if(flickerpos < 0 ){
                flickerpos = 0;
            }*/

            /*flicker.setPosition(Math.abs(flickerpos));

            telemetry.addData("flicker pos: ", flicker.getPosition());
            telemetry.update();*/

        }
    }


    protected void setupHardware() {

        leftFront = getMotor("leftFront");
        rightFront = getMotor("rightFront");
        leftBack = getMotor("leftBack");
        rightBack = getMotor("rightBack");

        flicker = getServo("flicker");
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    @Override
    public void idle(long milliseconds) {
        // This is probably the wrong way to handle this-- spin loop.
        // However, it's better than Thread.idleFor()-- probably.
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            telemetry.update();
            idle();
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
