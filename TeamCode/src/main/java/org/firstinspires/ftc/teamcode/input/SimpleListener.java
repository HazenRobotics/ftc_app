package org.firstinspires.ftc.teamcode.input;

public abstract class SimpleListener<T extends IButton> extends EventListener<T> {
    public abstract void on(IButton button);
    public void on(EventType type, IButton button) {
            on(button);
    }
}
