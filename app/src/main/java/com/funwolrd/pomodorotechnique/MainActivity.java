package com.funwolrd.pomodorotechnique;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.common.views.CircleCountDownTimer;
import com.funwolrd.pomodorotechnique.common.views.CountDownTimerView;
import com.funwolrd.pomodorotechnique.notification.NotificationController;
import com.funwolrd.pomodorotechnique.notification.NotificationService;
import com.funwolrd.pomodorotechnique.notification.PomorodoReceiver;
import com.funwolrd.pomodorotechnique.task.Task;
import com.funwolrd.pomodorotechnique.task.TaskListDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CountDownTimerView.CountDownCallback {

    private static final int SECOND_IN_MINUTE = 60;
    private final int DELAY_TIME_SECOND = 5;
    private final int TEA_BREAK_TIME_20 = 20 * SECOND_IN_MINUTE;
    private final int TEA_BREAK_TIME_25 = 25 * SECOND_IN_MINUTE;
    private final int TEA_BREAK_TIME_30 = 30 * SECOND_IN_MINUTE;
    private final int NOTIFICATION_ID = 111;


    //TODO : build function for settings: sound, vibrate, tea break's time
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
        private boolean debug = false;

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

    private NotificationController mNotificationController;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViews();
        initListeners();
        initBroadcastReceiver();
        initNotificationController();
    }

    private void initViews() {
        mCountDownTimerView = (CircleCountDownTimer) findViewById(R.id.circle_count_down_timer);
        etTaskName = findViewById(R.id.et_task_name);
        ivNextTask = findViewById(R.id.iv_next_task);
        ivTaskList = findViewById(R.id.iv_task_list);
        btnStart = findViewById(R.id.btn_start);
        tvCount = findViewById(R.id.tv_count);
        tvNextStep = findViewById(R.id.tv_next_step);

        updateCurrentStep(getString(R.string.text_ready));
    }

    private void initListeners() {
        mCountDownTimerView.setCallback(this);
        ivNextTask.setOnClickListener(this);
        ivTaskList.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(PomorodoReceiver.ACTION_STOP);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case PomorodoReceiver.ACTION_STOP:
                        processReceive();
                        break;
                    case PomorodoReceiver.ACTION_START:
                        break;
                }
            }
        };

        registerReceiver(receiver, filter);
    }

    private void initNotificationController() {
        mNotificationController = new NotificationController(this, NOTIFICATION_ID, etTaskName.getText().toString());
    }

    private void processReceive() {
        mCountDownTimerView.stopCountDown();
        mNotificationController.cancelNotification();
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
                setCurrentTaskName(getNextTask());
                break;
            case R.id.iv_task_list:
                new TaskListDialog(this)
                        .setListener(task -> setCurrentTaskName(task))
                        .show();
                break;
            case R.id.btn_start:
                runPomodoroProcess();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("DEUBG", "onResume: vào");
    }

    @Override
    protected void onDestroy() {
        mNotificationController.cancelNotification();
        mCountDownTimerView.stopCountDown();
        if (receiver != null)
            unregisterReceiver(receiver);
        stopService(new Intent(this, NotificationService.class));
        super.onDestroy();
    }

    private void setCurrentTaskName(Task task) {
        if (task == null) {
            etTaskName.setText(R.string.text_no_task);
        } else {
            etTaskName.setText(task.getTaskName());
        }
    }

    private void updateCurrentStep(String nextStep) {
        tvCount.setText(String.format(getString(R.string.text_working_time), mPomodoroLapCount));
        tvNextStep.setText(String.format(getString(R.string.text_next_step), nextStep));
    }

    private void onChangePomorodoProcess(boolean isStart) {
        if (!isStart) {
            mPomodoroLapCount = 0;
            mCurrentStep = Pomodoro.SHORT_BREAK;
            updateCurrentStep(getString(R.string.text_ready));
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
            mNotificationController.sendNotification();
            startService(new Intent(this, NotificationService.class).setAction(PomorodoReceiver.ACTION_START));
        } else {
            mProcessStatus = ProcessStatus.STOPPED;
            mCountDownTimerView.stopCountDown();
            mNotificationController.cancelNotification();
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
        updateCurrentStep(nextStep.name());
        mCountDownTimerView.setTimerInSecond(mCurrentStep.value);
        mCountDownTimerView.enableWarningOutOfRestTime(mCurrentStep != Pomodoro.WORKING);
        mCountDownTimerView.startCountDown();
        ringTheBell();
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
        String title = mCurrentStep == Pomodoro.WORKING ? etTaskName.getText().toString() : "Break time!";
        mNotificationController.updateTimerProgress(title, remainTime, progress);
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

    private Task getNextTask() {
        List<Task> tasks = Task.getSavedTasks(this);
        for (Task task : tasks) {
            if (task.isOnDoing()) {
                if (etTaskName.getText().toString().equals(getString(R.string.text_no_task)))
                    return task;
                task.setDone(true);
                task.setOnDoing(false);
                break;
            }
        }
        for (Task task : tasks) {
            if (!task.isDone()) {
                task.setOnDoing(true);
                Task.saveTaskList(this, tasks);
                return task;
            }
        }
        Task.saveTaskList(this, tasks);
        return null;
    }

    //TODO : separate Push notification to other file


}
