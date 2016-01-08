package com.example.administrator.viewlearn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lt on 2016/1/7.
 */
public class MyImageView extends ImageView{

    private static final int SIZE_DEFAULT = 30;
    private static final float SIZE_DEFAULT_RATIO =1.8f;
    private float mWidth;
    private float mHeight;
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec)
    {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int minwidth = (int)(SIZE_DEFAULT * SIZE_DEFAULT_RATIO);
        int measurewidth;
        if(widthMode == MeasureSpec.EXACTLY)
        {
            measurewidth = Math.max(minwidth,widthSize);
        }
        else
        {
            measurewidth=minwidth;
            if(widthMode==MeasureSpec.AT_MOST)
            {
                measurewidth=Math.min(minwidth,widthSize);
            }
        }
        mWidth=measurewidth;
        return measurewidth;
    }

    private int measureHeight(int heightMeasureSpec)
    {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureheight;
        int minheight = (int)(SIZE_DEFAULT*SIZE_DEFAULT_RATIO);
        if(heightMode==MeasureSpec.EXACTLY)
        {
            measureheight=Math.max(minheight,heightSize);
        }
        else
        {
            measureheight=minheight;
            if(heightMode==MeasureSpec.AT_MOST)
            {
                measureheight=Math.min(minheight,heightSize);
            }
        }
        mHeight=measureheight;
        return measureheight;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        canvas.drawCircle(mWidth,mHeight,5,paint);
    }
}
