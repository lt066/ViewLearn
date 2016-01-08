package com.example.administrator.viewlearn.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;

import com.example.administrator.viewlearn.R;

/**
 * Created by lt on 2016/1/7.
 */
public class MySwitch1 extends CompoundButton{

    private static final int THUMB_SIZE_DEFAULT=60;
    private static final int BACK_WIDTH_DEFAULT=150;
    private static final int DEFAULT_ANIMATION_DURATION =100;
    private static final int DEFAULT_TINT_COLOR=0x327FC2;
    private static final int DEFAULT_THUMB_COLOR=0x133bbe;
    private static final int DEFAULT_BACK_COLOR=0xfcfbfb;
    //触发移动的最小距离
    private int mTouchSlop;
    //点击超时
    private int mClickTimeout;

    private int mTintColor;

    private int mBackColor;

    private int mThumbColor;

    private int mCurrentBackColor,mNextBackColor;

    private RectF mThumbRectF,mBackRectF;

    private Paint mPaint;

    private ObjectAnimator mAnimator;

    public MySwitch1(Context context) {
        super(context);
    }

    public MySwitch1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MySwitch1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        // ViewConfiguration.getPressedStateDuration() 获得的是按下效果显示的时间,由PRESSED_STATE_DURATION常量指定,为64毫秒.
        //getTapTimeout定义一个touch事件中是点击事件还是一个滑动事件所需的时间，如果用户在这个时间之内滑动，那么就认为是一个点击事件
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
        mThumbRectF = new RectF();
        mBackRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimator = ObjectAnimator.ofFloat(this, "process", 0, 0).setDuration(DEFAULT_ANIMATION_DURATION);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        TypedArray ta = attrs==null?null:getContext().obtainStyledAttributes(attrs, R.styleable.MySwitch1);
        if(ta == null)
        {
            mBackColor= DEFAULT_BACK_COLOR;
            mTintColor=DEFAULT_TINT_COLOR;
            mThumbColor=DEFAULT_THUMB_COLOR;
            mThumbRectF.set(getPaddingLeft(),getPaddingTop(),getPaddingLeft()+THUMB_SIZE_DEFAULT,getPaddingTop()+THUMB_SIZE_DEFAULT);
            mBackRectF.set(mThumbRectF.left,mThumbRectF.top,getPaddingLeft()+BACK_WIDTH_DEFAULT,mThumbRectF.bottom);
        }
        else
        {
            mBackColor= DEFAULT_BACK_COLOR;
            mTintColor=DEFAULT_TINT_COLOR;
            mThumbColor=DEFAULT_THUMB_COLOR;
            mThumbRectF.set(getPaddingLeft(),getPaddingTop(),getPaddingLeft()+THUMB_SIZE_DEFAULT,getPaddingTop()+THUMB_SIZE_DEFAULT);
            mBackRectF.set(mThumbRectF.left,mThumbRectF.top,getPaddingLeft()+BACK_WIDTH_DEFAULT,mThumbRectF.bottom);
        }
        setFocusable(true);
        setClickable(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHight(heightMeasureSpec));
    }

    private  int measureWidth(int widthMeasureSpec)
    {
        int getWidth = MeasureSpec.getSize(widthMeasureSpec);
        int getMode = MeasureSpec.getMode(widthMeasureSpec);
        int minWidth = BACK_WIDTH_DEFAULT;
        int measureWidth;
        if(getMode==MeasureSpec.EXACTLY)
        {
            measureWidth = Math.max(minWidth,getWidth);
        }
        else
        {
            measureWidth=minWidth;
            if(getMode==MeasureSpec.AT_MOST)
            {
                measureWidth=Math.min(minWidth,getWidth);
            }
        }
        return measureWidth;
    }

    private int measureHight(int heightMeasureSpec)
    {
        int getHight = MeasureSpec.getSize(heightMeasureSpec);
        int getMode = MeasureSpec.getMode(heightMeasureSpec);
        int minHight = THUMB_SIZE_DEFAULT;
        int measureHight;
        if(getMode==MeasureSpec.EXACTLY)
        {
            measureHight = Math.max(minHight,getHight);
        }
        else
        {
            measureHight=minHight;
            if(getMode==MeasureSpec.AT_MOST)
            {
                measureHight=Math.min(minHight,getHight);
            }
        }
        return measureHight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Resources res = getResources();
        float density = res.getDisplayMetrics().density;
        if(isChecked())
        {
            mCurrentBackColor=DEFAULT_TINT_COLOR;
            mNextBackColor=DEFAULT_BACK_COLOR;
//            mPaint.setARGB(255, Color.red(mCurrentBackColor),Color.green(mCurrentBackColor),Color.blue(mCurrentBackColor));
            mPaint.setAlpha(255);
            mPaint.setColor(Color.BLACK);
            canvas.drawRoundRect(mBackRectF,15*density,15*density,mPaint);
        }
        else {
            mNextBackColor=DEFAULT_TINT_COLOR;
            mCurrentBackColor=DEFAULT_BACK_COLOR;
//            mPaint.setARGB(255, Color.red(mCurrentBackColor),Color.green(mCurrentBackColor),Color.blue(mCurrentBackColor));
            mPaint.setAlpha(255);
            mPaint.setColor(Color.BLUE);
            canvas.drawRoundRect(mBackRectF,15*density,15*density,mPaint);
        }
        mPaint.setAlpha(255);
        mPaint.setColor(Color.RED);
        canvas.drawRoundRect(mThumbRectF,THUMB_SIZE_DEFAULT/2*density,THUMB_SIZE_DEFAULT/2*density,mPaint);
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                performClick();
                setChecked(true);
                animateToState(true);
                break;
        }

        return true;

    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void animateToState (boolean checked)
    {
        if(mAnimator==null)
            return;
        if(mAnimator.isRunning())
            mAnimator.cancel();
        if(checked)
        {
            mAnimator.setFloatValues(0,1f);
        }
        else
            mAnimator.setFloatValues(1f,0);
    }
}
