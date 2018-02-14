package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.input.Gamepad;
import org.firstinspires.ftc.teamcode.input.Joystick;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.output.Telemetry;

@TeleOp(name="CompTeleOp", group="TeleOp")
public class RobotTeleOp extends LinearOpMode implements IHardware {
    // Input and output mappings
    public final Gamepad gamepad1 = new Gamepad(super.gamepad1);
    public final Gamepad gamepad2 = new Gamepad(super.gamepad2);
    public final Telemetry telemetry = new Telemetry(super.telemetry);

    // The claw and arm
    protected DcMotor arm;
    protected DcMotor claw;

    // Wheel motors
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    protected DcMotor lift;

    protected Servo flicker;

    protected ModernRoboticsI2cGyro gyro;

    protected final static double CLAW_POWER = 0.4;
    protected final static double LIFT_POWER = 0.3;
    protected final static int AUTO_TURN_AMOUNT = 90;

    protected MechanamMotors motion;

    // Initialize the motor etc. variables
    protected void setupHardware() {
        gyro.calibrate();
        while(gyro.isCalibrating()){
            idle();
        }

        lift = getMotor("lift");
        lift.setDirection(DcMotor.Direction.FORWARD);

        claw = getMotor("claw");
        claw.setDirection(DcMotor.Direction.FORWARD);
        arm = getMotor("arm");
        arm.setDirection(DcMotor.Direction.FORWARD);

        leftFront = getMotor("leftFront");
        rightFront = getMotor("rightFront");
        leftBack = getMotor("leftBack");
        rightBack = getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        motion = new MechanamMotors(this);

        flicker = getServo("flicker");
        flicker.setDirection(Servo.Direction.REVERSE);
        flicker.setPosition(0);

        sleep(500);
    }

    protected ButtonPair claw_input = gamepad2.dpad.x;
    protected ButtonPair lift_input = gamepad2.dpad.y;
    protected Trigger arm_input = gamepad2.right_stick.y;

    protected Trigger turn_input = gamepad1.right_stick.x;
    protected Joystick strafe_input = gamepad1.left_stick;

    protected ButtonPair autoTurn_input = gamepad1.makePair(gamepad1.b, gamepad2.x);

    @Override
    public void runOpMode() {
        setupHardware();
        //Add any further initialization (methods) here

        waitForStart();

        autoTurn_input.listenSign(EventType.CHANGED, EventListener.fromMethod(this, "autoTurn"));
        // The "unsafe" constructors aren't really unsafe-- it's just due to a java limitation that I have to add them.
        // It's more unsafe in the "trust me" sense than the "unpredictable" sense.
        // TODO: method accessors so I can write arm_input.pressure() instead
        telemetry.add("Arm power: ", FieldAccessor.unsafe("y", gamepad2.right_stick));

        while (opModeIsActive()) {
            gamepad1.update();
            gamepad2.update();

            claw.setPower(-claw_input.sign() * CLAW_POWER);
            lift.setPower(lift_input.sign() * LIFT_POWER);
            arm.setPower(arm_input.value());
            drive();

            telemetry.update();
            idle();
        }
    }

    protected void drive() {
        double turn_x = turn_input.value();
        double x = strafe_input.x();
        double y = -strafe_input.y();
        // The total change, used to scale down the power.
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x);
        // Used to scale the power down to make sure it doesn't go over 1, via division below.
        double scale = Math.max(1, magnitude);

        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower = (y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;

        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    public void autoTurn(final IButton button) {
        if(button.sign() == 0) return;
        final int startingDirection = gyro.getIntegratedZValue();
        motion.turn(true, new Condition() {
            @Override
            public boolean isTrue() {
                if(turn_input.pressed() || strafe_input.pressed())
                    return true;
                double currentDirection = gyro.getIntegratedZValue();
                return (currentDirection - (startingDirection + button.sign() * AUTO_TURN_AMOUNT)) > 0;
            }
        });
    }

    // Implementations of the IHardware functions.
    // These are also used internally because they're convenient.

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
