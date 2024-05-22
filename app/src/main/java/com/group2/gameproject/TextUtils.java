package com.group2.gameproject;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextUtils {
    public static void AddOnTextChange(EditText editText, final AfterTextChanged afterTextChanged) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                afterTextChanged.afterTextChanged(s.toString());
            }


        });
    }

    public static boolean isNullOrEmpty(String string){
        if(string!=null && string.length()>0) return false;
        return true;
    }



}