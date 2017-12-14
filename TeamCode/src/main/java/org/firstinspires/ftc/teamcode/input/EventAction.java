package org.firstinspires.ftc.teamcode.input;

public abstract class EventAction extends EventListener implements Runnable {
    public abstract void run();
    public void on(EventType type, IButton button) {
        run();
    }
}
