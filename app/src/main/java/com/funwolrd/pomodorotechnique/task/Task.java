package com.funwolrd.pomodorotechnique.task;

import android.content.Context;
import android.util.Log;

import com.funwolrd.pomodorotechnique.common.Utils;
import com.funwolrd.pomodorotechnique.common.managers.PreferenceManager;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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

    public static List<Task> getSavedTasks(Context context) {
        Type type = new TypeToken<List<Task>>(){}.getType();
        Log.d("DEBUG", "getSavedTasks: "+PreferenceManager.getInstance(context).getTaskList());
        return Utils.getGson().fromJson(PreferenceManager.getInstance(context).getTaskList(), type);
    }

    public static void saveTaskList(Context context, List<Task> tasks) {
        String json = Utils.getGson().toJson(tasks);
        PreferenceManager.getInstance(context).saveTaskList(json);
    }
}
