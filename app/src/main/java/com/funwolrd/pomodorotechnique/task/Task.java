package com.funwolrd.pomodorotechnique.task;

/**
 * Created by ThanhTD on 7/22/2018.
 */
public class Task {
    private String taskName;
    private boolean isDone = false;
    private boolean onDoing = false;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isOnDoing() {
        return onDoing;
    }

    public void setOnDoing(boolean onDoing) {
        this.onDoing = onDoing;
    }

    public String getTaskName() {
        return taskName;
    }
}
