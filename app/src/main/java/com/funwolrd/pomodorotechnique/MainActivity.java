package com.funwolrd.pomodorotechnique;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private long timeCountInMilliSeconds = 1 * 60000;
    private final int DELAY_TIME_SECOND = 5;
    private final int TEA_BREAK_TIME_20 = 20;
    private final int TEA_BREAK_TIME_25 = 25;
    private final int TEA_BREAK_TIME_30 = 30;

    private enum ProcessStatus {
        STARTED,
        STOPPED
    }

    private enum Pomodoro {
        WORKING(25),
        SHORT_BREAK(5),
        TEA_BREAK(15);

        private int value;

        Pomodoro(int value) {
            this.value = value;
        }
    }

    private ProcessStatus mProcessStatus = ProcessStatus.STOPPED;
    private Pomodoro mCurrentStep = Pomodoro.WORKING;
    private int mPomodoroLapCount = 0;
    private boolean isDelayForNextStep = false;

    private ProgressBar progressBarCircle;
    private TextView tvTime;
    private EditText etTaskName;
    private ImageView ivNextTask;
    private ImageView ivTaskList;
    private CountDownTimer countDownTimer;
    private TextView btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViews();
        initListeners();
    }

    private void initViews() {
        progressBarCircle = findViewById(R.id.progress_bar_circle);
        tvTime = findViewById(R.id.tv_time);
        etTaskName = findViewById(R.id.et_task_name);
        ivNextTask = findViewById(R.id.iv_next_task);
        ivTaskList = findViewById(R.id.iv_task_list);
        btnStart = findViewById(R.id.btn_start);
    }

    private void initListeners() {
        ivNextTask.setOnClickListener(this);
        ivTaskList.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_next_task:
                //TODO: get next task in task list + assign for tvTaskName
                break;
            case R.id.iv_task_list:
                //TODO: open task list + select Task
                break;
            case R.id.btn_start:
                runPomodoroProcess();
                break;
        }
    }

    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }


    /**
     * method to start and stop count down timer
     */
    private void runPomodoroProcess() {
        if (mProcessStatus == ProcessStatus.STOPPED) {
            setTimerValues();
            setProgressBarValues();


            // changing the timer status to started
            mProcessStatus = ProcessStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            etTaskName.setEnabled(true);
            // changing the timer status to stopped
            mProcessStatus = ProcessStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    /**
     * method to start a timer for one step
     */
    private void doNextStep () {
        switch (mCurrentStep) {
            case WORKING:
                mCurrentStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK: Pomodoro.SHORT_BREAK;
                etTaskName.setEnabled(false);
                break;
            case SHORT_BREAK:
            case TEA_BREAK:
                mCurrentStep = Pomodoro.WORKING;
                etTaskName.setEnabled(true);
                break;
        }
        setTimerValues();
        setProgressBarValues();
    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        // assigning values after converting to milliseconds
//        if(isDelayForNextStep)
//            timeCountInMilliSeconds = DELAY_TIME_SECOND * 60 * 1000;
//        else
            timeCountInMilliSeconds = mCurrentStep.value * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tvTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
//                tvTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                ringTheBell();
                doNextStep();
            }

        };
        countDownTimer.start();
        ringTheBell();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
        countDownTimer = null;
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    /**
     * notify start - end of step by sound
     */
    private void ringTheBell () {
        try {
            Uri notification = RingtoneManager.getDefaultUri(mCurrentStep == Pomodoro.WORKING ? RingtoneManager.TYPE_ALARM: RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
//                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
}
