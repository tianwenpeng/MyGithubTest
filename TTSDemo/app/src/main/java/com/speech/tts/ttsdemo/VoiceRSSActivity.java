package com.speech.tts.ttsdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speech.tts.ttsdemo.contants.Config;
import com.voicerss.tts.AudioCodec;
import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.Languages;
import com.voicerss.tts.SpeechDataEvent;
import com.voicerss.tts.SpeechDataEventListener;
import com.voicerss.tts.SpeechErrorEvent;
import com.voicerss.tts.SpeechErrorEventListener;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class VoiceRSSActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = VoiceRSSActivity.class.getSimpleName();

    private EditText mInputEt;
    private Button mLanguageBt, mSettingBt, mStartBt, mCancelBt;

    private Toast mToast;
    private String[] mCloudLanguagesEntries;
    private String[] mCloudLanguagesValue;
    private String[] mCloudLanguages;
    private SharedPreferences mSharedPreferences;

    //默认语言
    private String mLanguage = "zh-cn";
    //
    private String mLanguageText = "";

    //语音合成
    private VoiceProvider mTTs;
    private VoiceParameters mParams;

    private MediaPlayer mMediaPlayer;

    private int mSpeed;//语速
    private String mAudioCodec;//语音编解码格式
    private String mAudioFormat;//语音格式


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_rss);
        initView();
        setText();
        mTTs = new VoiceProvider(Config.API_KEY);
        mCloudLanguagesEntries = getResources().getStringArray(R.array.voice_rss_language_entries);
        mCloudLanguagesValue = getResources().getStringArray(R.array.voice_rss_language_values);
        mCloudLanguages = getResources().getStringArray(R.array.voice_rss_languages);
        mSharedPreferences = getSharedPreferences(SettingActivity.PREFER_NAME_VOICERSS, MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View view) {
        if (mTTs == null){
            showTip("语音合成对象创建失败");
            return;
        }
        switch (view.getId()) {
            case R.id.language_bt:
                String s = mInputEt.getText().toString().trim();
                showPresonSelectDialog(s);
                //mInputEt.setText(mLanguageText);
                break;
            case R.id.rss_setting_bt:
                Intent intent = new Intent(VoiceRSSActivity.this, SettingActivity.class);
                intent.putExtra(Config.TTS_SELECT, "voicerss");
                startActivity(intent);
                break;
            case R.id.rss_start_bt:
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                mStartBt.setText("合成中");
                mStartBt.setEnabled(false);
                String ss = mInputEt.getText().toString().trim();
                getData();
                setParam(ss);
                break;
            case R.id.rss_cancel_bt:
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTs != null) {
            mTTs = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    private void setText() {
        mInputEt.setText(getResources().getText(R.string.voicerss_input));
    }

    private void initView() {
        mInputEt = findViewById(R.id.rss_input_et);
        mLanguageBt = findViewById(R.id.language_bt);
        mSettingBt = findViewById(R.id.rss_setting_bt);
        mStartBt = findViewById(R.id.rss_start_bt);
        mCancelBt = findViewById(R.id.rss_cancel_bt);
        mLanguageBt.setOnClickListener(this);
        mSettingBt.setOnClickListener(this);
        mStartBt.setOnClickListener(this);
        mCancelBt.setOnClickListener(this);
    }

    private int selectedNum = 1;

    /**
     * 语言选择。
     */
    private void showPresonSelectDialog(final String s) {
        new AlertDialog.Builder(this).setTitle("在线合成语言选项")
                .setSingleChoiceItems(mCloudLanguagesEntries, // 单选框有几项,各是什么名字
                        selectedNum, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                mLanguage = mCloudLanguagesValue[which];
                                mLanguageText = mCloudLanguages[which];
                                Log.i(TAG, "which = " + which + " <=> Language: " + selectedNum);
                                //((EditText) findViewById(R.id.rss_input_et)).setText(mLanguageText);
                                mInputEt.setText(mLanguageText);
                                selectedNum = which;
                                dialog.dismiss();
                            }
                        }).show();
    }

    /**
     * 语速、编解码格式等设置数据获取
     */
    private void getData() {
        mSpeed = Integer.parseInt(mSharedPreferences.getString("speed_preference", "0"));
        mAudioCodec = mSharedPreferences.getString("codec_preference", AudioCodec.MP3);
        mAudioFormat = mSharedPreferences.getString("format_preference", AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        Log.i(TAG, "getData: mSharedPreferences speed:" + mSharedPreferences.getString("speed_preference", "0"));
        Log.i(TAG, "getData: mSpeed = " + mSpeed + ";mAudioCodec=" + mAudioCodec + ";mAudioFormat=" + mAudioFormat);
    }

    /**
     * 参数设置
     *
     * @param s
     */
    private void setParam(final String s) {
        mParams = new VoiceParameters(s, mLanguage);
        mParams.setCodec(mAudioCodec);
        mParams.setFormat(mAudioFormat);
        mParams.setBase64(false);
        mParams.setSSML(false);
        mParams.setRate(mSpeed);
        mTTs.addSpeechErrorEventListener(mSpeechErrorEventListener);
        mTTs.addSpeechDataEventListener(mSpeechDataEventListener);
        mTTs.speechAsync(mParams);
    }

    private SpeechErrorEventListener mSpeechErrorEventListener = new SpeechErrorEventListener() {
        @Override
        public void handleSpeechErrorEvent(SpeechErrorEvent e) {
            //System.out.print(e.getException().getMessage());
            Log.e(TAG, "handleSpeechErrorEvent: " + e.getException().getMessage().toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStartBt.setText("开始合成");
                    mStartBt.setEnabled(true);
                }
            });

        }
    };

    private SpeechDataEventListener mSpeechDataEventListener = new SpeechDataEventListener() {
        @Override
        public void handleSpeechDataEvent(SpeechDataEvent<?> e) {

            try {
                String fileName = getCacheDir() + "/voice.mp3";
                Log.i(TAG, "Tywin fileName: " + fileName);
                File file = new File(fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write((byte[]) e.getData());
                fos.close();

                AudioManager audioManager = (AudioManager) getSystemService(MainActivity.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);

                if (mMediaPlayer != null)
                    mMediaPlayer.release();

                FileInputStream fis = new FileInputStream(fileName);

                Log.i(TAG, "Tywin start play");
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
                mMediaPlayer.setDataSource(fis.getFD());
                mMediaPlayer.setOnErrorListener(onErrorListener);
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
                mMediaPlayer.setOnPreparedListener(onPreparedListener);
                mMediaPlayer.prepareAsync();

                fis.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStartBt.setText("开始合成");
                        mStartBt.setEnabled(true);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "onPrepared: ");
            mp.setVolume(1, 1);
            mp.start();
        }
    };

    MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "onError: ");
            return false;
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "onCompletion: ");
            mp.release();
            mp = null;
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
