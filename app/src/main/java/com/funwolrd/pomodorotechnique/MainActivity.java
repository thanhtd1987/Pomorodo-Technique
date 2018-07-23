package com.funwolrd.pomodorotechnique;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
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
    private final int NOTIFICATION_ID = 111;

    //setting
    private boolean isNoSound = true;

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

    private EditText etTaskName;
    private ImageView ivNextTask;
    private ImageView ivTaskList;
    private TextView btnStart;
    private TextView tvCount, tvNextStep;
    private CountDownTimerView mCountDownTimerView;

    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;


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
        btnStart.setTextColor(isStart ? getResources().getColor(R.color.colorYellow) : getResources().getColor(R.color.colorPrimaryDark));
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
        callNotification();
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
        doNextStep();
    }

    @Override
    public void onCountDown(String remainTime, int progress) {
        updateTimerProgress(remainTime, progress);
    }

    /**
     * notify start - end of step by sound
     */
    private void ringTheBell() {
        if (!isNoSound) {
            MediaPlayer mp = MediaPlayer.create(this, mCurrentStep == Pomodoro.WORKING ? R.raw.sound_working : R.raw.sound_rest);
            mp.setOnCompletionListener(mediaPlayer -> mp.release());
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.start();
        }
    }


    private void callNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, "channel");
        builder.setContentTitle(mCurrentStep.name())
                .setContentText("progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setProgress(100, 0, false);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void updateTimerProgress(String remainTime, int progress) {
        builder.setProgress(100, progress, false);
        String[] temp = remainTime.split(":");
        builder.setContentText("remain: " + temp[0] + "m" + temp[1] + "s " + progress + "%");
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
