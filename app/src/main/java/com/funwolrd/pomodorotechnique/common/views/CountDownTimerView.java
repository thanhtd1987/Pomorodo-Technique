package com.funwolrd.pomodorotechnique.common.views;

/**
 * Created by ThanhTD on 7/19/2018.
 */
public interface CountDownTimerView {

    void startCountDown();

    void stopCountDown();

    void setTimerInMinute(float time);

    void setCallback(CountDownCallback callback);

    interface CountDownCallback {
        void onStartCountDown();

        void onStopCountDown();

        void onFinishCountDown();

        void onCountDown();
    }
}
