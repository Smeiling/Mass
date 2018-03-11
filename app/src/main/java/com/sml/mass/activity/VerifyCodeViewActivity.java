package com.sml.mass.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sml.mass.R;
import com.sml.mass.components.basic.VerifyCodeView;

/**
 * Created by Smeiling on 2018/3/11.
 */

public class VerifyCodeViewActivity extends AppCompatActivity {

    private VerifyCodeView verifyCodeView;


    private String correctString = "1123";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_view);

        verifyCodeView = findViewById(R.id.verify_code_view);
        verifyCodeView.setOnVerificationListener(new VerifyCodeView.OnVerificationListener() {
            @Override
            public void onVerified(String verifyCode) {
                if(verifyCode.equals(correctString)){
                    verifyCodeView.clearCode();
                } else {
                    verifyCodeView.onVerifyFailed();
                }
            }
        });
    }
}
