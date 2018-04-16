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

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;
import com.speech.tts.ttsdemo.contants.Config;

public class IflyTTSActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = IflyTTSActivity.class.getSimpleName();

    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";

    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;

    // 引擎类型 在线
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private Toast mToast;
    private SharedPreferences mSharedPreferences;

    private EditText inputEt;
    private Button vocierBt, settingBt, startBt, cancelBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifly_tts);
        //SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        initView();
        setText();
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(IflyTTSActivity.this, mTtsInitListener);

        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);

        mSharedPreferences = getSharedPreferences(SettingActivity.PREFER_NAME_IFLY, MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private void initView() {
        inputEt = findViewById(R.id.input_et);
        findViewById(R.id.vocier_bt).setOnClickListener(this);
        findViewById(R.id.setting_bt).setOnClickListener(this);
        findViewById(R.id.start_bt).setOnClickListener(this);
        findViewById(R.id.cancel_bt).setOnClickListener(this);
    }
    private void setText(){
        inputEt.setText(getResources().getText(R.string.ifly_input));
    }

    @Override
    public void onClick(View view) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        switch (view.getId()) {
            case R.id.vocier_bt://发音人选择
                String s = inputEt.getText().toString().trim();
                showPresonSelectDialog(s);
                break;
            case R.id.setting_bt://设置
                if (SpeechConstant.TYPE_CLOUD.equals(mEngineType)) {
                    Intent intent = new Intent(IflyTTSActivity.this, SettingActivity.class);
                    intent.putExtra(Config.TTS_SELECT,"ifly");
                    startActivity(intent);
                } else {
                    showTip("请前往xfyun.cn下载离线合成体验");
                }
                break;
            case R.id.start_bt://开始合成
                // 移动数据分析，收集开始合成事件
                FlowerCollector.onEvent(IflyTTSActivity.this, "tts_play");
                String ss = inputEt.getText().toString();
                // 设置参数
                setParam();
                int code = mTts.startSpeaking(ss, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.ico";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

                if (code != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误码: " + code);
                }
                break;
            case R.id.cancel_bt://取消
                mTts.stopSpeaking();
                break;
        }

    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            Log.i(TAG, "开始播放 ");
        }

        @Override
        public void onSpeakPaused() {
            Log.i(TAG, "暂停播放 ");
        }

        @Override
        public void onSpeakResumed() {
            Log.i(TAG, "继续播放 ");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            Log.i(TAG, "onBufferProgress: percent = " + percent);

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            Log.i(TAG, "onSpeakProgress: percent = " + percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //showTip("播放完成");
                Log.i(TAG, "播放完成");
            } else if (error != null) {
                //showTip(error.getPlainDescription(true));
                Log.i(TAG, "onCompleted error "+error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
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
                                voicer = mCloudVoicersValue[which];
                                Log.i(TAG, "which = "+which+" <=> voicer: "+ voicer);
                                if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)) {
                                    ((EditText) findViewById(R.id.input_et)).setText(R.string.input_hint_en);
                                } else {
                                    ((EditText) findViewById(R.id.input_et)).setText(s);
                                }
                                selectedNum = which;
                                dialog.dismiss();
                            }
                        }).show();
    }


    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
//        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
//        }else {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
//            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
//            /**
//             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
//             * 开发者如需自定义参数，请参考在线合成参数设置
//             */
//        }

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTts != null) {
            mTts.stopSpeaking();
            mTts = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //移动数据统计分析
        FlowerCollector.onResume(IflyTTSActivity.this);
        FlowerCollector.onPageStart(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(IflyTTSActivity.this);
    }
}
