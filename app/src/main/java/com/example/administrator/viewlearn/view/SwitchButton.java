package com.example.administrator.viewlearn.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;

import com.example.administrator.viewlearn.R;


/**
 * SwitchButton
 *
 * @author kyleduo
 * @version 1.3.2
 * @since 2014-09-24
 */

public class SwitchButton extends CompoundButton {
	//背景测量默认比例
	public static final float DEFAULT_BACK_MEASURE_RATIO = 1.8f;
	//滑动球大小
	public static final int DEFAULT_THUMB_SIZE_DP = 30;
	//滑动球与滑动区域边缘的距离
	public static final int DEFAULT_THUMB_MARGIN_DP = 1;
	//滑动速度
	public static final int DEFAULT_ANIMATION_DURATION = 100;
	//默认开启按钮背景颜色
	public static final int DEFAULT_TINT_COLOR = 0x327FC2;
	//滑动触摸状态
	private static int[] CHECKED_PRESSED_STATE = new int[]{android.R.attr.state_checked, android.R.attr.state_enabled, android.R.attr.state_pressed};
	private static int[] UNCHECKED_PRESSED_STATE = new int[]{-android.R.attr.state_checked, android.R.attr.state_enabled, android.R.attr.state_pressed};
    //滑动球和背景自定义资源
	private Drawable mThumbDrawable, mBackDrawable;
	//滑动球和背景自定义颜色
	private ColorStateList mBackColor, mThumbColor;
	//滑动球和背景半径
	private float mThumbRadius, mBackRadius;
	//滑动球边缘矩形变量
	private RectF mThumbMargin;
	//自定义背景测量比例
	private float mBackMeasureRatio;
	//自定义背景滑动速度
	private long mAnimationDuration;
	// fade back drawable or color when dragging or animating
	private boolean mFadeBack;
	//checked开启时自定义的颜色
	private int mTintColor;
	//滑动球自定义尺寸
	private PointF mThumbSizeF;
	//当前显示的颜色
	private int mCurrThumbColor, mCurrBackColor,mCurrBackColor1, mNextBackColor;
	private Drawable mCurrentBackDrawable, mNextBackDrawable;
	//自定义矩形
	private RectF mThumbRectF, mBackRectF,mBackRectF1, mSafeRectF;
	//画笔变量
	private Paint mPaint,mPaint1;
	// whether using Drawable for thumb or back
	//是否使用drawable
	private boolean mIsThumbUseDrawable, mIsBackUseDrawable;
	private boolean mDrawDebugRect = false;
	//动画设置对象
	private ObjectAnimator mProcessAnimator;
	// animation control
	private float mProcess;
	// temp position of thumb when dragging or animating
	//显示的滑动球矩形变量
	private RectF mPresentThumbRectF;
	//坐标值
	private float mStartX, mStartY, mLastX;
	//触发移动的最小距离
	private int mTouchSlop;
	//点击超时
	private int mClickTimeout;
	//矩形画笔变量
	private Paint mRectPaint;

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public SwitchButton(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		// ViewConfiguration.getPressedStateDuration() 获得的是按下效果显示的时间,由PRESSED_STATE_DURATION常量指定,为64毫秒.
		//getTapTimeout定义一个touch事件中是点击事件还是一个滑动事件所需的时间，如果用户在这个时间之内滑动，那么就认为是一个点击事件
		mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();

		//ANTI_ALIAS_FLAG为抗锯齿
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//让画出的图形是空心的
		mRectPaint.setStyle(Paint.Style.STROKE);
		//设置画出的线的 粗细程度
		mRectPaint.setStrokeWidth(getResources().getDisplayMetrics().density);

		mThumbRectF = new RectF();
		mBackRectF = new RectF();
		mBackRectF1 = new RectF();
		mSafeRectF = new RectF();
		mThumbSizeF = new PointF();
		mThumbMargin = new RectF();

		//设置动画属性
		mProcessAnimator = ObjectAnimator.ofFloat(this, "process", 0, 0).setDuration(DEFAULT_ANIMATION_DURATION);
		mProcessAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

		mPresentThumbRectF = new RectF();

		Resources res = getResources();
		//获取手机屏幕参数,后面的density就是屏幕的密度
		float density = res.getDisplayMetrics().density;

		Drawable thumbDrawable = null;
		ColorStateList thumbColor = null;

		float margin = density * DEFAULT_THUMB_MARGIN_DP;
		float marginLeft = 0;
		float marginRight = 0;
		float marginTop = 0;
		float marginBottom = 0;
		//滑动球默认大小
		float thumbWidth = density * DEFAULT_THUMB_SIZE_DP;
		float thumbHeight = density * DEFAULT_THUMB_SIZE_DP;
		//半径
		float thumbRadius = density * DEFAULT_THUMB_SIZE_DP / 2;
		float backRadius = thumbRadius;
		Drawable backDrawable = null;
		ColorStateList backColor = null;
		float backMeasureRatio = DEFAULT_BACK_MEASURE_RATIO;
		int animationDuration = DEFAULT_ANIMATION_DURATION;
		boolean fadeBack = true;
		int tintColor = Integer.MIN_VALUE;

		TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
		if (ta != null) {
			thumbDrawable = ta.getDrawable(R.styleable.SwitchButton_kswThumbDrawable);
			thumbColor = ta.getColorStateList(R.styleable.SwitchButton_kswThumbColor);
			margin = ta.getDimension(R.styleable.SwitchButton_kswThumbMargin, margin);
			marginLeft = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginLeft, margin);
			marginRight = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginRight, margin);
			marginTop = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginTop, margin);
			marginBottom = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginBottom, margin);
			thumbWidth = ta.getDimension(R.styleable.SwitchButton_kswThumbWidth, thumbWidth);
			thumbHeight = ta.getDimension(R.styleable.SwitchButton_kswThumbHeight, thumbHeight);
			thumbRadius = ta.getDimension(R.styleable.SwitchButton_kswThumbRadius, Math.min(thumbWidth, thumbHeight) / 2.f);
			backRadius = ta.getDimension(R.styleable.SwitchButton_kswBackRadius, thumbRadius + density * 2f);
			backDrawable = ta.getDrawable(R.styleable.SwitchButton_kswBackDrawable);
			backColor = ta.getColorStateList(R.styleable.SwitchButton_kswBackColor);
			backMeasureRatio = ta.getFloat(R.styleable.SwitchButton_kswBackMeasureRatio, backMeasureRatio);
			animationDuration = ta.getInteger(R.styleable.SwitchButton_kswAnimationDuration, animationDuration);
			fadeBack = ta.getBoolean(R.styleable.SwitchButton_kswFadeBack, true);
			tintColor = ta.getColor(R.styleable.SwitchButton_kswTintColor, tintColor);
			ta.recycle();
		}

		// thumb drawable and color
		mThumbDrawable = thumbDrawable;
		mThumbColor = thumbColor;
		mIsThumbUseDrawable = mThumbDrawable != null;
		mTintColor = tintColor;
		if (mTintColor == Integer.MIN_VALUE) {
			mTintColor = DEFAULT_TINT_COLOR;
		}
		if (!mIsThumbUseDrawable && mThumbColor == null) {
			mThumbColor = ColorUtils.generateThumbColorWithTintColor(mTintColor);
			mCurrThumbColor = mThumbColor.getDefaultColor();
		}
		mThumbSizeF.set(thumbWidth, thumbHeight);

		// back drawable and color
		mBackDrawable = backDrawable;
		mBackColor = backColor;
		mIsBackUseDrawable = mBackDrawable != null;
		if (!mIsBackUseDrawable && mBackColor == null) {
			mBackColor = ColorUtils.generateBackColorWithTintColor(mTintColor);
			mCurrBackColor = mBackColor.getDefaultColor();
			mNextBackColor = mBackColor.getColorForState(CHECKED_PRESSED_STATE, mCurrBackColor);
		}

		// margin
		mThumbMargin.set(marginLeft, marginTop, marginRight, marginBottom);

		// size & measure params must larger than 1
		mBackMeasureRatio = mThumbMargin.width() >= 0 ? Math.max(backMeasureRatio, 1) : backMeasureRatio;

		mThumbRadius = thumbRadius;
		mBackRadius = backRadius;
		mAnimationDuration = animationDuration;
		mFadeBack = fadeBack;

		mProcessAnimator.setDuration(mAnimationDuration);

		setFocusable(true);
		setClickable(true);

		// sync checked status
		if (isChecked()) {
			setProcess(1);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int widthMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int measuredWidth;

		int minWidth = (int) (mThumbSizeF.x * mBackMeasureRatio);
		if (mIsBackUseDrawable) {
			minWidth = Math.max(minWidth, mBackDrawable.getMinimumWidth());
		}
		minWidth = Math.max(minWidth, (int) (minWidth + mThumbMargin.left + mThumbMargin.right));
		minWidth = Math.max(minWidth, minWidth + getPaddingLeft() + getPaddingRight());
		//需要确保其高度和宽度至少达到View所规定的最小值（可通过getSuggestedMinimumHeight()和getSuggestedMinimumWidth()得到）
		minWidth = Math.max(minWidth, getSuggestedMinimumWidth());

		if (widthMode == MeasureSpec.EXACTLY) {
			measuredWidth = Math.max(minWidth, widthSize);
		} else {
			measuredWidth = minWidth;
			if (widthMode == MeasureSpec.AT_MOST) {
				measuredWidth = Math.min(measuredWidth, widthSize);
			}
		}

		return measuredWidth;
	}

	private int measureHeight(int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int measuredHeight;

		int minHeight = (int) Math.max(mThumbSizeF.y, mThumbSizeF.y + mThumbMargin.top + mThumbMargin.right);
		//需要确保其高度和宽度至少达到View所规定的最小值（可通过getSuggestedMinimumHeight()和getSuggestedMinimumWidth()得到）
		minHeight = Math.max(minHeight, getSuggestedMinimumHeight());
		minHeight = Math.max(minHeight, minHeight + getPaddingTop() + getPaddingBottom());

		if (heightMode == MeasureSpec.EXACTLY) {
			measuredHeight = Math.max(minHeight, heightSize);
		} else {
			measuredHeight = minHeight;
			if (heightMode == MeasureSpec.AT_MOST) {
				measuredHeight = Math.min(measuredHeight, heightSize);
			}
		}

		return measuredHeight;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			setup();
		}
	}

	/**
	 * set up the rect of back and thumb
	 */
	private void setup() {
		float thumbTop = getPaddingTop() + Math.max(0, mThumbMargin.top);
		float thumbLeft = getPaddingLeft() + Math.max(0, mThumbMargin.left);

		if (mIsThumbUseDrawable) {
			mThumbSizeF.x = Math.max(mThumbSizeF.x, mThumbDrawable.getMinimumWidth());
			mThumbSizeF.y = Math.max(mThumbSizeF.y, mThumbDrawable.getMinimumHeight());
		}

		mThumbRectF.set(thumbLeft, thumbTop, thumbLeft + mThumbSizeF.x, thumbTop + mThumbSizeF.y);

		float backLeft = mThumbRectF.left - mThumbMargin.left;
		mBackRectF.set(backLeft, mThumbRectF.top - mThumbMargin.top, backLeft + mThumbMargin.left + mThumbSizeF.x * mBackMeasureRatio + mThumbMargin.right, mThumbRectF.bottom + mThumbMargin.bottom);
		mBackRectF1.set(backLeft+5, (mThumbRectF.top - mThumbMargin.top)+5, (backLeft + mThumbMargin.left + mThumbSizeF.x * mBackMeasureRatio + mThumbMargin.right)-5, (mThumbRectF.bottom + mThumbMargin.bottom)-5);
		mSafeRectF.set(mThumbRectF.left, 0, mBackRectF.right - mThumbMargin.right - mThumbRectF.width(), 0);

		float minBackRadius = Math.min(mBackRectF.width(), mBackRectF.height()) / 2.f;
		mBackRadius = Math.min(minBackRadius, mBackRadius);

		if (mBackDrawable != null) {
			mBackDrawable.setBounds((int) mBackRectF.left, (int) mBackRectF.top, (int) mBackRectF.right, (int) mBackRectF.bottom);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// fade back
		if (mIsBackUseDrawable) {
			if (mFadeBack && mCurrentBackDrawable != null && mNextBackDrawable != null) {
				int alpha = (int) (255 * (isChecked() ? getProcess() : (1 - getProcess())));
				mCurrentBackDrawable.setAlpha(alpha);
				mCurrentBackDrawable.draw(canvas);
				alpha = 255 - alpha;
				mNextBackDrawable.setAlpha(alpha);
				mNextBackDrawable.draw(canvas);
			} else {
				mBackDrawable.setAlpha(255);
				mBackDrawable.draw(canvas);
			}
		} else {
			if (mFadeBack) {
				int alpha;
				int colorAlpha;

				// curr back
				alpha = (int) (255 * (isChecked() ? getProcess() : (1 - getProcess())));
				colorAlpha = Color.alpha(mCurrBackColor);
				colorAlpha = colorAlpha * alpha / 255;
				mPaint.setARGB(colorAlpha, Color.red(mCurrBackColor), Color.green(mCurrBackColor), Color.blue(mCurrBackColor));
				canvas.drawRoundRect(mBackRectF, mBackRadius, mBackRadius, mPaint);
				if(!isChecked()) {
					mCurrBackColor1=0xedebeb;
					mPaint1.setARGB(colorAlpha, Color.red(mCurrBackColor1), Color.green(mCurrBackColor1), Color.blue(mCurrBackColor1));
					canvas.drawRoundRect(mBackRectF1, mBackRadius - 5, mBackRadius - 5, mPaint1);
				}

				// next back
				alpha = 255 - alpha;
				colorAlpha = Color.alpha(mNextBackColor);
				colorAlpha = colorAlpha * alpha / 255;
				mPaint.setARGB(colorAlpha, Color.red(mNextBackColor), Color.green(mNextBackColor), Color.blue(mNextBackColor));
				canvas.drawRoundRect(mBackRectF, mBackRadius, mBackRadius, mPaint);

				mPaint.setAlpha(255);
			} else {
				mPaint.setColor(mCurrBackColor);
				canvas.drawRoundRect(mBackRectF, mBackRadius, mBackRadius, mPaint);
			}
		}

		// thumb
		mPresentThumbRectF.set(mThumbRectF);
		mPresentThumbRectF.offset(mProcess * mSafeRectF.width(), 0);
		if (mIsThumbUseDrawable) {
			mThumbDrawable.setBounds((int) mPresentThumbRectF.left, (int) mPresentThumbRectF.top, (int) mPresentThumbRectF.right, (int) mPresentThumbRectF.bottom);
			mThumbDrawable.draw(canvas);
		} else {
			mPaint.setColor(mCurrThumbColor);
			canvas.drawRoundRect(mPresentThumbRectF, mThumbRadius, mThumbRadius, mPaint);
		}

		if (mDrawDebugRect) {
			mRectPaint.setColor(Color.parseColor("#AA0000"));
			canvas.drawRect(mBackRectF, mRectPaint);
			mRectPaint.setColor(Color.parseColor("#0000FF"));
			canvas.drawRect(mPresentThumbRectF, mRectPaint);
		}
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		if (!mIsThumbUseDrawable && mThumbColor != null) {
			mCurrThumbColor = mThumbColor.getColorForState(getDrawableState(), mCurrThumbColor);

		} else {
			setDrawableState(mThumbDrawable);
		}

		int[] nextState = isChecked() ? UNCHECKED_PRESSED_STATE : CHECKED_PRESSED_STATE;
		if (!mIsBackUseDrawable && mBackColor != null) {
			mCurrBackColor = mBackColor.getColorForState(getDrawableState(), mCurrBackColor);
			mNextBackColor = mBackColor.getColorForState(nextState, mCurrBackColor);
		} else {
			if (mBackDrawable instanceof StateListDrawable && mFadeBack) {
				mBackDrawable.setState(nextState);
				mNextBackDrawable = mBackDrawable.getCurrent().mutate();
			} else {
				mNextBackDrawable = null;
			}
			setDrawableState(mBackDrawable);
			if (mBackDrawable != null) {
				mCurrentBackDrawable = mBackDrawable.getCurrent().mutate();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled() || !isClickable()) {
			return false;
		}

		int action = event.getAction();

		float deltaX = event.getX() - mStartX;
		float deltaY = event.getY() - mStartY;

		// status the view going to change to when finger released
		boolean nextStatus;

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				catchView();
				mStartX = event.getX();
				mStartY = event.getY();
				mLastX = mStartX;
				setPressed(true);
				break;

			case MotionEvent.ACTION_MOVE:
				float x = event.getX();
				setProcess(getProcess() + (x - mLastX) / mSafeRectF.width());
				mLastX = x;
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				setPressed(false);
				nextStatus = getStatusBasedOnPos();
				float time = event.getEventTime() - event.getDownTime();
				if (deltaX < mTouchSlop && deltaY < mTouchSlop && time < mClickTimeout) {
					performClick();
				} else {
					if (nextStatus != isChecked()) {
						playSoundEffect(SoundEffectConstants.CLICK);
						setChecked(nextStatus);
					} else {
						animateToState(nextStatus);
					}
				}
				break;

			default:
				break;
		}
		return true;
	}


	/**
	 * return the status based on position of thumb
	 *
	 * @return
	 */
	private boolean getStatusBasedOnPos() {
		return getProcess() > 0.5f;
	}

	public final float getProcess() {
		return mProcess;
	}

	public final void setProcess(final float process) {
		float tp = process;
		if (tp > 1) {
			tp = 1;
		} else if (tp < 0) {
			tp = 0;
		}
		this.mProcess = tp;
		invalidate();
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	/**
	 * processing animation
	 *
	 * @param checked checked or unChecked
	 */
	protected void animateToState(boolean checked) {
		if (mProcessAnimator == null) {
			return;
		}
		if (mProcessAnimator.isRunning()) {
			mProcessAnimator.cancel();
		}
		mProcessAnimator.setDuration(mAnimationDuration);
		if (checked) {
			mProcessAnimator.setFloatValues(mProcess, 1f);
		} else {
			mProcessAnimator.setFloatValues(mProcess, 0);
		}
		mProcessAnimator.start();
	}

	private void catchView() {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public void setChecked(final boolean checked) {
		super.setChecked(checked);
		animateToState(checked);
	}

	public void setCheckedImmediately(boolean checked) {
		super.setChecked(checked);
		if (mProcessAnimator != null && mProcessAnimator.isRunning()) {
			mProcessAnimator.cancel();
		}
		setProcess(checked ? 1 : 0);
		invalidate();
	}

	public void toggleImmediately() {
		setCheckedImmediately(!isChecked());
	}

	private void setDrawableState(Drawable drawable) {
		if (drawable != null) {
			int[] myDrawableState = getDrawableState();
			drawable.setState(myDrawableState);
			invalidate();
		}
	}

	public boolean isDrawDebugRect() {
		return mDrawDebugRect;
	}

	public void setDrawDebugRect(boolean drawDebugRect) {
		mDrawDebugRect = drawDebugRect;
		invalidate();
	}

	public long getAnimationDuration() {
		return mAnimationDuration;
	}

	public void setAnimationDuration(long animationDuration) {
		mAnimationDuration = animationDuration;
	}

	public Drawable getThumbDrawable() {
		return mThumbDrawable;
	}

	public void setThumbDrawable(Drawable thumbDrawable) {
		mThumbDrawable = thumbDrawable;
		mIsThumbUseDrawable = mThumbDrawable != null;
		setup();
		refreshDrawableState();
		requestLayout();
		invalidate();
	}

	public void setThumbDrawableRes(int thumbDrawableRes) {
		setThumbDrawable(ContextCompat.getDrawable(getContext(), thumbDrawableRes));
	}

	public Drawable getBackDrawable() {
		return mBackDrawable;
	}

	public void setBackDrawable(Drawable backDrawable) {
		mBackDrawable = backDrawable;
		mIsBackUseDrawable = mBackDrawable != null;
		setup();
		refreshDrawableState();
		requestLayout();
		invalidate();
	}

	public void setBackDrawableRes(int backDrawableRes) {
		setBackDrawable(ContextCompat.getDrawable(getContext(), backDrawableRes));
	}

	public ColorStateList getBackColor() {
		return mBackColor;
	}

	public void setBackColor(ColorStateList backColor) {
		mBackColor = backColor;
		if (mBackColor != null) {
			setBackDrawable(null);
		}
		invalidate();
	}

	public void setBackColorRes(int backColorRes) {
//		setBackColor(ContextCompat.getColorStateList(getContext(), backColorRes));
		setBackColor(getResources().getColorStateList(backColorRes));
	}

	public ColorStateList getThumbColor() {
		return mThumbColor;
	}

	public void setThumbColor(ColorStateList thumbColor) {
		mThumbColor = thumbColor;
		if (mThumbColor != null) {
			setThumbDrawable(null);
		}
	}

	public void setThumbColorRes(int thumbColorRes) {
//		setThumbColor(ContextCompat.getColorStateList(getContext(), thumbColorRes));
		setBackColor(getResources().getColorStateList(thumbColorRes));
	}

	public float getBackMeasureRatio() {
		return mBackMeasureRatio;
	}

	public void setBackMeasureRatio(float backMeasureRatio) {
		mBackMeasureRatio = backMeasureRatio;
		requestLayout();
	}

	public RectF getThumbMargin() {
		return mThumbMargin;
	}

	public void setThumbMargin(RectF thumbMargin) {
		if (thumbMargin == null) {
			setThumbMargin(0, 0, 0, 0);
		} else {
			setThumbMargin(thumbMargin.left, thumbMargin.top, thumbMargin.right, thumbMargin.bottom);
		}
	}

	public void setThumbMargin(float left, float top, float right, float bottom) {
		mThumbMargin.set(left, top, right, bottom);
		requestLayout();
	}

	public void setThumbSize(float width, float height) {
		mThumbSizeF.set(width, height);
		setup();
		requestLayout();
	}

	public float getThumbWidth() {
		return mThumbSizeF.x;
	}

	public float getThumbHeight() {
		return mThumbSizeF.y;
	}

	public void setThumbSize(PointF size) {
		if (size == null) {
			float defaultSize = getResources().getDisplayMetrics().density * DEFAULT_THUMB_SIZE_DP;
			setThumbSize(defaultSize, defaultSize);
		} else {
			setThumbSize(size.x, size.y);
		}
	}

	public PointF getThumbSizeF() {
		return mThumbSizeF;
	}

	public float getThumbRadius() {
		return mThumbRadius;
	}

	public void setThumbRadius(float thumbRadius) {
		mThumbRadius = thumbRadius;
		if (!mIsThumbUseDrawable) {
			invalidate();
		}
	}

	public PointF getBackSizeF() {
		return new PointF(mBackRectF.width(), mBackRectF.height());
	}

	public float getBackRadius() {
		return mBackRadius;
	}

	public void setBackRadius(float backRadius) {
		mBackRadius = backRadius;
		if (!mIsBackUseDrawable) {
			invalidate();
		}
	}

	public boolean isFadeBack() {
		return mFadeBack;
	}

	public void setFadeBack(boolean fadeBack) {
		mFadeBack = fadeBack;
	}

	public int getTintColor() {
		return mTintColor;
	}

	public void setTintColor(int tintColor) {
		mTintColor = tintColor;
		mThumbColor = ColorUtils.generateThumbColorWithTintColor(mTintColor);
		mBackColor = ColorUtils.generateBackColorWithTintColor(mTintColor);
		mIsBackUseDrawable = false;
		mIsThumbUseDrawable = false;
		// call this method to refresh color states
		refreshDrawableState();
		invalidate();
	}



}