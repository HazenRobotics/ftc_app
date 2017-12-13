package org.firstinspires.ftc.teamcode.input;

public interface AEventListener<T extends ButtonEvent> {
    public void on(T event);
}
