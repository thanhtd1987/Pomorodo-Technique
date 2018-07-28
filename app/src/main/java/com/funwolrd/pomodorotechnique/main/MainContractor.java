package com.funwolrd.pomodorotechnique.main;

import com.funwolrd.pomodorotechnique.task.Task;

/**
 * Created by ThanhTD on 7/26/2018.
 */
public interface MainContractor {

    interface View {
        Presenter getPresenter();
        void onChangePomorodoProcess();
        void updateCurrentStep(String nextStep);
        void setCurrentTaskName(Task task);
    }

    interface Presenter {
        View getView();
        void ringTheBell();
        void getNextTask();
        void runPomodoroProcess();
        void doNextStep();
    }
}
