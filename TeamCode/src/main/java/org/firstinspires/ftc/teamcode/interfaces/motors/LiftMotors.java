package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.ILift;

public class LiftMotors implements ILift {
	protected final IHardware hardware;
    protected final DcMotor liftMotor;

    protected static final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double LIFT_GEAR_REDUCTION = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double LIFT_INCHES_PER_REV = 0.0;// Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;

    public LiftMotors(IHardware hardware) {
    	this.hardware = hardware;
    	liftMotor = hardware.getMotor("lift");
    	liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void setLiftHeight(int glyphHeightMultiplier) {
    	if(glyphHeightMultiplier < 0 || glyphHeightMultiplier > 4)
    		throw new IllegalArgumentException("The lift cannot go through the ground or into the sky! glyphHeight was " + glyphHeightMultiplier);
	    // TODO: Implement this function.
    	// Use hardwareMap.idle() for the idle function in RobotTeleOp.
        int nextPosition = COUNT_PER_GLYPH_HEIGHT * glyphHeightMultiplier;
        liftMotor.setTargetPosition(nextPosition);
        liftMotor.setPower(MAIN_LIFT_SPEED);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
