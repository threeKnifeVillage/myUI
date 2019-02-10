package com.example.musically.myui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import com.example.musically.myui.R;

/**
 * <pre>
 *     author : 王磊
 *     time   : 2019/2/9
 *     desc   : 输入验证码的EditText
 *     version: 1.0
 * </pre>
 */
public class VerificationCodeEditText extends AppCompatEditText implements TextWatcher {
    private int mVerCodeLength;
    private int mVerCodePadding = 0;
    private int mVerCodeCount = 4;
    private int mBottonLineHeight = 11;
    private int mCurrentPosition;
    private Paint mBottomLinePaint;
    private Paint mBottomNormalPaint;

    public VerificationCodeEditText(Context context) {
        this(context, null);
    }

    public VerificationCodeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        mVerCodePadding = dp2px(50);
        setFocusableInTouchMode(true);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        super.addTextChangedListener(this);
    }

    private void initPaint() {
        mBottomLinePaint = new Paint();
        mBottomLinePaint.setColor(getResources().getColor(R.color.colorAccent));
        mBottomLinePaint.setStrokeWidth(mBottonLineHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = 0, heightResult = 0;
        //最终的宽度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            widthResult = widthSize;
        } else {
            widthResult = getScreenWidth(getContext());
        }
        //每个矩形形的宽度
        mVerCodeLength = (widthResult - (dp2px(5) * (mVerCodeCount - 1))) / mVerCodeCount;
        Log.e("wanglei", "mVerCodeLength is " + mVerCodeLength);

        //最终的高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            heightResult = heightSize;
        } else {
            heightResult = mVerCodeLength;
        }
        setMeasuredDimension(widthResult, heightResult);
        Log.e("wanglei", "widthResult is " + widthResult + " heightResult" + heightResult);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCurrentPosition = getText().length();
        Editable editable = getText();
        int width = mVerCodeLength - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (editable != null) {
            String value = editable.toString();
            for (int i = 0; i < value.length(); i++) {
                canvas.save();
                int start = width * i + i * mVerCodePadding;
                float startX = start + width / 2;

                TextPaint paint = getPaint();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(getCurrentTextColor());
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                float baseLine = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(String.valueOf(value.charAt(i)), startX, baseLine, paint);
                canvas.restore();
            }
        }

        // 绘制底线
        for (int i = 0; i < mVerCodeCount; i++) {
            canvas.save();
            int startX = width * i + i * mVerCodePadding;
            int startY = height - mBottonLineHeight / 2;
            int stopX = startX + width;
            Log.e("wanglei", " draw line " + startX + " " + startY + " stopX" + stopX);
            canvas.drawLine(startX, startY, stopX, startY, mBottomLinePaint);
        }
    }
    private int getMeasureResult(int measureSpec) {
        return MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY ? MeasureSpec.getSize(measureSpec) : getScreenWidth(getContext());
    }

    private int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mCurrentPosition = getText().length();
        postInvalidate();
    }


    @Override
    public void afterTextChanged(Editable s) {
        mCurrentPosition = getText().length();
        postInvalidate();
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCurrentPosition = getText().length();
        postInvalidate();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
