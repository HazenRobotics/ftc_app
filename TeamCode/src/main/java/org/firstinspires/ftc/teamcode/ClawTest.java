package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.input.Toggle;

/**
 * Created by Robotics on 11/19/2017.
 */

public class ClawTest {
    protected boolean clawClosing = false;

    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;

    //Claw and Arm Objects
    protected DcMotor armMotor;
    protected CRServo armControlServo;
    protected boolean armManual = false;
    protected DcMotor claw;
    //TODO: ??
    //protected DigitalChannel limitOpen;
    protected DigitalChannel limitClosed;
    protected ElapsedTime clawRuntime = new ElapsedTime();

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final double ARM_MOTOR_POWER = 0.4;
    protected final double ARM_SERVO_POWER = 0.4;
    protected final double CLAW_POWER = 0.2;
    protected final double JOYSTICK_ERROR_RANGE = 0.1;

    claw = getMotor("claw");
        claw.setDirection(DcMotor.Direction.FORWARD);
    armControlServo = hardwareMap.crservo.get("armControlServo");
    armMotor = getMotor("armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);*/

      /*//limitOpen = hardwareMap.get(DigitalChannel.class, "clawOpenSensor");
        limitClosed = hardwareMap.get(DigitalChannel.class, "clawClosedSensor");
        //limitOpen.setMode(DigitalChannel.Mode.INPUT);
        limitClosed.setMode(DigitalChannel.Mode.INPUT);*/
}

    //claw function, run by servo
    protected void setupButtons() {


        //when a is pressed once, claw closes.  when pressed again, claw opens.
        buttons.add(new Toggle() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.a;
            }

            @Override
            public void onActivate() {
                claw.setPower(CLAW_POWER);
                clawClosing = true;
            }

            @Override
            public void onDeactivate() {
                claw.setPower(-CLAW_POWER);
                clawClosing = false;
            }
        });
    }

    //when claw has reached the correct position or moved open long enough, the claw stops moving.
    protected void claw() {
        //TODO:???
        if((clawClosing && limitClosed.getState())/* || (!clawClosing && limitOpen.getState())*/) {
            claw.setPower(0);
        }
        else if(!clawClosing)
        {
            clawRuntime.reset();
            while (opModeIsActive() && (clawRuntime.seconds() < 1.5)) {
                idle();
            }
            claw.setPower(0);

            //when a is pressed once, claw closes.  when pressed again, claw opens.
            buttons.add(new Toggle() {
                @Override
                public boolean isInputPressed() {
                    return gamepad2.a;
                }

                @Override
                public void onActivate() {
                    claw.setPower(CLAW_POWER);
                    clawClosing = true;
                }

                @Override
                public void onDeactivate() {
                    claw.setPower(-CLAW_POWER);
                    clawClosing = false;
                }
            });
        }

        //when claw has reached the correct position or moved open long enough, the claw stops moving.
    protected void claw() {
        //TODO:???
        if((clawClosing && limitClosed.getState())/* || (!clawClosing && limitOpen.getState())*/) {
            claw.setPower(0);
        }
        else if(!clawClosing)
        {
            clawRuntime.reset();
            while (opModeIsActive() && (clawRuntime.seconds() < 1.5)) {
                idle();
            }
            claw.setPower(0);

























}
