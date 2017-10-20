package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by davegoldy on 4/3/17.
 */
@Autonomous(button = "Autonomous", group = "Autonomous")
public class ProgramControl extends LinearOpMode
{
    private DcMotor fRightMotor;
    private DcMotor fLeftMotor;
    private ColorSensor fColorSensor;
    private TouchSensor fTouchSensor;
    private ModernRoboticsI2cRangeSensor fFrontDistance;
    private boolean fLastCheckSawBlack = false;
    private int fLineCount = 0;
    private double fStartTimer = -1.0;

    @Override
    public void runOpMode() throws InterruptedException
    {
        setupHardware();
        waitForStart();
        countLines();
        turn();
        followLine();
        touchSensor();
        distanceSensor();
    }

    /**
     * This method will drive forward and count the number of lines it sees. When the 4th
     * line is seen, the robot will stop.
     */
    private void countLines()
    {
        boolean keepRunning = true;
        while (opModeIsActive() && keepRunning)
        {
            telemetry.addData("Red", "Red = " + fColorSensor.red());
            telemetry.addData("Green", "Green = " + fColorSensor.green());
            telemetry.addData("Blue", "Blue = " + fColorSensor.blue());
            if (fColorSensor.red() < 10 && fColorSensor.green() < 10 && fColorSensor.blue() < 5
                    && fLastCheckSawBlack == false)
            {
                fLineCount = fLineCount + 1;
                fLastCheckSawBlack = true;
            }
            else if (fColorSensor.red() > 10 && fColorSensor.green() > 10 && fColorSensor.blue() > 5
                    && fLastCheckSawBlack == true)
            {
                fLastCheckSawBlack = false;
            }
            if (fLineCount == 4 && fLastCheckSawBlack == false)
            {
                fLeftMotor.setPower(0.0);
                fRightMotor.setPower(0.0);
                keepRunning = false;
            }
            else
            {
                fLeftMotor.setPower(0.1);
                fRightMotor.setPower(0.1);
            }
            telemetry.addData("Line Count", "Line Count = " + String.format("%d", fLineCount));
            telemetry.update();
            sleep(10);
        }
    }

    /**
     * This method will spend .5 seconds turning the robot to the left and driving it forward. Next
     * it will pivot the robot for .5 seconds. At the end of 1 second, the robot will start the
     * the line following step.
     */
    private void turn()
    {
        fStartTimer = getRuntime();
        boolean keepRunning = true;
        while (opModeIsActive() && keepRunning)
        {
            double turnTime = getRuntime() - fStartTimer;
            if (turnTime < 0.5)
            {
                fRightMotor.setPower(0.2);
                fLeftMotor.setPower(0.1);
            }
            else if (turnTime < 0.9)
            {
                fRightMotor.setPower(0.2);
                fLeftMotor.setPower(-0.2);
            }
            else
            {
                fRightMotor.setPower(0.0);
                fLeftMotor.setPower(0.0);
                keepRunning = false;
            }
            telemetry.addData("Turn Time", "Turn Time = " + String.format("%.2f", turnTime));
            telemetry.update();
            sleep(10);
        }
    }

    /**
     * This method will spend .5 seconds backing up and another .5 seconds turning the robot to the right.
     * At the end of this method, the distance sensor processing will start.
     */
    private void touchSensor()
    {
        fStartTimer = getRuntime();
        boolean keepRunning = true;
        while (opModeIsActive() && keepRunning)
        {
            if (getRuntime() - fStartTimer < 0.5)
            {
                fRightMotor.setPower(-0.12);
                fLeftMotor.setPower(-0.12);
            }
            else if (getRuntime() - fStartTimer < 1.0)
            {
                fRightMotor.setPower(-0.2);
                fLeftMotor.setPower(0.4);
            }
            else
            {
                fRightMotor.setPower(0.0);
                fLeftMotor.setPower(0.0);
                keepRunning = false;
            }
            sleep(10);
        }
    }

    /**
     * This method will follow the left edge of the black line. This method is done when the touch
     * sensor on the front of the robot is touched. Once the touch sensor is touched, the line
     * following step will start.
     */
    private void followLine()
    {
        boolean keepRunnng = true;
        while (opModeIsActive() && keepRunnng)
        {
            if (fTouchSensor.isPressed())
            {
                fRightMotor.setPower(0.0);
                fLeftMotor.setPower(0.0);
                keepRunnng = false;
                telemetry.addData("Line following", "Done");
            }
            else
            {
                if (fColorSensor.red() < 10 && fColorSensor.green() < 10 && fColorSensor.blue() < 5)
                {
                    fRightMotor.setPower(0);
                    fLeftMotor.setPower(0.2);
                    telemetry.addData("Line following", "Power left motor.");
                }
                else
                {
                    fRightMotor.setPower(0.2);
                    fLeftMotor.setPower(0.0);
                    telemetry.addData("Line following", "Power right motor.");
                }
            }
            telemetry.update();
            sleep(10);
        }
    }

    /**
     * This method will drive the robot forward until the distance sensor on the front of the robot is
     * about 3 inches from the wall. At this point the robot will stop moving and the
     * autonomous program is complete.
     */
    private void distanceSensor()
    {
        boolean keepRunning = true;
        while (opModeIsActive() && keepRunning)
        {
            double frontDistance = fFrontDistance.getDistance(DistanceUnit.CM);
            if (frontDistance < 15.0)
            {
                //We are at the wall
                fRightMotor.setPower(0.0);
                fLeftMotor.setPower(0.0);
                fColorSensor.enableLed(false);
                keepRunning = false;
            }
            else
            {
                fRightMotor.setPower(0.1);
                fLeftMotor.setPower(0.1);
            }
            telemetry.addData("Front Distance", "Front Distance = " + String.format("%.2f", frontDistance));
            telemetry.update();
            sleep(10);
        }
    }

    /**
     * All if the initial hardware setup code belongs in this method.
     *
     * @throws InterruptedException
     */
    protected void setupHardware() throws InterruptedException
    {
        fLeftMotor = hardwareMap.dcMotor.get("leftMotor");
        fRightMotor = hardwareMap.dcMotor.get("rightMotor");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        fLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        fRightMotor.setDirection(DcMotor.Direction.FORWARD);

        fColorSensor = hardwareMap.colorSensor.get("lightSensor");
        fColorSensor.enableLed(true);

        fTouchSensor = hardwareMap.touchSensor.get("touchSensor");
        fFrontDistance = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "frontDistance");
    }
}
