package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.controllers.IHardware;
import org.firstinspires.ftc.teamcode.controllers.MecanumWheels;
import org.firstinspires.ftc.teamcode.input.ButtonManager;

public class TestDriving extends LinearOpMode implements IHardware {


    protected static final float DRIVE_SPEED = 1.0f;

    protected ButtonManager buttons;
    protected MecanumWheels wheels;

    @Override
    public void runOpMode() {

        setupHardware();
        setupButtons();
        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            drive();
            idle();
        }
    }

    protected void setupHardware() {
        wheels = new MecanumWheels(this);

        /* vs
        leftFront = getMotor("leftFront");
        rightFront = getMotor("rightFront");
        leftBack = getMotor("leftBack");
        rightBack = getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
         */
    }

    protected void setupButtons() {
        buttons = new ButtonManager();
    }


    protected void drive() {
        double turn_x = gamepad1.right_stick_x;
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x);
        double scale = Math.max(1, magnitude);

        MecanumWheels.Coefficients coefficients = new MecanumWheels.Coefficients();
        coefficients.leftFront = (y + x + turn_x) / scale;
        coefficients.rightFront = (y - x - turn_x) / scale;
        coefficients.leftBack = (y - x + turn_x) / scale;
        coefficients.rightBack = (y + x - turn_x) / scale;
        wheels.setPower(coefficients, DRIVE_SPEED);

        /* vs
         double turn_x = gamepad1.right_stick_x;
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x);
        double scale = Math.max(1, magnitude);

        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower = (y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
         */
    }

    @Override
    public void idle(long milliseconds) {
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
