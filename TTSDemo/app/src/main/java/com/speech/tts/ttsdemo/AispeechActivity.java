package com.speech.tts.ttsdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.speech.tts.ttsdemo.contants.Config;
import com.unisound.client.SpeechSynthesizer;

public class AispeechActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = AispeechActivity.class.getSimpleName();

    private EditText mInputEt;
    private Button mVoicerBt, mSettingBt, mStartBt, mCancelBt;

    private AICloudTTSEngine mEngine;
    //发音人 默认"zhilingf"
    private String mVoicer = "zhilingf";

    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;


    private SharedPreferences mSharedPreferences;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifly_tts);
        initView();
        setText();
        //初始化云端合成引擎
        mEngine = AICloudTTSEngine.createInstance();
        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.aispeech_voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.aispeech_voicer_cloud_values);

        mSharedPreferences = getSharedPreferences(SettingActivity.PREFER_NAME_AISPEECH, MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
    private void setText(){
        mInputEt.setText(getResources().getText(R.string.aispeech_input));
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
        if (null == mEngine) {
            showTip("初始化语音合成引擎失败");
            return;
        }
        switch (view.getId()) {
            case R.id.vocier_bt:
                String s = mInputEt.getText().toString().trim();
                showPresonSelectDialog(s);
                break;
            case R.id.setting_bt:
                if (mEngine != null) {
                    Intent intent = new Intent(AispeechActivity.this, SettingActivity.class);
                    intent.putExtra(Config.TTS_SELECT, "aispeech");
                    startActivity(intent);
                } else {
                    showTip("初始化语音合成引擎失败");
                }

                break;
            case R.id.start_bt:
                String ss = mInputEt.getText().toString().trim();
                setParam();
                mEngine.speak(ss);
                break;
            case R.id.cancel_bt:
                mEngine.stop();
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

    private void setParam() {
        mEngine.setTextType("text");//设置合成的文本类型,默认为text类型
        mEngine.setServer("http://tts.dui.ai/runtime/v2/synthesize");//访问云端合成服务器地址，默认为该地址
        mEngine.setStreamType(AudioManager.STREAM_MUSIC);//设置合成音播放的音频流,默认为音乐流
        mEngine.setUseCache(true);//设置是否使用本地缓存,默认缓存为true。最大缓存20条合成音频
        mEngine.setSpeaker(mVoicer);
        mEngine.setSpeed(mSharedPreferences.getString("speed_preference", "1.0"));
        mEngine.setVolume(mSharedPreferences.getString("volume_preference", "50"));
        mEngine.init(mAITTSListener);
    }

    private AITTSListener mAITTSListener = new AITTSListener() {
        @Override
        public void onInit(int i) {
            if (i == AIConstant.OPT_SUCCESS) {
                Log.i(TAG, "初始化成功!");
            } else {
                Log.i(TAG, "初始化失败!");
            }
        }

        @Override
        public void onError(String s, AIError aiError) {
            Log.i(TAG, "onError: s:" + s + ";aiError:" + aiError.toString());

        }

        @Override
        public void onReady(String s) {
            Log.i(TAG, "onReady: s:" + s);

        }

        @Override
        public void onCompletion(String s) {
            Log.i(TAG, "onCompletion: s:" + s);
        }

        @Override
        public void onProgress(int i, int i1, boolean b) {
            Log.e(TAG, "onProgress: i:" + i + ";i1:" + i1 + ";b:" + b);
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.stop();
            mEngine.release();
            mEngine = null;
        }
    }
}
