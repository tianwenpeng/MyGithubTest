package com.speech.tts.ttsdemo.utils;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * 输入框输入范围控制
 */
public class SettingTextWatcher implements TextWatcher {
    private int editStart;
    private int editCount;
    private EditTextPreference mEditTextPreference;
    int minValue;//最小值
    int maxValue;//最大值
    double minValue_d;
    double maxValue_d;
    private Context mContext;

    private boolean flag = false;//科大讯飞、云知声：false；思必驰：true；

    public SettingTextWatcher(Context context, EditTextPreference e, int min, int max) {
        mContext = context;
        mEditTextPreference = e;
        minValue = min;
        maxValue = max;
    }

    public SettingTextWatcher(Context context, EditTextPreference e, double min, double max, boolean f) {
        mContext = context;
        mEditTextPreference = e;
        minValue_d = min;
        maxValue_d = max;
        flag = f;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//		Log.e("demo", "onTextChanged start:"+start+" count:"+count+" before:"+before);
        editStart = start;
        editCount = count;
        Log.i("Tywin", "onTextChanged: start=" + start + ";before=" + before + ";count=" + count);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//		Log.e("demo", "beforeTextChanged start:"+start+" count:"+count+" after:"+after);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i("tywin", "afterTextChanged: minValue_d=" + minValue_d + ";maxValued_d=" + maxValue_d);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        String content = s.toString();
//		Log.e("demo", "content:"+content);
        if (!flag) {
            if (isNumeric(content)) {
                int num = Integer.parseInt(content);
                if (num > maxValue || num < minValue) {
                    s.delete(editStart, editStart + editCount);
                    mEditTextPreference.getEditText().setText(s);
                    Toast.makeText(mContext, "超出有效值范围", Toast.LENGTH_SHORT).show();
                }
            } else {
                s.delete(editStart, editStart + editCount);
                mEditTextPreference.getEditText().setText(s);
                Toast.makeText(mContext, "只能输入数字哦", Toast.LENGTH_SHORT).show();
            }
        }
//        } else {
//            if (editStart == 0 && editCount == 1) {
//                if (isNumeric(content)) {
//                    double num = Double.parseDouble(content);
//                    Log.i("tywin", "0 afterTextChanged: content:" + content);
//                    if (num < 0 || (0 < num && num < minValue_d) || num > maxValue_d) {
//                        s.delete(editStart, editStart + editCount);
//                        mEditTextPreference.getEditText().setText(s);
//                        Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
//                    }
////                    if (num == 0) {
////                        Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
////                    }
//                } else {
//                    s.delete(editStart, editStart + editCount);
//                    mEditTextPreference.getEditText().setText(s);
//                    Toast.makeText(mContext, "只能输入数字哦", Toast.LENGTH_SHORT).show();
//                }
//            } else if (editStart == 1 && editCount == 1) {
//                Log.i("tywin", "1 afterTextChanged: content:" + content);
//                if (!content.endsWith(".")) {
//                    Log.i("tywin", "1 afterTextChanged: content::" + content);
//                    s.delete(editStart, editStart + editCount);
//                    mEditTextPreference.getEditText().setText(s);
//                    Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
//                }else {
//                    double num = Double.parseDouble(content);
//                    if (num < minValue_d || num > maxValue_d) {
////                        s.delete(editStart, editStart + editCount);
////                        mEditTextPreference.getEditText().setText(s);
//                        Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
//                    }
//                }
////                if (!isPoint(content)) {
////                    Log.i("tywin", "1 afterTextChanged: content::" + content);
////                    s.delete(editStart, editStart + editCount);
////                    mEditTextPreference.getEditText().setText(s);
////                    Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
////                }
//            } else if (editStart == 2 && editCount == 1) {
//                Log.i("tywin", "2 afterTextChanged: content:" + content);
//                String new_contet = content.substring(content.length() - 1, content.length());
//                if (isNumeric(new_contet)) {
//                    double num = Double.parseDouble(content);
//                    if (num < minValue_d || num > maxValue_d) {
//                        s.delete(editStart, editStart + editCount);
//                        mEditTextPreference.getEditText().setText(s);
//                        Toast.makeText(mContext, "请输入0.5～2.0范围内的数字", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    s.delete(editStart, editStart + editCount);
//                    mEditTextPreference.getEditText().setText(s);
//                    Toast.makeText(mContext, "只能输入数字哦", Toast.LENGTH_SHORT).show();
//                }
//
//            }
////            if (isNumeric(content)) {
////                double num = Double.parseDouble(content);
////                if (num > maxValue_d || num < minValue_d) {
////                    s.delete(editStart, editStart + editCount);
////                    mEditTextPreference.getEditText().setText(s);
////                    Toast.makeText(mContext, "超出有效值范围", Toast.LENGTH_SHORT).show();
////                }
////            }else{
////                s.delete(editStart, editStart + editCount);
////                mEditTextPreference.getEditText().setText(s);
////                Toast.makeText(mContext, "只能输入数字哦", Toast.LENGTH_SHORT).show();
////            }
//        }
    }

    /**
     * 正则表达式-判断是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 正则表达式-判断是否为数字0 1 2
     */
    public static boolean isNumeric012(String str) {
        Pattern pattern = Pattern.compile("[0-2]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 正则表达式-判断最后一位是否为.
     */
    public static boolean isPoint(String str) {
        Pattern pattern = Pattern.compile("[.]$");
        return pattern.matcher(str).matches();
    }

    /**
     * 正则表达式-判断最后一位是否为数字
     */
    public static boolean isNumericOfLast(String str) {
        Pattern pattern = Pattern.compile("[0-9]$");
        return pattern.matcher(str).matches();
    }

};
