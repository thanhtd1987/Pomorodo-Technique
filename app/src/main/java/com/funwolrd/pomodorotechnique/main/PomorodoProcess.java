package com.funwolrd.pomodorotechnique.main;

/**
 * Created by ThanhTD on 8/29/2018.
 */
public class PomorodoProcess {

    public enum ProcessStatus {
        STARTED,
        STOPPED
    }

    void startProcess() {

    }

    void stopProcess() {

    }

    interface Callback {
        void onStatusChange(ProcessStatus status);
        void onStartWorking();
        void onBreak();
        void onLongBreak();
    }
}
