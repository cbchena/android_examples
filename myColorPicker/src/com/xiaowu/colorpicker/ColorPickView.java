package com.xiaowu.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickView extends View {
	private Context context;
	private int bigCircle; // 外圈半径
	private int rudeRadius; // 可移动小球的半径
	private int centerColor; // 可移动小球的颜色
	private Bitmap bitmapBack; // 背景图片
	private Paint mPaint; // 背景画笔
	private Paint mCenterPaint; // 可移动小球画笔
	private Point centerPoint;// 中心位置
	private Point mRockPosition;// 小球当前位置
	private OnColorChangedListener listener; // 小球移动的监听
	private int length; // 小球到中心位置的距离

	public ColorPickView(Context context) {
		super(context);
	}

	public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init(attrs);
	}

	public ColorPickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.listener = listener;
	}

	/**
	 * @describe 初始化操作
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

		// 获取自定义组件的属性
		TypedArray types = context.obtainStyledAttributes(attrs,
				R.styleable.color_picker);
		try {
			bigCircle = types.getDimensionPixelOffset(
					R.styleable.color_picker_circle_radius, 100);
			rudeRadius = types.getDimensionPixelOffset(
					R.styleable.color_picker_center_radius, 3);
			centerColor = types.getColor(R.styleable.color_picker_center_color,
					Color.WHITE);
		} finally {
			types.recycle(); // TypeArray用完需要recycle
		}

		// 将背景图片大小设置为属性设置的直径
		bitmapBack = BitmapFactory.decodeResource(getResources(),
				R.drawable.piccolor);
		bitmapBack = Bitmap.createScaledBitmap(bitmapBack, bigCircle * 2,
				bigCircle * 2, false);

		// 中心位置坐标
		centerPoint = new Point(bigCircle, bigCircle);
		mRockPosition = new Point(centerPoint);

		// 初始化背景画笔和可移动小球的画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mCenterPaint = new Paint();
		mCenterPaint.setColor(centerColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 画背景图片
		canvas.drawBitmap(bitmapBack, 0, 0, mPaint);

		// 画中心小球
		canvas.drawCircle(mRockPosition.x, mRockPosition.y, rudeRadius,
				mCenterPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下
            length = getLength(event.getX(), event.getY(), centerPoint.x,
                    centerPoint.y);
            if (length <= bigCircle - rudeRadius) {
                mRockPosition.set((int) event.getX(), (int) event.getY());
            } else {
                mRockPosition = getBorderPoint(centerPoint, new Point(
                        (int) event.getX(), (int) event.getY()), bigCircle
                        - rudeRadius);
            }

            listener.onColorChange(bitmapBack.getPixel(mRockPosition.x,
                    mRockPosition.y));

			break;
		case MotionEvent.ACTION_MOVE: // 移动
			length = getLength(event.getX(), event.getY(), centerPoint.x,
					centerPoint.y);
			if (length <= bigCircle - rudeRadius) {
				mRockPosition.set((int) event.getX(), (int) event.getY());
			} else {
				mRockPosition = getBorderPoint(centerPoint, new Point(
						(int) event.getX(), (int) event.getY()), bigCircle
						- rudeRadius);
			}

			listener.onColorChange(bitmapBack.getPixel(mRockPosition.x,
					mRockPosition.y));
			break;
		case MotionEvent.ACTION_UP:// 抬起

			break;

		default:
			break;
		}

		invalidate(); // 更新画布
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 视图大小设置为直径
		setMeasuredDimension(bigCircle * 2, bigCircle * 2);
	}

	/**
	 * @describe 计算两点之间的位置
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int getLength(float x1, float y1, float x2, float y2) {
		return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * @describe 当触摸点超出圆的范围的时候，设置小球边缘位置
	 * @param a
	 * @param b
	 * @param cutRadius
	 * @return
	 */
	public static Point getBorderPoint(Point a, Point b, int cutRadius) {
		float radian = getRadian(a, b);
		return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.x
				+ (int) (cutRadius * Math.sin(radian)));
	}

	/**
	 * @describe 触摸点与中心点之间直线与水平方向的夹角角度
	 * @param a
	 * @param b
	 * @return
	 */
	public static float getRadian(Point a, Point b) {
		float lenA = b.x - a.x;
		float lenB = b.y - a.y;
		float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
		float ang = (float) Math.acos(lenA / lenC);
		ang = ang * (b.y < a.y ? -1 : 1);
		return ang;
	}

	// 颜色发生变化的回调接口
	public interface OnColorChangedListener {
		void onColorChange(int color);
	}
}
