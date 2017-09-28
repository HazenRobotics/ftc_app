package org.firstinspires.ftc.teamcode;

/**
 * Created by Robotics on 9/28/2017.
 */

public abstract class Toggle {
    //Store is the button controlling the toggle was previously active last it was checked
    private boolean pressed = false;

    //Store is the toggle is currently on or off
    private boolean on = false;

    //Method declared during object creation to control what (gamepad) input determines if the
    //toggle should be triggered
    protected abstract boolean input();

    //Method declared during object creation to control what happens when the toggle is toggled on
    protected abstract void turnOn();

    //Method declared during object creation to control what happens when the toggle is toggled off
    protected abstract void turnOff();

    //Method declared during object creation to allow you to specify debug output
    protected abstract void debug();

    //Logic function which when run, activates toggles as necessary based on the input()
    public void logic() {

        //If input() used is not pressed, but now it is, then the user pressed input() since
        //the last iteration of the loop; We should turnOn() or turnOff()
        if (input() && !pressed) {

            //The toggle is turnOn() if it was previously off
            if (on)
                turnOff();

                //The toggle is turnOff() if it was previously wasn't off
            else
                turnOn();

            //The current state is flipped to reflect the toggle
            on = !on;

            //The pressed state that the input was active since the last iteration of the loop is
            //store (so that the toggle isn't turned on and off every time iteration of the loop)
            pressed = true;
        }
        //Only when the pressed value is reset after letting go of the input() and then the input()
        //is later true/pressed again can the toggle be triggered
        else if (!input() && pressed)
            pressed = false;
    }

    public boolean isOn() {
        return on;
    }
}
