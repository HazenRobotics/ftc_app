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
    	liftMotor = hardware.getMotor("mainLift");
    	liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    //Moves main mainLift to a glyph height
    public void setLiftHeight(int targetHeight) {
        //throws an error if glyph height is out of range {1,2,3,4}
    	if(targetHeight < 0 || targetHeight > 4)
    		throw new IllegalArgumentException("The mainLift cannot go through the ground or into the sky! glyphHeight was " + targetHeight);
        //calculates the next position by converting target height to number of motor counts
        int nextPosition = COUNT_PER_GLYPH_HEIGHT * targetHeight;
        liftMotor.setTargetPosition(nextPosition);
        //sets motor speed
        liftMotor.setPower(MAIN_LIFT_SPEED);
        //runs to position
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //idle when running to position
        while(liftMotor.isBusy()) {
            hardware.idle();
        }
        //once reached position, set power to 0 and start using encoders again
        liftMotor.setPower(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
