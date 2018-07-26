package com.funwolrd.pomodorotechnique.setting;

import android.content.Context;

import com.funwolrd.pomodorotechnique.common.Utils;
import com.funwolrd.pomodorotechnique.common.managers.PreferenceManager;
import com.funwolrd.pomodorotechnique.main.Pomodoro;

import java.util.Set;

/**
 * Created by ThanhTD on 7/26/2018.
 */
public class Setting {
    private final int SECOND_IN_MINUTE = 60;
    private final int TEA_BREAK_TIME_20 = 20;
    private final int TEA_BREAK_TIME_25 = 25;
    private final int TEA_BREAK_TIME_30 = 30;

    boolean isNoSound = false;
    boolean isVibrate = true;
    int teaBreakTime = Pomodoro.TEA_BREAK.value;

    private static Setting sSetting;

    public static Setting getInstance(Context context) {
        if(sSetting == null)
            sSetting = Utils.getGson().fromJson(PreferenceManager.getInstance(context).getSetting(), Setting.class);
        if(sSetting == null)
            sSetting = new Setting();

        return sSetting;
    }

    public boolean isNoSound() {
        return sSetting.isNoSound;
    }

    public void setNoSound(boolean noSound) {
        sSetting.isNoSound = noSound;
    }

    public boolean isVibrate() {
        return sSetting.isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        sSetting.isVibrate = vibrate;
    }

    public int getTeaBreakTime() {
        return sSetting.teaBreakTime;
    }

    public void setTeaBreakTime(int teaBreakTime) {
        sSetting.teaBreakTime = teaBreakTime * SECOND_IN_MINUTE;
    }

    public int getSelectedTeaBreakTime() {
        return sSetting.teaBreakTime / 1000;
    }

    private void updateSetting(Context context) {
        if(sSetting == null)
            sSetting = new Setting();
        PreferenceManager.getInstance(context).saveSetting(Utils.getGson().toJson(sSetting));
    }
}
