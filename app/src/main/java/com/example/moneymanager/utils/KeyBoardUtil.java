package com.example.moneymanager.utils;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.text.InputType;

import java.util.List;
import com.example.moneymanager.R;

public class KeyBoardUtil {
    private KeyboardView keyboardView;
    private Context mContext;
    private KeyboardView mKeyboardView;
    private Keyboard mNumberKeyboard; // 数字键盘
    private Keyboard mLetterKeyboard; // 字母键盘
    private Keyboard mPunctuateKeyboard; // 标点键盘

    private boolean isNumber = false;  // 是否数字键盘
    private boolean isUpper = false;   // 是否大写
    private boolean isPun = false;   // 是否标点
    private EditText mEditText;
    public  final int KEYCODE_PUN = -7;
    public  final int NUMBERDECIMAL = 0x00002002;//只显示数字键盘

    public int inputType ; // 0:字母键盘，1：数字键盘，2：字符键盘
    private OnClickDone onClickDone;

    public interface OnClickDone{//OnEnsureListener
        public void onDone();//onEnsure
    }
    public void setOnClickDone(OnClickDone onClickDone) {

        this.onClickDone = onClickDone;
    }

    public KeyBoardUtil(Context context, KeyboardView view, EditText editText, int inputType) {
        this.inputType = inputType;
        this.mContext = context;
        this.isNumber = inputType == 1;
        this.mEditText = editText;
        editText.setInputType(InputType.TYPE_NULL);
        mNumberKeyboard = new Keyboard(mContext, R.xml.keyboard_numbers);
        mLetterKeyboard = new Keyboard(mContext, R.xml.keyboard_qwerty);
        mPunctuateKeyboard = new Keyboard(mContext, R.xml.keyboard_punctuate);
        mKeyboardView = view;
        List<Keyboard.Key> mNumberKeyList = mNumberKeyboard.getKeys();
        for (Keyboard.Key key : mNumberKeyList) {
            if(key!=null ){
                if( key.codes[0] == -2 || key.codes[0] == -3|| key.codes[0] == -4
                        ||key.codes[0] == -5 || key.codes[0] == -7||key.codes[0] == 32
                        ||key.codes[0] == 46 ||key.codes[0] == 57419||key.codes[0] == 57421
                        ||key.codes[0] == 44) {
                    key.onPressed();
                }
            }
        }
        List<Keyboard.Key> mLetterKeyList = mLetterKeyboard.getKeys();
        for (Keyboard.Key key : mLetterKeyList) {
            if(key!=null ){
                if(key.codes[0] == -1 || key.codes[0] == -2 || key.codes[0] == -4||key.codes[0] == -5){
                    key.onPressed();
                }
            }
        }
        List<Keyboard.Key> mPunctuateKeyList = mPunctuateKeyboard.getKeys();
        for (Keyboard.Key key : mPunctuateKeyList) {
            if(key!=null ){

                if( key.codes[0] == -2 || key.codes[0] == -4||key.codes[0] == -5 || key.codes[0] == -7) {
                    key.onPressed();
                }
            }
        }

        switch (inputType){
            case 0:
                mKeyboardView.setKeyboard(mLetterKeyboard);
                break;
            case 1:
                mKeyboardView.setKeyboard(mNumberKeyboard);
                break;
            case 2:
                mKeyboardView.setKeyboard(mPunctuateKeyboard);
                break;
            default:
                mKeyboardView.setKeyboard(mLetterKeyboard);
                break;
        }
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(listener);
    }

    /*键盘监听*/
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEditText.getText();
            mEditText.setSelection(mEditText.getText().toString().length());
            int start = mEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) { // cancel
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DONE) { // done
                if(onClickDone!=null){
                    onClickDone.onDone();
                }
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) { // 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) { // 大小写切换

                changeKeyboart();
                mKeyboardView.setKeyboard(mLetterKeyboard);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) { // 数字键盘切换
                if (isNumber) {
                    if(mEditText.getInputType() == NUMBERDECIMAL){
                        return;
                    }
                    isNumber = false;
                    mKeyboardView.setKeyboard(mLetterKeyboard);
                } else {
                    if(isPun){
                        isPun = false;
                        mKeyboardView.setKeyboard(mLetterKeyboard);
                    }else{
                        isNumber = true;
                        mKeyboardView.setKeyboard(mNumberKeyboard);
                    }
                }
            }else if(primaryCode== KEYCODE_PUN){
                if(mEditText.getInputType() == NUMBERDECIMAL){
                    return;
                }
                if(isPun){
                    isPun = false;
                    isNumber =true;
                    mKeyboardView.setKeyboard(mNumberKeyboard);
                }else {
                    isPun = true;
                    isNumber = false;
                    mKeyboardView.setKeyboard(mPunctuateKeyboard);

                }
            }else if (primaryCode == 57419) { // 左移
                if (start > 0) {
                    mEditText.setSelection(start - 1);
                }

            } else if (primaryCode == 57421) { // 右移
                if (start < mEditText.length()) {
                    mEditText.setSelection(start + 1);
                }
            }  else { // 输入键盘值
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    private void changeKeyboart() {
        List<Keyboard.Key> keyList = mLetterKeyboard.getKeys();
        if (isUpper) { // 大写切换小写
            isUpper = false;
            for (Keyboard.Key key : keyList) {
                if(key.codes[0] == -1){
                    key.onPressed();
                }
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else { // 小写切换成大写
            isUpper = true;
            for (Keyboard.Key key : keyList) {
                if(key.codes[0] == -1){
                    key.onPressed();
                }
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    /**
     * 判断是否是字母
     */
    private boolean isLetter(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str.toLowerCase());
    }

    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        }
    }

    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }



}