package org.firstinspires.ftc.teamcode.models;

/**
 * Timer is a condition type which {@link #isTrue()} when a certain wait time has passed and {@link #endTime} has been reached
 */
public class Timer extends Condition {

    protected final long startTime;
    protected final long endTime;


    /**
     * Creates a Timer Condition with the given wait length in milliseconds
     * @param waitTimeMs The amount of time to pass in millisecond before the timer {@link #isTrue()}
     */
    public Timer(long waitTimeMs) {
        startTime = System.currentTimeMillis();
        endTime = startTime + waitTimeMs;
    }

    /**
     * Creates a Timer Condition with the given wait length in milliseconds
     * @param waitTimeMs The amount of time to pass in millisecond before the timer {@link #isTrue()}
     */
    public Timer(int waitTimeMs) {
        this((long) waitTimeMs);
    }

    /**
     * Creates a Timer Condition with the given wait length in seconds
     * @param waitTime The amount of time to pass in seconds before the timer {@link #isTrue()}
     */
    public Timer(float waitTime) {
        this((long) (waitTime * 1000));
    }

    /**
     * Calculates the amount of time that has elapsed since the creation of the time in milliseconds
     * @return Time elapsed in milliseconds
     */
    public long getTimeElapsedMs() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Calculates the amount of time that has elapsed since the creation of the time in seconds
     * @return Time elapsed in seconds
     */
    public float getTimeElapsed() {
        return getTimeElapsedMs() / 1000f;
    }

    /**
     * Calculates the duration of the timer in milliseconds
     * @return The duration of the timer in milliseconds
     */
    public long getDurationMs() {
        return endTime - startTime;
    }

    /**
     * Calculates the duration of the timer in seconds
     * @return The duration of the timer in seconds
     */
    public float getDuration() {
        return getDurationMs() / 1000f;
    }

    /**
     * Determines if enough time has elapsed since the creation of the timer, and {@link #endTime} has been reached
     * @return If the specified wait time has passed
     */
    @Override
    protected boolean condition() {
        return System.currentTimeMillis() >= endTime;
    }
}
