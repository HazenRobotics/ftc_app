package org.firstinspires.ftc.teamcode.controllers;

public class StrafeTurnDriving implements IDriving {

    //Controller controller1;
    //Controller controller2;
    MecanumWheels wheels;

    public StrafeTurnDriving(MecanumWheels wheels/*, Controller controller1, Controller controller2*/) {
        //this.controller1 = controller1;
        //this.controller2 = controller2;
        this.wheels = wheels;
    }

    @Override
    public void updateMotion() {
        /* double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1

        //Algorithum for calculating the power that is set to the wheels
        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower = (y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;*/

        MecanumWheels.Coefficients wheelPowers = new MecanumWheels.Coefficients()



    }

    @Override
    public void stopMotion() {

    }
}
