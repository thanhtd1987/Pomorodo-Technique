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
        void onChangePomorodoProcess();
        void updateCurrentStep(String nextStep);
        void setCurrentTaskName(Task task);
    }

    interface Presenter {
        View getView();
        void ringTheBell();
        Task getNextTask();
        void runPomodoroProcess();
        void doNextStep();
    }
}
