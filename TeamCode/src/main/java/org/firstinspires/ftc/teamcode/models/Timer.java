package org.firstinspires.ftc.teamcode.models;

/**
 * Timer is a condition type which is true when a certain wait time has passed and {@link #endTime} has been reached
 */
public class Timer extends Condition {

    protected long startTime;
    protected long endTime;


    /**
     * Creates a Timer Condition with the given wait length in milliseconds
     * @param waitTimeMS The amount of time to pass in millisecond before the timer {@link #isDone()}/{@link #isTrue()}
     */
    public Timer(long waitTimeMS) {
        startTime = System.currentTimeMillis();
        endTime = startTime + waitTimeMS;
    }

    /**
     * Creates a Timer Condition with the given wait length in milliseconds
     * @param waitTimeMS The amount of time to pass in millisecond before the timer {@link #isDone()}/{@link #isTrue()}
     */
    public Timer(int waitTimeMS) {
        this((long) waitTimeMS);
    }

    /**
     * Creates a Timer Condition with the given wait length in second
     * @param waitTime The amount of time to pass in seconds before the timer {@link #isDone()}/{@link #isTrue()}
     */
    public Timer(float waitTime) {
        this((long) (waitTime * 1000));
    }


    /**
     * Determines if enough time has elapsed since the creation of the timer, and {@link #endTime} has been reached
     * @return If the specified wait time has passed
     */
    @Override
    public boolean isTrue() {
        return isDone();
    }

    /**
     * Determines if enough time has elapsed since the creation of the timer, and {@link #endTime} has been reached
     * @return If the specified wait time has passed
     */
    public boolean isDone() {
        return System.currentTimeMillis() > endTime;
    }

    /**
     * Calculates the amount of time that has elapsed since the creation of the time in milliseconds
     * @return Time elapsed in milliseconds
     */
    public long getTimeElapsedMS() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Calculates the amount of time that has elapsed since the creation of the time in seconds
     * @return Time elapsed in seconds
     */
    public float getTimeElapsed() {
        return getTimeElapsedMS() / 1000f;
    }

    /**
     * Calculates the duration of the timer in milliseconds
     * @return The duration of the timer in milliseconds
     */
    public long getDurationMS() {
        return endTime - startTime;
    }

    /**
     * Calculates the duration of the timer in seconds
     * @return The duration of the timer in seconds
     */
    public float getDuration() {
        return getDurationMS() / 1000f;
    }
}
