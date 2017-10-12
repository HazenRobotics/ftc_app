package org.firstinspires.ftc.teamcode;

/**
 * Created by Robotics on 9/28/2017.
 */

public abstract class Button {
    //Store is the button controlling the toggle was previously active last it was checked
    private boolean pressed = false;

    //Method declared during object creation to control what (gamepad) input determines if the
    //toggle should be triggered
    protected abstract boolean input();

    //Method declared during object creation to control what happens when the toggle is toggled
    protected abstract void turnOn();

    //Method declared during object creation to allow you to specify debug output
    protected abstract void debug();

    //Logic function which when run, activates toggles as necessary based on the input()
    public void logic() {

        //If input() used is not pressed, but now it is, then the user pressed input() since
        //the last iteration of the loop; We should turnOn() or turnOff()
        if (input() && !pressed) {

            //Do button functionality
            turnOn();

            //The pressed state that the input was active since the last iteration of the loop is
            //store (so that the toggle isn't turned on and off every time iteration of the loop)
            pressed = true;
        }
        //Only when the pressed value is reset after letting go of the input() and then the input()
        //is later true/pressed again can the toggle be triggered
        else if (!input() && pressed)
            pressed = false;
    }
}
