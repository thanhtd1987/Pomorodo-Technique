package com.funwolrd.pomodorotechnique.main;

/**
 * Created by ThanhTD on 7/26/2018.
 */
public enum Pomodoro {

    WORKING(25),
    SHORT_BREAK(5),
    TEA_BREAK(15);

    public int value;
    private boolean debug = true;

    Pomodoro(int value) {
        if (debug)
            this.value = value;
        else
            this.value = value * 60;
    }
}
