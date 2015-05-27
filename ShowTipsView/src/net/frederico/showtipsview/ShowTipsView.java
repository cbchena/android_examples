package net.frederico.showtipsview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 新手引导提示 2015/4/29 17:03
 */
public class ShowTipsView extends RelativeLayout {
	private Point showhintPoints; // 原点
	private int radius = 0; // 半径

	private String title, description; // 标题，描述
	private boolean custom, displayOneTime;
	private int displayOneTimeID = 0;
	private int delay = 0; // 延迟时间

	private ShowTipsViewInterface callback; // 回调函数
    private Handler _handler; // 点击在圆圈的区域时，发送的事件 2015/4/29 17:04

    /**
     * 点击圆圈区域的回调处理器 2015/4/30 11:25
     * @param _handler 处理器
     */
    public void set_handler(Handler _handler) {
        this._handler = _handler;
    }

	private View targetView;
	private int screenX, screenY;

	private int title_color, description_color, background_color, circleColor;

	private StoreUtils showTipsStore;

	public ShowTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ShowTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShowTipsView(Context context) {
		super(context);
		init();
	}

    /**
     * 初始化 2015/4/30 11:20
     */
	private void init() {
		this.setVisibility(View.GONE);
		this.setBackgroundColor(Color.TRANSPARENT);

        /**
         * 点击事件 2015/4/30 11:20
         */
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// DO NOTHING
				// HACK TO BLOCK CLICKS

			}
		});

        /**
         * 区域判断 2015/4/30 11:20
         */
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:

                        // 进行点击区域判断
                        float tx = motionEvent.getX();
                        float ty = motionEvent.getY();
                        if (_handler != null && _whichCircle(tx, ty)) { // 执行事件
                            _handler.sendEmptyMessage(0);
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        break;
                }

                return true;
            }
        });

		showTipsStore = new StoreUtils(getContext());
	}

    /**
     * 判断点击事件 2015/4/29 16:58
     * @param x 坐标原点 x
     * @param y 坐标原点 y
     * @return 是否点击正确
     */
    private boolean _whichCircle(float x, float y) {

        boolean isClick = false;

        // 将屏幕中的点转换成以屏幕中心为原点的坐标点
        float mx = x - showhintPoints.x;
        float my = y - showhintPoints.y;
        float result = mx * mx + my * my;

        // 高中的解析几何
        if(result <= radius * radius) {// 点击的点在小圆内
            isClick = true;
        }

        return isClick;
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Get screen dimensions
		screenX = w;
		screenY = h;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/*
		 * Draw circle and transparency background
		 */

		Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas temp = new Canvas(bitmap);
		Paint paint = new Paint();
		if (background_color != 0)
			paint.setColor(background_color);
		else
			paint.setColor(Color.parseColor("#000000"));

		paint.setAlpha(170);
		temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), paint);

		Paint transparentPaint = new Paint();
		transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
		transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // 画透明圆圈
		int x = showhintPoints.x;
		int y = showhintPoints.y;
		temp.drawCircle(x, y, radius, transparentPaint);

		canvas.drawBitmap(bitmap, 0, 0, new Paint());

        // 设置圆圈边框颜色 2015/4/30 11:24
        Paint circleline = new Paint();
		circleline.setStyle(Paint.Style.STROKE);
		if (circleColor != 0)
			circleline.setColor(circleColor);
		else
			circleline.setColor(Color.RED);

		circleline.setAntiAlias(true);
		circleline.setStrokeWidth(3);
		canvas.drawCircle(x, y, radius, circleline);
	}

	boolean isMeasured;

    /**
     * 显示提示 2015/4/30 11:22
     * @param activity
     */
	public void show(final Activity activity) {
		if (isDisplayOneTime() && showTipsStore.hasShown(getDisplayOneTimeID())) {
			setVisibility(View.GONE);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTipsView.this);
			return;
		} else {
			if (isDisplayOneTime())
				showTipsStore.storeShownId(getDisplayOneTimeID());
		}

        ((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				ShowTipsView.this.startAnimation(fadeInAnimation);

				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						if (isMeasured)
							return;

						if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
							isMeasured = true;

						}

						if (custom == false) {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + targetView.getWidth() / 2;
							int y = location[1] + targetView.getHeight() / 2;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							radius = targetView.getWidth() / 2;
                        } else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + showhintPoints.x;
							int y = location[1] + showhintPoints.y;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;

						}

						invalidate();

						createViews();

					}
				});
			}
		}, getDelay());
	}

	/*
	 * Create text views and close button
	 */
	private void createViews() {
		this.removeAllViews();

		RelativeLayout texts_layout = new RelativeLayout(getContext());

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		/*
		 * Title
		 */
		TextView textTitle = new TextView(getContext());
		textTitle.setText(getTitle());
		if (getTitle_color() != 0)
			textTitle.setTextColor(getTitle_color());
		else
			textTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
		textTitle.setId(123);
		textTitle.setTextSize(26);

		// Add title to this view
		texts_layout.addView(textTitle);

		/*
		 * Description
		 */
		TextView text = new TextView(getContext());
		text.setText(getDescription());
		if (getDescription_color() != 0)
			text.setTextColor(getDescription_color());
		else
			text.setTextColor(Color.WHITE);
		text.setTextSize(17);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 123);
		text.setLayoutParams(params);

		texts_layout.addView(text);

		LayoutParams paramsTexts = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		if (screenY / 2 > showhintPoints.y) {
			// textBlock under the highlight circle
			paramsTexts.height = (showhintPoints.y + radius) - screenY;
			paramsTexts.topMargin = (showhintPoints.y + radius);
			texts_layout.setGravity(Gravity.START | Gravity.TOP);

			texts_layout.setPadding(50, 50, 50, 50);
		} else {
			// textBlock above the highlight circle
			paramsTexts.height = showhintPoints.y - radius;

			texts_layout.setGravity(Gravity.START | Gravity.BOTTOM);

			texts_layout.setPadding(50, 100, 50, 50);
		}

		texts_layout.setLayoutParams(paramsTexts);
		this.addView(texts_layout);

		/*
		 * Close button
		 */
//        addCloseBtn(true);
	}

    /**
     * 添加关闭按钮 2015/1/29 15:34
     * @param isDefault 是否使用系统默认
     */
    public void addCloseBtn(boolean isDefault) {
        if (isDefault) { // 是否使用系统默认按钮
            Button btn_close = new Button(getContext());
            btn_close.setId(4375);
            btn_close.setText("跳过");
            btn_close.setTextColor(Color.WHITE);
            btn_close.setTextSize(17);
            btn_close.setGravity(Gravity.CENTER);

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.rightMargin = 50;
            params.bottomMargin = 50;

            btn_close.setLayoutParams(params);
            btn_close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (getCallback() != null)
                        getCallback().gotItClicked();

                    setVisibility(View.GONE);
                    removeParent();
                }
            });

            this.addView(btn_close);
        } else { // 否则使用自定义按钮，后面可扩展 2015/1/29 15:34

        }
    }

    public void removeParent() {
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
                .removeView(ShowTipsView.this);
    }

	public void setTarget(View v) {
		targetView = v;
	}

    /**
     * 设置提示目标 2015/4/30 11:22
     * @param v 提示视图
     * @param x x
     * @param y y
     * @param radius 半径
     */
	public void setTarget(View v, int x, int y, int radius) {
		custom = true;
		targetView = v;
		Point p = new Point(x, y);
		showhintPoints = p;
		this.radius = radius;
	}

	static Point getShowcasePointFromView(View view) {
		Point result = new Point();
		result.x = view.getLeft() + view.getWidth() / 2;
		result.y = view.getTop() + view.getHeight() / 2;
		return result;
	}

    /**
     * 设置标题 2015/4/30 11:22
     * @param title 标题
     */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

    /**
     * 设置描述 2015/4/30 11:23
     * @param description 描述内容
     */
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisplayOneTime() {
		return displayOneTime;
	}

	public void setDisplayOneTime(boolean displayOneTime) {
		this.displayOneTime = displayOneTime;
	}

	public ShowTipsViewInterface getCallback() {
		return callback;
	}

    /**
     * 设置回调函数 2015/4/30 11:23
     * @param callback 回调
     */
	public void setCallback(ShowTipsViewInterface callback) {
		this.callback = callback;
	}

	public int getDelay() {
		return delay;
	}

    /**
     * 设置延迟时间 2015/4/30 11:23
     * @param delay 时间
     */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDisplayOneTimeID() {
		return displayOneTimeID;
	}

	public void setDisplayOneTimeID(int displayOneTimeID) {
		this.displayOneTimeID = displayOneTimeID;
	}

	public int getTitle_color() {
		return title_color;
	}

	public void setTitle_color(int title_color) {
		this.title_color = title_color;
	}

	public int getDescription_color() {
		return description_color;
	}

	public void setDescription_color(int description_color) {
		this.description_color = description_color;
	}

	public int getBackground_color() {
		return background_color;
	}

    /**
     * 设置背景颜色 2015/4/30 11:23
     * @param background_color 背景颜色
     */
	public void setBackground_color(int background_color) {
		this.background_color = background_color;
	}

	public int getCircleColor() {
		return circleColor;
	}

    /**
     * 设置圆圈的边框颜色 2015/4/30 11:24
     * @param circleColor 颜色
     */
	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

}
