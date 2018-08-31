package com.funwolrd.pomodorotechnique.main;

/**
 * Created by ThanhTD on 7/26/2018.
 */
public enum Pomodoro {

    WORKING(25),
    BREAK(5),
    LONG_BREAK(15);

    public int value;
    private boolean debug = false;

    Pomodoro(int value) {
        if (debug)
            this.value = value;
        else
            this.value = value * 60;
    }
}
