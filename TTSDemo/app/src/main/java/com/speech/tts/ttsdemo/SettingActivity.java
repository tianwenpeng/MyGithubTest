package com.speech.tts.ttsdemo;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;

import com.speech.tts.ttsdemo.contants.Config;
import com.speech.tts.ttsdemo.utils.SettingTextWatcher;

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    public static final String TAG = SettingActivity.class.getSimpleName();

    public static final String PREFER_NAME_IFLY = "com.iflytek.setting";
    public static final String PREFER_NAME_UNISOUND = "com.unisound.setting";
    public static final String PREFER_NAME_AISPEECH = "com.aispeech.setting";
    public static final String PREFER_NAME_VOICERSS = "com.voicerss.setting";
    private EditTextPreference mSpeedPreference;
    private EditTextPreference mPitchPreference;
    private EditTextPreference mVolumePreference;
    private EditTextPreference mAudioCodecPreference;
    private EditTextPreference mAudioFormatPreference;

    private String mTTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        // 指定保存文件名字
        mTTS = getIntent().getStringExtra(Config.TTS_SELECT);
        Log.i(TAG, "onCreate: mTTS:"+mTTS);
        if (mTTS.equals("ifly")){
            Log.i(TAG, "com.iflytek.setting");
            getPreferenceManager().setSharedPreferencesName(PREFER_NAME_IFLY);
            addPreferencesFromResource(R.xml.tts_setting);
            mSpeedPreference = (EditTextPreference)findPreference("speed_preference");
            mSpeedPreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mSpeedPreference,0,100));

            mPitchPreference = (EditTextPreference)findPreference("pitch_preference");
            mPitchPreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mPitchPreference,0,100));

            mVolumePreference = (EditTextPreference)findPreference("volume_preference");
            mVolumePreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mVolumePreference,0,100));

        }else if (mTTS.equals("unisound")){
            Log.i(TAG, "com.unisound.setting");
            getPreferenceManager().setSharedPreferencesName(PREFER_NAME_UNISOUND);
            addPreferencesFromResource(R.xml.tts_setting);
            mSpeedPreference = (EditTextPreference)findPreference("speed_preference");
            mSpeedPreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mSpeedPreference,0,100));

            mPitchPreference = (EditTextPreference)findPreference("pitch_preference");
            mPitchPreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mPitchPreference,0,100));

            mVolumePreference = (EditTextPreference)findPreference("volume_preference");
            mVolumePreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mVolumePreference,0,100));

        }else if (mTTS.equals("aispeech")){
            Log.i(TAG, "com.aispeech.setting");
            getPreferenceManager().setSharedPreferencesName(PREFER_NAME_AISPEECH);
            addPreferencesFromResource(R.xml.tts_setting_aispeech);
            mSpeedPreference = (EditTextPreference) findPreference("speed_preference");
            mSpeedPreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mSpeedPreference,0.5,2.0,true));

            mVolumePreference = (EditTextPreference)findPreference("volume_preference");
            mVolumePreference.getEditText().addTextChangedListener(new SettingTextWatcher(SettingActivity.this,mVolumePreference,0,100));
        }else if (mTTS.equals("voicerss")){
            Log.i(TAG, "com.voicerss.setting");
            getPreferenceManager().setSharedPreferencesName(PREFER_NAME_VOICERSS);
            addPreferencesFromResource(R.xml.tts_setting_voicerss);
        }


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }
}
