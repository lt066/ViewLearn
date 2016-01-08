package com.example.administrator.viewlearn.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lt on 2016/1/7.
 */
public class MyView2 extends View {

    private static final int THUMB_SIZE_DEFAULT=60;
    private static final int BACK_WIDTH_DEFAULT=150;
    private static final int DEFAULT_ANIMATION_DURATION =100;
    private static final int DEFAULT_TINT_COLOR=0x327FC2;
    private static final int DEFAULT_THUMB_COLOR=0x133bbe;
    private static final int DEFAULT_BACK_COLOR=0xfcfbfb;
    private RectF mThumbRectF,mBackRectF;
    private Paint mPaint;
    public MyView2(Context context) {
        super(context);
    }

    public MyView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Resources res = getResources();
        float density = res.getDisplayMetrics().density;
        mThumbRectF = new RectF();
        mPaint= new Paint();
        mThumbRectF.set(100, 50, 100 + THUMB_SIZE_DEFAULT, 50 + THUMB_SIZE_DEFAULT);
//        mBackRectF.set(mThumbRectF.left,mThumbRectF.top,getPaddingLeft()+BACK_WIDTH_DEFAULT,mThumbRectF.bottom);
        mPaint.setAlpha(255);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(120, 70, 10 * density, mPaint);
    }
}
