package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.ILift;

public class LiftMotors implements ILift {
	
	protected final IHardware hardware;
    protected final DcMotor liftMotor;
    
    public LiftMotors(IHardware hardware) {
    	this.hardware = hardware;
    	liftMotor = hardware.getMotor("lift");
    	liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void setLiftHeight(int glyphHeight) {
    	if(glyphHeight < 0 || glyphHeight > 4)
    		throw new IllegalArgumentException("The lift cannot go through the ground or into the sky! glyphHeight was " + glyphHeight);
	    // TODO: Implement this function.
    	// Use hardwareMap.idle() for the idle function in RobotTeleOp.
    }
}
