package com.funwolrd.pomodorotechnique;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.common.views.CircleCountDownTimer;
import com.funwolrd.pomodorotechnique.common.views.CountDownTimerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CountDownTimerView.CountDownCallback {

    private static final int SECOND_IN_MINUTE = 60;
    private final int DELAY_TIME_SECOND = 5;
    private final int TEA_BREAK_TIME_20 = 20 * SECOND_IN_MINUTE;
    private final int TEA_BREAK_TIME_25 = 25 * SECOND_IN_MINUTE;
    private final int TEA_BREAK_TIME_30 = 30 * SECOND_IN_MINUTE;
    private final String SOUND_REST = "sound_rest.mp3";
    private final String SOUND_WORKING = "sound_working.mp3";
    private final String SOUND_DELAY_URL = "sound_delay.mp3";

    private enum ProcessStatus {
        STARTED,
        STOPPED
    }

    private enum Pomodoro {
        WORKING(25),
        SHORT_BREAK(5),
        TEA_BREAK(15);

        private int value;
        private boolean debug = true;

        Pomodoro(int value) {
            if (debug)
                this.value = value;
            else
                this.value = value * SECOND_IN_MINUTE;
        }
    }

    private ProcessStatus mProcessStatus = ProcessStatus.STOPPED;
    private Pomodoro mCurrentStep = Pomodoro.SHORT_BREAK;
    private int mPomodoroLapCount = 0;
    private boolean isDelayForNextStep = false;
    Ringtone mRingtone;

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

        updateView(getString(R.string.text_ready));
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

    private void onChangePomorodoProcess(boolean isStart) {
        if (!isStart) {
            mPomodoroLapCount = 0;
            mCurrentStep = Pomodoro.SHORT_BREAK;
            updateView(getString(R.string.text_ready));
        }
        btnStart.setText(isStart ? getString(R.string.text_stop) : getString(R.string.text_start));
        btnStart.setSelected(isStart);
        btnStart.setTextColor(isStart ? getColor(R.color.colorYellow) : getColor(R.color.colorPrimaryDark));
    }

    /**
     * method to start and stop count down timer
     */
    private void runPomodoroProcess() {
        if (mProcessStatus == ProcessStatus.STOPPED) {
            mProcessStatus = ProcessStatus.STARTED;
            doNextStep();
            mCountDownTimerView.startCountDown();
        } else {
            mProcessStatus = ProcessStatus.STOPPED;
            mCountDownTimerView.stopCountDown();
        }
    }

    /**
     * method to start a timer for one step
     */
    private void doNextStep() {
        Pomodoro nextStep = Pomodoro.WORKING;
        switch (mCurrentStep) {
            case WORKING:
                mCurrentStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK : Pomodoro.SHORT_BREAK;
                etTaskName.setEnabled(false);
                break;
            case SHORT_BREAK:
            case TEA_BREAK:
                mCurrentStep = Pomodoro.WORKING;
                mPomodoroLapCount++;
                nextStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK : Pomodoro.SHORT_BREAK;
                etTaskName.setEnabled(true);
                break;
        }
        updateView(nextStep.name());
        mCountDownTimerView.setTimerInSecond(mCurrentStep.value);
        mCountDownTimerView.enableWarningOutOfRestTime(mCurrentStep != Pomodoro.WORKING);
        mCountDownTimerView.startCountDown();
        ringTheBell();
        isDelayForNextStep = true;
    }

    private void showDelayTime() {
        mCountDownTimerView.setTimerInSecond(DELAY_TIME_SECOND);
        mCountDownTimerView.startCountDown();
        tvNextStep.setText(String.format(getString(R.string.text_next_step), getString(R.string.text_ready)));
        isDelayForNextStep = false;
    }

    @Override
    public void onStartCountDown() {
        onChangePomorodoProcess(true);
    }

    @Override
    public void onStopCountDown() {
        onChangePomorodoProcess(false);
    }

    @Override
    public void onFinishCountDown() {
//        if (isDelayForNextStep)
//            showDelayTime();
//        else
            doNextStep();
    }

    @Override
    public void onCountDown() {

    }

    /**
     * notify start - end of step by sound
     */
    private void ringTheBell() {
//        if(mRingtone == null)
        mRingtone = null;
        {
            String url = "android.resource://" /*+ getPackageName()*/ + "raw/"
                    + (mCurrentStep == Pomodoro.WORKING ? SOUND_WORKING : SOUND_REST);
            Log.d("DEBUG", "ringTheBell: "+url);
            Uri soundPath = Uri.parse(url);
//            Uri notification = RingtoneManager.getDefaultUri(mCurrentStep == Pomodoro.WORKING ?
//                    RingtoneManager.TYPE_NOTIFICATION : RingtoneManager.TYPE_NOTIFICATION);
            mRingtone = RingtoneManager.getRingtone(getApplicationContext(), soundPath);
        }
        try {
            mRingtone.play();
//            mRingtone = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
