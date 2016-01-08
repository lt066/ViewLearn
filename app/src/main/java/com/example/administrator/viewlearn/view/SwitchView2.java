package com.example.administrator.viewlearn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lt on 2016/1/8.
 */
public class SwitchView2 extends View {

    private final Paint mPaint= new Paint();
    private final Path mPath = new Path();
    private int mWidth,mHeight;
    private float sWidth,sHeight;
    private float sLeft,sTop,sRight,sBottom;
    private float sCenterX,sCenterY;

    private float bRadius,bStrokWidth;
    private float bWidth;
    private float bLeft,bTop,bRight,bBottom;
    private float bTranslateX;
    private float sScaleCenterX;
    private float sScale;

    private float sAnim,bAnim;

    private boolean isOn=false;

    public SwitchView2(Context context) {
        super(context);
    }

    public SwitchView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    public SwitchView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize =MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) (widthSize*0.65f);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        mHeight=h;
        sLeft=sTop=0;
        sRight=mWidth;
        sBottom=mHeight*0.8f;
        sWidth=sRight-sLeft;
        sHeight=sBottom-sTop;
        sCenterX=(sLeft+sRight)/2;
        sCenterY=(sTop+sBottom)/2;
        RectF rectF =new RectF(sLeft,sTop,sBottom,sBottom);
        mPath.arcTo(rectF,90,180);
        rectF.left=sRight-sBottom;
        rectF.right=sRight;
        mPath.arcTo(rectF,270,180);
        mPath.close();

        bLeft=bTop=0;
        bBottom=bRight=sBottom;
        bWidth=bRight-bLeft;
        final float halfHeight=(sBottom-sTop)/2;
        bRadius = halfHeight*0.98f;
        bStrokWidth=halfHeight-bRadius;
        sScale=1-bStrokWidth/sHeight;
        sScaleCenterX=sWidth-halfHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffcccccc);
        canvas.drawPath(mPath, mPaint);
        sAnim = sAnim-0.1f>0?sAnim-0.1f:0;
        final float scale = (float) (0.98*(isOn?sAnim:1-sAnim));
        canvas.save();
        canvas.scale(scale, scale, sCenterX, sCenterY);
        mPaint.setColor(0xffffffff);
        canvas.drawPath(mPath, mPaint);
        canvas.save();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffffffff);
        canvas.drawCircle(bWidth / 2, bWidth / 2, bRadius, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xffdddddd);
        mPaint.setStrokeWidth(bStrokWidth);
        canvas.drawCircle(bWidth / 2, bWidth / 2, bRadius, mPaint);
        canvas.restore();
        bTranslateX=sWidth-bWidth;
        final float translate = bTranslateX*(isOn?1-sAnim:sAnim);
        canvas.translate(translate,0);
        mPaint.reset();
        if(sAnim>0)
            invalidate();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                sAnim=1;
                isOn=!isOn;
                invalidate();
                break;


        }
        return super.onTouchEvent(event);
    }
}
