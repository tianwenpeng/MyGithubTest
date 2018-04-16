package com.speech.tts.ttsdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speech.tts.ttsdemo.contants.Config;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

public class UnisoundTTSActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = UnisoundTTSActivity.class.getSimpleName();

    private EditText mInputEt;
    private Button mVocierBt, mSettingBt, mStartBt, mCancelBt;

    //发音人 默认"xiaoli"
    private String mVoicer = "xiaoli";

    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;

    private SpeechSynthesizer mTTSPlayer;

    private SharedPreferences mSharedPreferences;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifly_tts);
        initView();
        setText();
        mTTSPlayer = new SpeechSynthesizer(this, Config.appKey, Config.secret);
        //在线合成
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_NET);
        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.unisound_voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.unisound_voicer_cloud_values);

        mSharedPreferences = getSharedPreferences(SettingActivity.PREFER_NAME_UNISOUND, MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
    private void setText(){
        mInputEt.setText(getResources().getText(R.string.unisound_input));
    }

    private void initView() {
        mInputEt = findViewById(R.id.input_et);
        findViewById(R.id.vocier_bt).setOnClickListener(this);
        findViewById(R.id.setting_bt).setOnClickListener(this);
        findViewById(R.id.start_bt).setOnClickListener(this);
        findViewById(R.id.cancel_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (null == mTTSPlayer) {
            showTip("语音合成对象创建失败！");
            return;
        }
        switch (view.getId()) {
            case R.id.vocier_bt:
                String s = mInputEt.getText().toString().trim();
                showPresonSelectDialog(s);
                break;
            case R.id.setting_bt:
                if (mTTSPlayer.getOption(SpeechConstants.TTS_SERVICE_MODE).equals(SpeechConstants.TTS_SERVICE_MODE_NET)) {
                    Intent intent = new Intent(UnisoundTTSActivity.this, SettingActivity.class);
                    intent.putExtra(Config.TTS_SELECT, "unisound");
                    startActivity(intent);
                } else {
                    //showTip("请前往xfyun.cn下载离线合成体验");
                }

                break;
            case R.id.start_bt:
                String ss = mInputEt.getText().toString().trim();
                setOption();
                mTTSPlayer.playText(ss);

                break;
            case R.id.cancel_bt:
                mTTSPlayer.cancel();
                break;
        }
    }

    private int selectedNum = 0;

    /**
     * 发音人选择。
     */
    private void showPresonSelectDialog(final String s) {
        new AlertDialog.Builder(this).setTitle("在线合成发音人选项")
                .setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
                        selectedNum, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                mVoicer = mCloudVoicersValue[which];
                                Log.i(TAG, "which = " + which + " <=> voicer: " + mVoicer);
                                ((EditText) findViewById(R.id.input_et)).setText(s);
                                selectedNum = which;
                                dialog.dismiss();
                            }
                        }).show();
    }

    /**
     * 参数设置
     */

    private void setOption() {
        //设置发音人
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, mVoicer);

        Log.i(TAG, "setOption: mSharedPreferences speed:" + mSharedPreferences.getString("speed_preference", "50"));
        //语速
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, Integer.parseInt(mSharedPreferences.getString("speed_preference", "50")));
        //音调
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_PITCH, Integer.parseInt(mSharedPreferences.getString("pitch_preference", "50")));
        //音量
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, Integer.parseInt(mSharedPreferences.getString("volume_preference", "50")));

        Log.i(TAG, "mVoicer:" + mTTSPlayer.getOption(SpeechConstants.TTS_KEY_VOICE_NAME));
        Log.i(TAG, "Speed:" + mTTSPlayer.getOption(SpeechConstants.TTS_KEY_VOICE_SPEED));
        Log.i(TAG, "Pitch:" + mTTSPlayer.getOption(SpeechConstants.TTS_KEY_VOICE_PITCH));
        Log.i(TAG, "Volume:" + mTTSPlayer.getOption(SpeechConstants.TTS_KEY_VOICE_VOLUME));

        mTTSPlayer.setTTSListener(mSpeechSynthesizerListener);

        //初始化合成引擎
        mTTSPlayer.init("");

    }

    private SpeechSynthesizerListener mSpeechSynthesizerListener = new SpeechSynthesizerListener() {

        @Override
        public void onEvent(int i) {
            switch (i) {
                case SpeechConstants.TTS_EVENT_INIT:
                    // 初始化成功回调
                    Log.i(TAG, "onEvent: 初始化成功回调");
                    break;
                case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                    // 开始合成回调
                    Log.i(TAG, "onEvent: 开始合成回调");
                    break;
                case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                    // 合成结束回调
                    Log.i(TAG, "onEvent: 合成结束回调");
                    break;
                case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                    // 开始缓存回调
                    Log.i(TAG, "onEvent: 开始缓存回调");
                    break;
                case SpeechConstants.TTS_EVENT_BUFFER_READY:
                    // 缓存完毕回调
                    Log.i(TAG, "onEvent: 缓存完毕回调");
                    break;
                case SpeechConstants.TTS_EVENT_PLAYING_START:
                    // 开始播放回调
                    Log.i(TAG, "onEvent: 开始播放回调");
                    break;
                case SpeechConstants.TTS_EVENT_PLAYING_END:
                    // 播放完成回调
                    Log.i(TAG, "onEvent: 播放完成回调");
                    break;
                case SpeechConstants.TTS_EVENT_PAUSE:
                    // 暂停回调
                    Log.i(TAG, "onEvent: 暂停回调");
                    break;
                case SpeechConstants.TTS_EVENT_RESUME:
                    // 恢复回调
                    Log.i(TAG, "onEvent: 恢复回调");
                    break;
                case SpeechConstants.TTS_EVENT_STOP:
                    // 停止回调
                    Log.i(TAG, "onEvent: 停止回调");
                    break;
                case SpeechConstants.TTS_EVENT_RELEASE:
                    // 释放资源回调
                    Log.i(TAG, "onEvent: 释放资源回调");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int i, String s) {
            Log.i(TAG, "onError: i:" + i + ":error:" + s);
            showTip(s);
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
            mTTSPlayer = null;
        }
    }
}
