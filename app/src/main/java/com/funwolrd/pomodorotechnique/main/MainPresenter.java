//package com.funwolrd.pomodorotechnique.main;
//
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//
//import com.funwolrd.pomodorotechnique.R;
//import com.funwolrd.pomodorotechnique.notification.NotificationService;
//import com.funwolrd.pomodorotechnique.notification.PomorodoReceiver;
//import com.funwolrd.pomodorotechnique.task.Task;
//
//import java.util.List;
//
//import static com.funwolrd.pomodorotechnique.main.Pomodoro.WORKING;
//
///**
// * Created by ThanhTD on 7/29/2018.
// */
//public class MainPresenter implements MainContractor.Presenter {
//
//
//
//    @Override
//    public MainContractor.View getView() {
//        return null;
//    }
//
//    @Override
//    public void ringTheBell() {
//        if (!isNoSound) {
//            MediaPlayer mp = MediaPlayer.create(this, mCurrentStep == WORKING ? R.raw.sound_working : R.raw.sound_rest);
//            mp.setOnCompletionListener(mediaPlayer -> mp.release());
//            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.start();
//        }
//    }
//
//    @Override
//    public Task getNextTask() {
//        List<Task> tasks = Task.getSavedTasks(getView().getContext());
//        for (Task task : tasks) {
//            if (task.isOnDoing()) {
//                if (etTaskName.getText().toString().equals(getView().getContext().getString(R.string.text_no_task)))
//                    return task;
//                task.setDone(true);
//                task.setOnDoing(false);
//                break;
//            }
//        }
//        for (Task task : tasks) {
//            if (!task.isDone()) {
//                task.setOnDoing(true);
//                Task.saveTaskList(getView().getContext(), tasks);
//                return task;
//            }
//        }
//        Task.saveTaskList(getView().getContext(), tasks);
//        return null;
//    }
//
//    @Override
//    public void runPomodoroProcess() {
//        if (mProcessStatus == MainActivity.ProcessStatus.STOPPED) {
//            mProcessStatus = MainActivity.ProcessStatus.STARTED;
//            doNextStep();
//            mCountDownTimerView.startCountDown();
//            mNotificationController.sendNotification();
//            startService(new Intent(this, NotificationService.class).setAction(PomorodoReceiver.ACTION_START));
//        } else {
//            mProcessStatus = MainActivity.ProcessStatus.STOPPED;
//            mCountDownTimerView.stopCountDown();
//            mNotificationController.cancelNotification();
//        }
//    }
//
//    @Override
//    public void doNextStep() {
//        Pomodoro nextStep = WORKING;
//        switch (mCurrentStep) {
//            case WORKING:
//                mCurrentStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK : Pomodoro.SHORT_BREAK;
//                etTaskName.setEnabled(false);
//                break;
//            case SHORT_BREAK:
//            case TEA_BREAK:
//                mCurrentStep = WORKING;
//                mPomodoroLapCount++;
//                nextStep = mPomodoroLapCount % 4 == 0 ? Pomodoro.TEA_BREAK : Pomodoro.SHORT_BREAK;
//                etTaskName.setEnabled(true);
//                break;
//        }
//        updateCurrentStep(nextStep.name());
//        mNotificationController.enableOnceTimeFunctions(true);
//        mCountDownTimerView.setTimerInSecond(mCurrentStep.value);
//        mCountDownTimerView.enableWarningOutOfRestTime(mCurrentStep != WORKING);
//        mCountDownTimerView.startCountDown();
//        ringTheBell();
//    }
//}
