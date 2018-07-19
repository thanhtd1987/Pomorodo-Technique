package com.funwolrd.pomodorotechnique.common.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.R;
import com.funwolrd.pomodorotechnique.common.Utils;

/**
 * Created by ThanhTD on 7/19/2018.
 */
public class CircleCountDownTimer extends RelativeLayout implements CountDownTimerView {

    private ProgressBar progressBarCircle;
    private TextView tvTime;
    private CountDownTimer mCountDownTimer;
    private long mTimeCountInMilliSeconds;
    private CountDownCallback mCallback;

    public CircleCountDownTimer(Context context) {
        super(context);
    }

    public CircleCountDownTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_circle_count_down_timer, this, true);
        progressBarCircle = findViewById(R.id.progress_bar_circle);
        tvTime = findViewById(R.id.tv_time);
    }

    public CircleCountDownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCallback(CountDownCallback callback) {
        mCallback = callback;
    }

    @Override
    public void startCountDown() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(mTimeCountInMilliSeconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTime.setText(Utils.msTimeFormatter(millisUntilFinished));
                    progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                    mCallback.onCountDown();
                }

                @Override
                public void onFinish() {
                    tvTime.setText(Utils.msTimeFormatter(0));
                    progressBarCircle.setProgress(0);
                    mCallback.onFinishCountDown();
                }
            };
            mCallback.onStartCountDown();
        }
        mCountDownTimer.start();
    }

    @Override
    public void stopCountDown() {
        mCountDownTimer.cancel();
        mCountDownTimer = null;
        mCallback.onStopCountDown();
    }

    @Override
    public void setTimerInMinute(float time) {
        setTimerValues(time);
        setProgressBarValues();
    }

    private void setTimerValues(float time) {
        mTimeCountInMilliSeconds = (int) time * 60 * 1000;
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) mTimeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) mTimeCountInMilliSeconds / 1000);
    }

}
