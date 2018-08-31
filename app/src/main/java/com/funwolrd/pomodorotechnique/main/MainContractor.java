package com.funwolrd.pomodorotechnique.main;

import android.content.Context;

import com.funwolrd.pomodorotechnique.task.Task;

/**
 * Created by ThanhTD on 7/26/2018.
 */
public interface MainContractor {

    interface View {
        Presenter getPresenter();
        Context getContext();
        void onStartProcess();
        void onStopProcess();
        void updateStepInfo(String nextStep);
        void updateTaskName(Task task);
    }

    interface Presenter {
        View getView();
        void ringTheBell();
        Task getNextTask(String currentTask);
        void runPomodoroProcess();
        void doNextStep();
        void destroyServices();
    }
}
