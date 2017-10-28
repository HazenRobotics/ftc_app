package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by davegoldy on 4/8/17.
 */
@Autonomous(name = "Drive Straight", group = "Autonomous")
public class DriveStraight extends LinearOpMode
{
    private DcMotor fRightMotor;
    private DcMotor fLeftMotor;
    private double fStartTime = -1.0;
    private final double fTotalTime = 2.0;

    @Override
    public void runOpMode() throws InterruptedException
    {
        setupHardware();
        waitForStart();
        while (opModeIsActive())
        {
            if (fStartTime <= 0.0)
            {
                fStartTime = getRuntime();
            }
            double runttime = getRuntime() - fStartTime;
            if (runttime < fTotalTime)
            {
                fRightMotor.setPower(0.2);
                fLeftMotor.setPower(0.2);
            }
            else
            {
                fRightMotor.setPower(0.0);
                fLeftMotor.setPower(0.0);
            }
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

        // eg: Set the mainLift motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        fLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        fRightMotor.setDirection(DcMotor.Direction.FORWARD);

    }
}
