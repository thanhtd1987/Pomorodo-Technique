package com.funwolrd.pomodorotechnique.common.managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ThanhTD on 7/24/2018.
 */
public class PreferenceManager {

    private final String SHARE_PREFERENCE_NAME = "share_preference";
    private final String KEY_TASKS = "task_list";

    private static PreferenceManager sPreferenceManager;
    private SharedPreferences mSharedPreferences;

    public static PreferenceManager getInstance(Context context) {
        if(sPreferenceManager == null) {
            sPreferenceManager = new PreferenceManager(context);
        }
        return sPreferenceManager;
    }

    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void saveTaskList(String jsonTasks) {
        mSharedPreferences.edit().putString(KEY_TASKS, jsonTasks).apply();
    }

    public String getTaskList() {
        return mSharedPreferences.getString(KEY_TASKS, "[]");
    }
}
