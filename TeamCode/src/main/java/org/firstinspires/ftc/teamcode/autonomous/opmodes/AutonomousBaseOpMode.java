/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.autonomous.Autonomous;
import org.firstinspires.ftc.teamcode.autonomous.StartingPosition;
import org.firstinspires.ftc.teamcode.autonomous.controllers.GlyphController;
import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.interfaces.DcMotor;
import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.ILift;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.output.Telemetry;
import org.firstinspires.ftc.teamcode.sensors.ColorSensor;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {
	private final StartingPosition startingPosition;
	
	private final IHardware hardware = this;
	private IArm arm;
	private ILift lift;
	private IWheels wheels;
	private ColorSensor colorSensor;
	
	private final Telemetry telemetry = new Telemetry(super.telemetry);
	private String status;
	
	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;
	}
	
	public void initialize() {
		status = "Initializing";
		
		telemetry.add("Status", () -> { return status; });
    	telemetry.update();
		
		arm = null;
		lift = new LiftMotors(hardware);
		wheels = new MechanamMotors(hardware);
		
		colorSensor = null;
	}

    @Override
    public void runOpMode() {
    	initialize();
    	
    	status = "Waiting for start";
    	telemetry.update();
    	
    	GlyphController glyphController = new GlyphController(arm, lift);
    	MotionController motionController = new MotionController(wheels, startingPosition.getStartingPosition());
    	
    	waitForStart();
    	
    	// TODO: Rework this entire method of handling autonomous control flow
    	Thread autonomous = new Thread(new Autonomous(startingPosition, motionController, glyphController, colorSensor));
    	autonomous.start();
    	
        while (opModeIsActive());
        
        // It remains to be seen whether idle() is actually interruptable. If not, I'll need something else.
        autonomous.interrupt();
    }

	@Override
	public DcMotor getMotor(String name) {
		return hardwareMap.dcMotor.get(name);
	}
}
