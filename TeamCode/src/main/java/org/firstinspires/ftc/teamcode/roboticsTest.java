package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.input.old.Button;
import org.firstinspires.ftc.teamcode.input.old.ButtonManager;

/**
 * Created by Robotics on 10/21/2017.
 */
@Disabled
@TeleOp(name="robtoicsTest", group="Testing")
public class roboticsTest extends LinearOpMode {
    protected DcMotor mainLift;
    private ElapsedTime runtime = new ElapsedTime();

    protected ButtonManager buttons = new ButtonManager();

    protected static final double MAIN_LIFT_SPEED = 0.9;
    protected static final double MIAN_LIFT_MAX_SPEED = 1.0;

    @Override
    public void runOpMode() {
        setupHardware();
        setupButtons();
        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            //lift Motor Manual
            if (Math.abs(gamepad1.right_stick_y) > 0 ) {
                mainLift.setPower(gamepad1.right_stick_y);
            }

            //Lift motor Auto
            if(!(mainLift.isBusy())){
                mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                mainLift.setPower(0);
            }

            /*if(gamepad2.dpad_up) {
                lift.setTargetPosition(1120);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(LIFT_SPEED);
            }*/


            telemetry.addData("stick power", gamepad1.right_stick_y);
            telemetry.addData("LiftPos: ", mainLift.getCurrentPosition());
            telemetry.addData("LiftTargetPos: ", mainLift.getTargetPosition());

            telemetry.update();

        }
    }

    protected void setupHardware() {
        mainLift = hardwareMap.dcMotor.get("lift");

        mainLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


    }

    protected void setupButtons() {
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.dpad_up;
            }

            @Override
            public void onPress() {
               mainLift.setTargetPosition(1120);
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                runtime.reset();

                mainLift.setPower(MAIN_LIFT_SPEED);

            }

        });
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.dpad_down;
            }

            @Override
            public void onPress() {
                mainLift.setTargetPosition(0);

                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                runtime.reset();
                mainLift.setPower(MAIN_LIFT_SPEED);

            }});

    }


}
