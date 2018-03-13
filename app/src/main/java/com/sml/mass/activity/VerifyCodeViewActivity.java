package com.sml.mass.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.sml.mass.R;
import com.sml.mass.components.basic.VerifyCodeView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Smeiling on 2018/3/11.
 */

public class VerifyCodeViewActivity extends AppCompatActivity {

    private VerifyCodeView verifyCodeView;
    private Button clearBtn;
    private Button hideBtn;
    private Button closeBtn;

    private String correctString = "1123";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_view);

        clearBtn = findViewById(R.id.clear_btn);
        hideBtn = findViewById(R.id.hide_btn);
        closeBtn = findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCodeView.hideKeyboard();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCodeView.clearCode();
            }
        });
        verifyCodeView = findViewById(R.id.verify_code_view);
        verifyCodeView.setOnVerificationListener(new VerifyCodeView.OnVerificationListener() {
            @Override
            public void onVerified(String verifyCode) {
                if (verifyCode.equals(correctString)) {
                    verifyCodeView.clearCode();
                } else {
                    verifyCodeView.onVerifyFailed();
                    verifyCodeView.clearCode();
                }
            }
        });

    }

}
