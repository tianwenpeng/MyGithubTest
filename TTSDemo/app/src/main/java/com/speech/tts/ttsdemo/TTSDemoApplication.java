package com.speech.tts.ttsdemo;

import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by apple on 18/4/2.
 */

public class TTSDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        // Setting.setShowLog(false);

    }
}
