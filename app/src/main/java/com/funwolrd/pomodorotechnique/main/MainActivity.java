package com.funwolrd.pomodorotechnique.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.funwolrd.pomodorotechnique.R;
import com.funwolrd.pomodorotechnique.common.views.CircleCountDownTimer;
import com.funwolrd.pomodorotechnique.common.views.CountDownTimerView;
import com.funwolrd.pomodorotechnique.notification.NotificationController;
import com.funwolrd.pomodorotechnique.notification.NotificationService;
import com.funwolrd.pomodorotechnique.notification.PomorodoReceiver;
import com.funwolrd.pomodorotechnique.task.Task;
import com.funwolrd.pomodorotechnique.task.TaskListDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CountDownTimerView.CountDownCallback {

    private final int NOTIFICATION_ID = 111;

    //TODO : add UI for quick setting

    //TODO : add option menu for action bar

    //TODO : build function for settings: sound, vibrate, tea break's time ( quick / menu setting)
    //setting
    private boolean isNoSound = !true;

    private enum ProcessStatus {
        STARTED,
        STOPPED
    }

    private ProcessStatus mProcessStatus = ProcessStatus.STOPPED;
    private Pomodoro mCurrentStep = Pomodoro.SHORT_BREAK;
    private int mPomodoroLapCount = 0;
    private boolean doubleBackToExit = false;

    private EditText etTaskName;
    private ImageView ivNextTask;
    private ImageView ivTaskList;
    private TextView btnStart;
    private TextView tvCount, tvNextStep;
    private CountDownTimerView mCountDownTimerView;
    private ImageView mSoundSetting;

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
        mSoundSetting = findViewById(R.id.iv_sound_setting);

        mSoundSetting.setSelected(!isNoSound);

        updateCurrentStep(getString(R.string.text_ready));
    }

    private void initListeners() {
        mCountDownTimerView.setCallback(this);
        ivNextTask.setOnClickListener(this);
        ivTaskList.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        mSoundSetting.setOnClickListener(this);
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
            case R.id.iv_sound_setting:
                isNoSound = !isNoSound;
                mSoundSetting.setSelected(!isNoSound);
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed();
            return;
        }
        doubleBackToExit = true;
        Toast.makeText(this, "Double press back to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExit = false, 2000);
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
        btnStart.setTextColor(isStart ? getResources().getColor(R.color.colorYellow)
                : getResources().getColor(R.color.colorPrimaryDark));
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
        mNotificationController.enableOnceTimeFunctions(true);
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
        mNotificationController.enableOnceTimeFunctions(false);
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
