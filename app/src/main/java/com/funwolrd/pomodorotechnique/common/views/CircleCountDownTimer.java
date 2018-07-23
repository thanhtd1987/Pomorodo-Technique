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
    private final String TAG = CircleCountDownTimer.class.getName();

    private final int COUNT_DOWN_INTERVAL = 10;
    private final int WARNING_TIME_IN_SECOND = 10;

    private ProgressBar progressBarCircle;
    private TextView tvTime;
    private CountDownTimer mCountDownTimer;
    private long mTimeCountInMilliSeconds;
    private CountDownCallback mCallback;
    private boolean isWarningOutOfRestTime = true;

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
    public void enableWarningOutOfRestTime(boolean isEnable) {
        isWarningOutOfRestTime = isEnable;
    }

    @Override
    public void startCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mCountDownTimer = new CountDownTimer(mTimeCountInMilliSeconds, COUNT_DOWN_INTERVAL) {
            boolean blink = false;
            int countTimeForBlink = 0;
            String lastTime = "";
            @Override
            public void onTick(long millisUntilFinished) {
                String time = Utils.msTimeFormatter(millisUntilFinished);
                progressBarCircle.setProgress((int) (millisUntilFinished / COUNT_DOWN_INTERVAL));
                if (!lastTime.equals(time)) {
                    tvTime.setText(time);
                    mCallback.onCountDown(time, 100 - (int) (millisUntilFinished * 100 / mTimeCountInMilliSeconds));
                    lastTime = time;
                }

                //fix bug - not display progress + time at time 0 when COUNT_DOWN_INTERVAL = 1000
                /*long seconds = millisUntilFinished / COUNT_DOWN_INTERVAL;
                if(seconds == 1000 / COUNT_DOWN_INTERVAL) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Log.d(TAG, "onTick: "+ (seconds - 1000 / COUNT_DOWN_INTERVAL));
                            tvTime.setText(Utils.msTimeFormatter(0));
                            progressBarCircle.setProgress(0);
                        }
                    }, COUNT_DOWN_INTERVAL);
                }*/
                if (isWarningOutOfRestTime && Utils.isInWarningTime(millisUntilFinished, WARNING_TIME_IN_SECOND)) {
                    if (countTimeForBlink == 10) {
                        tvTime.setTextColor(getResources().getColor(R.color.warning_color));
                        tvTime.setVisibility(blink ? INVISIBLE : VISIBLE);
                        blink = !blink;
                        countTimeForBlink = 0;
                    } else {
                        countTimeForBlink++;
                    }
                }
            }

            @Override
            public void onFinish() {
                resetTimer();
                mCallback.onFinishCountDown();
            }
        }.start();
        mCallback.onStartCountDown();
//        mCountDownTimer.start();
    }

    @Override
    public void stopCountDown() {
        mCountDownTimer.cancel();
        mCountDownTimer = null;
        resetTimer();
        mCallback.onStopCountDown();
    }

    @Override
    public void setTimerInSecond(long time) {
        setTimerValues(time);
        setProgressBarValues();
    }

    private void setTimerValues(long time) {
        mTimeCountInMilliSeconds = time * 1000;
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) mTimeCountInMilliSeconds / COUNT_DOWN_INTERVAL);
        progressBarCircle.setProgress((int) mTimeCountInMilliSeconds / COUNT_DOWN_INTERVAL);
    }

    private void resetTimer () {
        tvTime.setVisibility(VISIBLE);
        tvTime.setTextColor(getResources().getColor(R.color.colorYellow));
        tvTime.setText(Utils.msTimeFormatter(0));
        progressBarCircle.setProgress(0);
    }

}
