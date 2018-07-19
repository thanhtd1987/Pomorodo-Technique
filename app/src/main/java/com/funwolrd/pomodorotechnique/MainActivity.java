package com.funwolrd.pomodorotechnique;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.common.views.CircleCountDownTimer;
import com.funwolrd.pomodorotechnique.common.views.CountDownTimerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CountDownTimerView.CountDownCallback{

    private final int DELAY_TIME_SECOND = 5;
    private final int TEA_BREAK_TIME_20 = 20;
    private final int TEA_BREAK_TIME_25 = 25;
    private final int TEA_BREAK_TIME_30 = 30;

    private enum ProcessStatus {
        STARTED,
        STOPPED
    }

    private enum Pomodoro {
        WORKING(1),
        SHORT_BREAK(1),
        TEA_BREAK(1);

        private int value;

        Pomodoro(int value) {
            this.value = value;
        }
    }

    private ProcessStatus mProcessStatus = ProcessStatus.STOPPED;
    private Pomodoro mCurrentStep = Pomodoro.SHORT_BREAK;
    private int mPomodoroLapCount = 0;
    private boolean isDelayForNextStep = false;

    private EditText etTaskName;
    private ImageView ivNextTask;
    private ImageView ivTaskList;
    private TextView btnStart;
    private TextView tvCount, tvNextStep;
    private CountDownTimerView mCountDownTimerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViews();
        initListeners();
    }

    private void initViews() {
        mCountDownTimerView = (CircleCountDownTimer) findViewById(R.id.circle_count_down_timer);
        etTaskName = findViewById(R.id.et_task_name);
        ivNextTask = findViewById(R.id.iv_next_task);
        ivTaskList = findViewById(R.id.iv_task_list);
        btnStart = findViewById(R.id.btn_start);
        tvCount = findViewById(R.id.tv_count);
        tvNextStep = findViewById(R.id.tv_next_step);

        updateView(mCurrentStep.name());
    }

    private void initListeners() {
        mCountDownTimerView.setCallback(this);
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

    private void updateView(String nextStep) {
        tvCount.setText(String.format(getString(R.string.text_working_time), mPomodoroLapCount));
        tvNextStep.setText(String.format(getString(R.string.text_next_step), nextStep));
    }

    private void resetView() {
        mPomodoroLapCount = 0;
        mCurrentStep = Pomodoro.SHORT_BREAK;
        updateView(Pomodoro.WORKING.name());
    }

    /**
     * method to start and stop count down timer
     */
    private void runPomodoroProcess() {
        if (mProcessStatus == ProcessStatus.STOPPED) {
            mProcessStatus = ProcessStatus.STARTED;
            doNextStep();
            mCountDownTimerView.startCountDown();
            btnStart.setText("STOP");
        } else {
            mProcessStatus = ProcessStatus.STOPPED;
            mCountDownTimerView.stopCountDown();
            etTaskName.setEnabled(true);
            btnStart.setText("START");
            resetView();
        }
    }

    /**
     * method to start a timer for one step
     */
    private void doNextStep () {
        Pomodoro nextStep = Pomodoro.WORKING;
        switch (mCurrentStep) {
            case WORKING:
                mCurrentStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK: Pomodoro.SHORT_BREAK;
                etTaskName.setEnabled(false);
                break;
            case SHORT_BREAK:
            case TEA_BREAK:
                mCurrentStep = Pomodoro.WORKING;
                mPomodoroLapCount++;
                nextStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK: Pomodoro.SHORT_BREAK;
                etTaskName.setEnabled(true);
                break;
        }
        updateView(nextStep.name());
        mCountDownTimerView.setTimerInMinute(mCurrentStep.value);
        mCountDownTimerView.startCountDown();
    }


    @Override
    public void onStartCountDown() {
        ringTheBell();
    }

    @Override
    public void onStopCountDown() {
        resetView();
    }

    @Override
    public void onFinishCountDown() {
        ringTheBell();
        doNextStep();
    }

    @Override
    public void onCountDown() {

    }

    /**
     * method to initialize the values for count down timer
     */
//    private void setTimerValues() {
//        // assigning values after converting to milliseconds
////        if(isDelayForNextStep)
////            timeCountInMilliSeconds = DELAY_TIME_SECOND * 60 * 1000;
////        else
//            timeCountInMilliSeconds = mCurrentStep.value * 60 * 1000;
//    }

    /**
     * notify start - end of step by sound
     */
    private void ringTheBell () {
        try {
            Uri notification = RingtoneManager.getDefaultUri(mCurrentStep == Pomodoro.WORKING ?
                    RingtoneManager.TYPE_NOTIFICATION: RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
