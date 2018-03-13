package com.sml.mass.components.basic;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sml.mass.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smeiling on 2018/3/11.
 */

public class VerifyCodeView extends LinearLayout {


    private boolean shakeOnFailed = true;
    private boolean vibrateOnFailed = true;

    private int codeCount = 4;
    private int codeTextSize = 12;
    private int codeTextColor = 0xFF999999;
    private int codeGapWidth = -1;
    private int shakeDistance = 15;

    private int cursorColor = 0xFFFF611B;
    private int codeBackgroundResource = R.drawable.verify_code_default;


    private List<EditText> codeViews = new ArrayList<>();
    private int cursorIndex = 0;
    /**
     * Vibrate pattern
     */
    private long[] vibratePattern;

    private OnVerificationListener onVerificationListener;

    public interface OnVerificationListener {
        void onVerified(String verifyCode);
    }


    public VerifyCodeView(Context context) {
        this(context, null);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeView);
        codeCount = array.getInt(R.styleable.VerifyCodeView_vcv_codeCount, 4);
        codeTextSize = array.getDimensionPixelSize(R.styleable.VerifyCodeView_vcv_codeTextSize, 24);
        codeTextColor = array.getColor(R.styleable.VerifyCodeView_vcv_codeTextColor, 0xFF999999);
        codeGapWidth = array.getDimensionPixelSize(R.styleable.VerifyCodeView_vcv_codeGapWidth, -1);
        cursorColor = array.getColor(R.styleable.VerifyCodeView_vcv_cursorColor, 0xffFF611B);
        shakeOnFailed = array.getBoolean(R.styleable.VerifyCodeView_vcv_enableShake, true);
        vibrateOnFailed = array.getBoolean(R.styleable.VerifyCodeView_vcv_enableVibrate, true);
        shakeDistance = array.getDimensionPixelSize(R.styleable.VerifyCodeView_vcv_shakeDistance, 15);
        codeBackgroundResource = array.getResourceId(R.styleable.VerifyCodeView_vcv_codeBackground, R.drawable.verify_code_default);

        array.recycle();
        initView();
    }

    /**
     * Clear all
     */
    public void clearCode() {
        resetViewState();
    }


    /**
     * Reset VerifyCodeView when verifyFailed.
     * Shake view and invoke vibration if needed.
     */
    public void onVerifyFailed() {
        if (shakeOnFailed) {
            shakeView();
        }
        if (vibrateOnFailed) {
            vibrate();
        }
        resetViewState();
    }

    /**
     * Customize vibrate pattern
     *
     * @param vibratePattern
     */
    public void setVibratePattern(long[] vibratePattern) {
        this.vibratePattern = vibratePattern;
    }

    public void setOnVerificationListener(OnVerificationListener onVerificationListener) {
        this.onVerificationListener = onVerificationListener;
    }

    private void initView() {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setGravity(Gravity.CENTER);
        initCodeView(codeCount);
    }

    /**
     * Add EditText view into layout
     *
     * @param codeCount
     */
    private void initCodeView(int codeCount) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        EditText code;
        for (int i = 0; i < codeCount; i++) {
            code = generateCodeView();
            codeViews.add(code);
            addView(code);
        }
        resetViewState();
    }

    /**
     * Reset verifyCodeView
     */
    private void resetViewState() {
        for (EditText editText : codeViews) {
            editText.setText("");
        }
        cursorIndex = 0;
        updateFocusCode(cursorIndex);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View view = getChildAt(0);
        int padding;
        int margin;
        if (codeGapWidth < 0) {
            // If codeGapWidth was not customized, calculate default padding value.
            padding = (getMeasuredWidth() - shakeDistance * 3 - codeViews.size() * view.getMeasuredWidth()) / (codeViews.size() - 1);
        } else {
            // Customized padding value.
            padding = codeGapWidth;

        }
        margin = shakeDistance * 3 / 2;
        layoutCodeViewWithPadding(padding, margin);
    }

    /**
     * Add margins
     *
     * @param margin
     * @param padding
     */
    private void layoutCodeViewWithPadding(int padding, int margin) {
        LayoutParams params;
        for (int i = 0; i < codeViews.size(); i++) {
            params = (LayoutParams) codeViews.get(i).getLayoutParams();
            if (i == 0) {
                params.setMargins(margin, 0, 0, 0);
            } else if (i == codeViews.size() - 1) {
                params.setMargins(padding, 0, margin, 0);
            } else {
                params.setMargins(padding, 0, 0, 0);
            }
            codeViews.get(i).setLayoutParams(params);
        }
    }

    /**
     * Create EditText
     *
     * @return
     */
    private EditText generateCodeView() {
        EditText editText = new EditText(getContext());
        editText.setMaxLines(1);
        editText.setTextColor(codeTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSize);
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setBackgroundResource(codeBackgroundResource);
        editText.setOnKeyListener(new DeleteCodeListener());
        editText.addTextChangedListener(new CodeTextWatcher());
        editText.setOnTouchListener(new OnCodeViewTouchedListener());
        injectCursorStyle(editText);

        return editText;
    }

    /**
     * Change cursor color by inject mCursorDrawableRes
     *
     * @param editText
     */
    private void injectCursorStyle(EditText editText) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[1];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes, null);
            } else {
                drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            }
            drawables[0].setColorFilter(cursorColor, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VerifyCode", "Inject failed");
        }
    }

    /**
     * Do validate when code input finished.
     */
    private void doValidate() {
//        hideKeyboard();
        StringBuilder stringBuilder = new StringBuilder();
        for (EditText editText : codeViews) {
            stringBuilder.append(editText.getText().toString());
        }
        if (onVerificationListener != null) {
            onVerificationListener.onVerified(stringBuilder.toString());
        }
        updateFocusCode(-1);
    }

    /**
     * Hide keyboard when code input finished.
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }

    }

    /**
     * Force show inputmethod if need
     */
    public void showKeyboard() {
        View view = codeViews.get(cursorIndex);
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }


    /**
     * Vibrate device when verify failed.
     */
    private void vibrate() {
        try {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (vibratePattern != null) {
                    vibrator.vibrate(vibratePattern, 0);
                } else {
                    vibrator.vibrate(400);
                }
            }
        } catch (Exception e) {
            Log.e("VerifyCode", "Vibrate failed");
        }
    }

    /**
     * Shake verifyCodeView when verify failed.
     */
    private void shakeView() {
        try {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this,
                    "translationX", 0, shakeDistance, -shakeDistance, shakeDistance, -shakeDistance, shakeDistance - 10, -shakeDistance + 10, 6, -6, 0);
            objectAnimator.setDuration(1000);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    clearCode();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
        } catch (Exception e) {
            Log.e("VerifyCode", "Animate failed");
        }
    }

    /**
     * Highlight selected CodeEditText.
     *
     * @param cursorIndex
     */
    private void updateFocusCode(int cursorIndex) {
        EditText codeView;
        for (int i = 0; i < codeViews.size(); i++) {
            codeView = codeViews.get(i);
            if (i == cursorIndex) {
                codeView.setFocusable(true);
                codeView.setFocusableInTouchMode(true);
                codeView.setClickable(true);
            } else {
                codeView.setFocusable(false);
                codeView.setFocusableInTouchMode(false);
                codeView.setClickable(false);
            }
            codeView.setTextIsSelectable(false);
            codeView.setLongClickable(false);
        }
        if (cursorIndex >= 0 && cursorIndex < codeViews.size()) {
            codeViews.get(cursorIndex).requestFocus();
        }
    }

    /**
     * TextWatcher for CodeEditText
     */
    public class CodeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())) {

            } else {
                cursorIndex++;
                if (cursorIndex == codeViews.size()) {
                    doValidate();
                } else {
                    updateFocusCode(cursorIndex);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class DeleteCodeListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (cursorIndex > 0) {
                    cursorIndex--;
                    codeViews.get(cursorIndex).setText("");
                    updateFocusCode(cursorIndex);
                }
            }
            return false;
        }
    }

    private class OnCodeViewTouchedListener implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            updateFocusCode(cursorIndex);
            return false;
        }
    }

}
