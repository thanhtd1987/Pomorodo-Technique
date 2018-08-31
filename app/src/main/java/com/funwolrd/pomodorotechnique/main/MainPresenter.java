package com.funwolrd.pomodorotechnique.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.funwolrd.pomodorotechnique.R;
import com.funwolrd.pomodorotechnique.notification.NotificationController;
import com.funwolrd.pomodorotechnique.notification.NotificationService;
import com.funwolrd.pomodorotechnique.notification.PomorodoReceiver;
import com.funwolrd.pomodorotechnique.setting.Setting;
import com.funwolrd.pomodorotechnique.task.Task;

import java.util.List;


/**
 * Created by ThanhTD on 7/29/2018.
 */
public class MainPresenter implements MainContractor.Presenter {
    private final int NOTIFICATION_ID = 111;
    Setting mSetting;
    MainContractor.View mView;
    Pomodoro mCurrentStep;
    private int mPomodoroLapCount = 0;

    private NotificationController mNotificationController;
    BroadcastReceiver receiver;

    public MainPresenter(MainContractor.View view) {
        mView = view;
        mSetting = Setting.getInstance(getView().getContext());

        initBroadcastReceiver();
        initNotificationController();
    }

    @Override
    public MainContractor.View getView() {
        return mView;
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(PomorodoReceiver.ACTION_STOP);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case PomorodoReceiver.ACTION_STOP:
                        onReceiveStopAction();
                        break;
                    case PomorodoReceiver.ACTION_START:
                        break;
                }
            }
        };

        getView().getContext().registerReceiver(receiver, filter);
    }

    private void initNotificationController() {
        mNotificationController = new NotificationController(getView().getContext(), NOTIFICATION_ID, etTaskName.getText().toString());
    }

    private void onReceiveStopAction() {
        getView().onStopProcess();
        mNotificationController.cancelNotification();
    }

    @Override
    public void ringTheBell() {
        if (!mSetting.isNoSound()) {
            MediaPlayer mp = MediaPlayer.create(getView().getContext(), mCurrentStep == Pomodoro.WORKING ? R.raw.sound_working : R.raw.sound_rest);
            mp.setOnCompletionListener(mediaPlayer -> mp.release());
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.start();
        }
    }

    @Override
    public Task getNextTask(String currentTask) {
        List<Task> tasks = Task.getSavedTasks(getView().getContext());
        for (Task task : tasks) {
            if (task.isOnDoing()) {
                if (currentTask.equals(getView().getContext().getString(R.string.text_no_task)))
                    return task;
                task.setDone(true);
                task.setOnDoing(false);
                break;
            }
        }
        for (Task task : tasks) {
            if (!task.isDone()) {
                task.setOnDoing(true);
                Task.saveTaskList(getView().getContext(), tasks);
                return task;
            }
        }
        Task.saveTaskList(getView().getContext(), tasks);
        return null;
    }

    @Override
    public void runPomodoroProcess() {
        if (mProcessStatus == PomorodoProcess.Status.STOPPED) {
            mProcessStatus = PomorodoProcess.Status.STARTED;
            doNextStep();
            mCountDownTimerView.startCountDown();
            mNotificationController.sendNotification();
            getView().getContext().startService(new Intent(getView().getContext(), NotificationService.class).setAction(PomorodoReceiver.ACTION_START));
        } else {
            mProcessStatus = PomorodoProcess.Status.STOPPED;
            mCountDownTimerView.stopCountDown();
            mNotificationController.cancelNotification();
        }
    }

    @Override
    public void doNextStep() {
        Pomodoro nextStep = Pomodoro.WORKING;
        switch (mCurrentStep) {
            case WORKING:
                mCurrentStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.LONG_BREAK : Pomodoro.BREAK;
                etTaskName.setEnabled(false);
                break;
            case BREAK:
            case LONG_BREAK:
                mCurrentStep = Pomodoro.WORKING;
                mPomodoroLapCount++;
                nextStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.LONG_BREAK : Pomodoro.BREAK;
                etTaskName.setEnabled(true);
                break;
        }
        updateCurrentStep(nextStep.name());
        mNotificationController.enableOnceTimeFunctions(true);
        mCountDownTimerView.setTimerInSecond(mCurrentStep.value);
        mCountDownTimerView.enableWarningOutOfRestTime(mCurrentStep != WORKING);
        mCountDownTimerView.startCountDown();
        ringTheBell();
    }

    @Override
    public void destroyServices() {
        mNotificationController.cancelNotification();
        if (receiver != null)
            getView().getContext().unregisterReceiver(receiver);
        getView().getContext().stopService(new Intent(getView().getContext(), NotificationService.class));
    }
}
