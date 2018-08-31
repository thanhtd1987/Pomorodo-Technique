package com.funwolrd.pomodorotechnique.main;

/**
 * Created by ThanhTD on 8/31/2018.
 */
public abstract class PomodoroPhase {
    private long mTimeInSeconds;

    abstract void onStartPhase();
    abstract void onEndPhase();
    void setPhaseTime(long timeInSeconds) {
        mTimeInSeconds = timeInSeconds;
    }
    long getPhaseTimes() {
        return mTimeInSeconds;
    }
}