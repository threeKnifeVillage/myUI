package com.example.musically.myui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musically.myui.R;

/**
 * <pre>
 *     author : 王磊
 *     time   : 2018/12/4
 *     desc   : 可扩展高度的textview
 *     version: 1.0
 * </pre>
 */
public class ExpandedTextView extends LinearLayout implements View.OnClickListener {

    private boolean mRelayout;
    private boolean mCollapsed = true;
    private boolean mIsAnimation;

    private TextView mTv;
    private ImageButton mButton;
    private int mTextHeightWidthMaxLines;


    private int mCollapsedHeight;
    private int mMarginBetweenTextAndBottom;
    private int mMaxCollapsedLines;

    private Drawable mExpandDrawable;
    private Drawable mCollapsedDrawable;

    private ExpandedStateController mExpandedStateController;
    private float mAnimaionAlphaStart;
    private int mHeight;

    public ExpandedTextView(Context context) {
        this(context, null);
    }

    public ExpandedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ExpandedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandedTextView);
        mMaxCollapsedLines = typedArray.getInteger(R.styleable.ExpandedTextView_maxCollapsedLines, 2);
        typedArray.recycle();
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTv = findViewById(R.id.expandable_text);
        Log.e("wanglei", "mTv IS " + mTv);
        mTv.setOnClickListener(this);

        mButton = findViewById(R.id.expand_collapse);
        mButton.setOnClickListener(this);
        mExpandedStateController = createExpandedStateController();
    }

    private ExpandedStateController createExpandedStateController() {
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("wanglei", "onMeasure" + mCollapsed + " mTv.getLineCount()" + mTv.getLineCount());
        if (!mRelayout) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mRelayout = false;
        mTv.setMaxLines(Integer.MAX_VALUE);
        mButton.setVisibility(GONE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTv.getLineCount() < mMaxCollapsedLines) {
            return;
        }
        mTextHeightWidthMaxLines = getRealTextViewHeight(mTv);

        if (mCollapsed) {
            mTv.setMaxLines(mMaxCollapsedLines);
        }
//        mButton.setVisibility(VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            mTv.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTextAndBottom = getHeight() - mTv.getHeight();
                    Log.e("wanglei", " mMarginBetweenTextAndBottom " + mMarginBetweenTextAndBottom);
                }
            });
            mCollapsedHeight = getMeasuredHeight();
            Log.e("wanglei", "mCollapsedHeight " + mCollapsedHeight);
        }

        Log.e("wanglei", "getHeight " + getHeight());
    }

    private int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();

        return textHeight + padding;
    }

    public void setText(String text) {
        mRelayout = true;
        mTv.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        requestLayout();
    }

    @Override
    public void onClick(View view) {
        Log.e("wanglei", "onClick");
//        if (mButton.getVisibility() != View.VISIBLE) {
//            return;
//        }

        mCollapsed = !mCollapsed;
        mButton.setImageDrawable(mCollapsed ? mExpandDrawable : mCollapsedDrawable);
        Log.e("wanglei", "mCollapsed is " + mCollapsed);

        Animation animation;
        if (mCollapsed) {
            animation = new ExpandedCollapsedAnimation(this, getHeight(), mCollapsedHeight);
        } else {
            animation  = new ExpandedCollapsedAnimation(this, getHeight(), getHeight() + mTextHeightWidthMaxLines - mTv.getHeight());
        }
        mIsAnimation = true;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                setAlpha(mAnimaionAlphaStart);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                mIsAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        clearAnimation();
        startAnimation(animation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsAnimation;
    }

    public interface ExpandedStateController {
        void changeState(boolean collapsed);

        void setView(View toggleView);
    }

    private class ExpandedCollapsedAnimation extends Animation {
        private int mStartHeight;
        private int mEndHeight;
        private View mTargetView;

        private ExpandedCollapsedAnimation(View targetView, int startHeight, int endHeight) {
            mTargetView = targetView;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Log.e("wang.lei", "interpolatedTime is " + interpolatedTime);
            final int newHeight = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTv.setMaxHeight(newHeight - mMarginBetweenTextAndBottom);
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }


        @Override
        public void initialize( int width, int height, int parentWidth, int parentHeight ) {
            super.initialize(width, height, parentWidth, parentHeight);
        }
    }
}

