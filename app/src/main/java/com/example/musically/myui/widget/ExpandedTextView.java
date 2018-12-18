package com.example.musically.myui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

    private View mToggleView;
    private TextView mTv;
    private int mTextHeightWidthMaxLines;


    private int mCollapsedHeight;
    private int mMarginBetweenTextAndBottom;
    private int mMaxCollapsedLines;
    private int mExpandTextId;
    private int mToggleId;

    private ExpandedStateController mExpandedStateController;
    private float mAnimaionAlphaStart;

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
        mExpandTextId = typedArray.getResourceId(R.styleable.ExpandedTextView_expandTextId, 0);
        mToggleId = typedArray.getResourceId(R.styleable.ExpandedTextView_togglerId, 0);
        mMaxCollapsedLines = typedArray.getInteger(R.styleable.ExpandedTextView_maxCollapsedLines, 8);
        typedArray.recycle();
        setOrientation(VERTICAL);
        mTv = findViewById(R.id.expandTextId);
        Log.e("wanglei", "mTv is " + mTv);
        mTv.setOnClickListener(this);


        mToggleView = findViewById(R.id.togglerId);
        mToggleView.setOnClickListener(this);
        mExpandedStateController = createExpandedStateController();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private ExpandedStateController createExpandedStateController() {
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mRelayout || getVisibility() == GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mRelayout = false;
        mToggleView.setVisibility(GONE);
        mTv.setMaxLines(Integer.MAX_VALUE);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTv.getLineCount() <= mMaxCollapsedLines) {
            return;
        }

        mTextHeightWidthMaxLines = getRealTextViewHeight(mTv);
        if (mCollapsed) {
            mTv.setMaxLines(mMaxCollapsedLines);
        }

        mToggleView.setVisibility(VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            mTv.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTextAndBottom = getHeight() - mTv.getHeight();
                }
            });

            mCollapsedHeight = getMeasuredHeight();
        }
    }

    private int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();

        return textHeight + padding;
    }

    public void setText(String content) {
        mRelayout = true;
        mTv.setText(content);
        mTv.setVisibility(TextUtils.isEmpty(content) ? GONE : VISIBLE);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    @Override
    public void onClick(View view) {
        if (mToggleView.getVisibility() != View.VISIBLE) {
            return;
        }

        mCollapsed = !mCollapsed;
        mExpandedStateController.changeState(mCollapsed);

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
                setAlpha(mAnimaionAlphaStart);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
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
        }

    }
}