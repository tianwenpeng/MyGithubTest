package com.speech.tts.ttsdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.aispeech.DUILiteSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button iflyTTSBt, cSoundTTSBt, sBitchTTSBt,voiceRssTTSBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
    }

    private void initView() {
        iflyTTSBt = findViewById(R.id.ifly_tts_bt);
        cSoundTTSBt = findViewById(R.id.csound_tts_bt);
        sBitchTTSBt = findViewById(R.id.sbitch_tts_bt);
        voiceRssTTSBt = findViewById(R.id.voicerss_tts_bt);

        iflyTTSBt.setOnClickListener(this);
        cSoundTTSBt.setOnClickListener(this);
        sBitchTTSBt.setOnClickListener(this);
        voiceRssTTSBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ifly_tts_bt://科大讯飞TTS
                intent = new Intent(MainActivity.this, IflyTTSActivity.class);
                startActivity(intent);
                break;

            case R.id.csound_tts_bt://云之声TTS
                intent = new Intent(MainActivity.this, UnisoundTTSActivity.class);
                startActivity(intent);
                break;
            case R.id.sbitch_tts_bt://思必驰TTS
                DUILiteSDK.init(getApplicationContext(),"b431396774f73596136c1b385ac490dc","278571602",new DUILiteSDK.InitListener() {
                    @Override
                    public void success() {
                        Intent intent1 = new Intent(MainActivity.this, AispeechActivity.class);
                        startActivity(intent1);
                    }

                    @Override
                    public void error(final String errorCode, final String errorInfo) {
                        Log.i(TAG, "error: 授权失败 errorCode:"+errorCode+";errorInfo:"+errorInfo);

                    }
                });

                break;
            case R.id.voicerss_tts_bt:
                intent = new Intent(MainActivity.this,VoiceRSSActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
