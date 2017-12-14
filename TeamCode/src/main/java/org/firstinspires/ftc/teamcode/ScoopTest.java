package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.input.old.Button;
import org.firstinspires.ftc.teamcode.input.old.ButtonManager;

/**
 * Created by Robotics on 10/28/2017.
 */
@Disabled
@TeleOp(name = "ScoopTest",group = "Testing")
public class ScoopTest extends LinearOpMode {
    protected Servo scoop;
    protected ButtonManager buttons = new ButtonManager();

    @Override
    public void runOpMode()  {
        setupHardware();
        setupButtons();
        waitForStart();
        while(opModeIsActive()){
          buttons.update();
            if (gamepad1.x){
                scoop.setPosition(1.0);
            }
            if ((gamepad1.b)){
                scoop.setPosition(-1.0);
            }

            telemetry.addData("StickPos", -gamepad1.left_stick_y);
            telemetry.addData("ScoopPos: ", scoop.getPosition());
            telemetry.update();
        }
    }

    protected void setupHardware(){
        scoop = hardwareMap.servo.get("scoop");

    }

    protected void setupButtons() {
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad1.a;
            }

            @Override
            public void onPress() {
                scoop.setPosition(gamepad1.left_stick_y);


            }

        });


    }
}
