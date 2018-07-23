package com.funwolrd.pomodorotechnique.common.views;

/**
 * Created by ThanhTD on 7/19/2018.
 */
public interface CountDownTimerView {

    void startCountDown();

    void stopCountDown();

    void setTimerInSecond(long time);

    void setCallback(CountDownCallback callback);

    void enableWarningOutOfRestTime(boolean isEnable);

    interface CountDownCallback {
        void onStartCountDown();

        void onStopCountDown();

        void onFinishCountDown();

        void onCountDown(String remainTime, int progress);
    }
}
