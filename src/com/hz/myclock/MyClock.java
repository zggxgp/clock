package com.hz.myclock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.hz.myclock.util.DisplayUtils;

public class MyClock extends View {

	private int width;
	private int height;

	private Context mContext;

	// 各个顶点
	private Point houEndPoint;
	private Point minEndPoint;
	private Point secEndPoint;
	private Point centerPoint;
	private List<HashMap<String, Point>> allPointList;
	private List<HashMap<String, Point>> nearPointList;
	private List<HashMap<String, Point>> selectPointList;
	private HashMap<String, Point> point;

	// 画笔
	private Paint houPaint;
	private Paint minPaint;
	private Paint secPaint;
	private Paint circlePaint;
	private Paint textPaint;

	private GestureDetector detector;
	private Calendar calendar;

	// 当前时分秒
	private int hour;
	private int min;
	private int sec;

	// 保持时间
	private int holdHour;
	private int holdMin;
	private int holdSec;

	// 时钟半径
	private int circleRadius = 100;
	private int secLength = (int) (circleRadius * 0.9);

	// int i = 0;

	// 长按事件所处象限标记
	private boolean oneFour = false;
	private boolean twoThree = false;

	// 设置时间模式标记
	private boolean setTimeMode = false;
	private boolean setTimeModePre = false;
	private boolean canDrag = false;

	// 自定义标志，如果为false表示为系统时间， true为自定时间。
	private boolean customTime = false;

	// 选中指针的对应画笔和点
	private Point selectPoint;
	private Paint selectPaint;
	private String selectHand;
	
	// 保存指针移动前的端点
	private int oldX;
	private int oldY;
	
	private ClockThread clockThead;
	
	// 手势监听器
	private MyListener mListener;

	public MyClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initObject();
		
		clockThead = new ClockThread();
		clockThead.start();
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);

		centerPoint.x = width / 2;
		centerPoint.y = height / 2;

		setMeasuredDimension(width, height);
		// System.out.println("Measure"+ width);
		// System.out.println(getWidth());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 准备指针的移动
		if (setTimeModePre || setTimeMode) {
			holdTime();
			drawText(canvas);
			drawCircle(canvas);

			for (String key : nearPointList.get(0).keySet()) {
				selectPoint = selectPointList.get(0).get(key);
				selectHand = key;
				if (key.equals("sec")) {
					drawHour(canvas);
					drawMin(canvas);

					secPaint.setAlpha(50);
					canvas.drawLine(centerPoint.x, centerPoint.y,
							selectPointList.get(0).get(key).x, selectPointList
									.get(0).get(key).y, secPaint);
					setTimeMode = true;
					setTimeModePre = false;
				}

				if (key.equals("min")) {
					drawHour(canvas);
					drawSec(canvas);
					minPaint.setAlpha(50);
					canvas.drawLine(centerPoint.x, centerPoint.y,
							selectPointList.get(0).get(key).x, selectPointList
									.get(0).get(key).y, minPaint);
					setTimeMode = true;
					setTimeModePre = false;
				}

				if (key.equals("hour")) {
					drawMin(canvas);
					drawSec(canvas);
					houPaint.setAlpha(50);
					canvas.drawLine(centerPoint.x, centerPoint.y,
							selectPointList.get(0).get(key).x, selectPointList
									.get(0).get(key).y, houPaint);
					setTimeMode = true;
					setTimeModePre = false;

				}

			}
			//System.out.println("开始准备重绘指针");
		} else {
			initTime();
			drawText(canvas);
			drawCircle(canvas);
			drawHour(canvas);
			drawMin(canvas);
			drawSec(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		if(setTimeMode==false&&setTimeMode==false){
			detector.onTouchEvent(event);
			System.out.println("detectorevent");
		}
		

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			if(isSelectedHand(event)&&setTimeMode){
		
				canDrag = true;
				System.out.println("action_down----------"+canDrag);
				clockThead.stopTH();
				}
			break;

		case MotionEvent.ACTION_MOVE:
			
			System.out.println(event.getX());
			System.out.println(event.getY());
			
			if (canDrag) {
				selectPoint.x = (int) event.getX();
				selectPoint.y = (int) event.getY();
				
				System.out.println("可以滑动指针了-------------");
			}
			break;

		case MotionEvent.ACTION_UP:
			
			System.out.println("ACTION_UP");
			break;
		default:
			break;
		}
		
		invalidate();
		
		return true;
	}

	/**
	 * 从calendar更新时间
	 */
	private void initTime() {
		calendar = Calendar.getInstance();
		holdHour = hour = calendar.get(Calendar.HOUR);
		holdMin = min = calendar.get(Calendar.MINUTE);
		holdSec = sec = calendar.get(Calendar.SECOND);

		// System.out.println("--------------" + hour);
		// System.out.println("--------------" + min);
		// System.out.println("--------------" + sec);

	}

	/**
	 * 准备重绘指针时保存当前时间。
	 */
	private void holdTime() {
		hour = holdHour;
		min = holdMin;
		sec = holdSec;
	}

	/**
	 * 初始化各种对象
	 */
	private void initObject() {
		centerPoint = new Point();
		houEndPoint = new Point();
		minEndPoint = new Point();
		secEndPoint = new Point();
		selectPoint = new Point();

		circlePaint = new Paint();
		houPaint = new Paint();
		secPaint = new Paint();
		minPaint = new Paint();
		textPaint = new Paint();
		selectPaint = new Paint();

		mListener = new MyListener();

		allPointList = new ArrayList<HashMap<String, Point>>();
		nearPointList = new ArrayList<HashMap<String, Point>>();
		selectPointList = new ArrayList<HashMap<String, Point>>();

		point = new HashMap<String, Point>();
		point.put("hour", houEndPoint);
		allPointList.add(point);

		point = new HashMap<String, Point>();
		point.put("min", minEndPoint);
		allPointList.add(point);

		point = new HashMap<String, Point>();
		point.put("sec", secEndPoint);
		allPointList.add(point);

		// 手势识别
		detector = new GestureDetector(mContext, mListener);

	}

	/**
	 * 判断点击长按事件是否在圆内
	 * 
	 * @param e
	 * @return
	 */
	private boolean isInCircle(MotionEvent e) {
		int distance = (int) (Math.pow(e.getX() - centerPoint.x, 2) + Math.pow(
				e.getY() - centerPoint.y, 2));
		int radiusDp = DisplayUtils.Dp2Px(mContext, circleRadius);
		int radiusPow = (int) Math.pow(radiusDp, 2);

		if (distance < radiusPow) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 画时钟的圆盘
	 * 
	 * @param canvas
	 */
	private void drawCircle(Canvas canvas) {

		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Style.STROKE);
		circlePaint.setAlpha(255);
		circlePaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 3));
		circlePaint.setAntiAlias(true);

		canvas.drawCircle(centerPoint.x, centerPoint.y,
				DisplayUtils.Dp2Px(mContext, circleRadius), circlePaint);
		canvas.drawPoint(centerPoint.x, centerPoint.y, circlePaint);
	}

	/**
	 * 画秒针
	 * 
	 * @param canvas
	 */
	private void drawSec(Canvas canvas) {
		secPaint.setColor(Color.GREEN);
		secPaint.setStyle(Style.STROKE);
		secPaint.setAlpha(255);
		secPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 4));
		secPaint.setAntiAlias(true);
		secEndPoint.x = (int) Math.round(Math
				.sin(-Math.PI - Math.PI / 30 * sec)
				* DisplayUtils.Dp2Px(mContext, secLength) + centerPoint.x);
		secEndPoint.y = (int) Math.round(Math
				.cos(-Math.PI - Math.PI / 30 * sec)
				* DisplayUtils.Dp2Px(mContext, secLength) + centerPoint.y);

		canvas.drawLine(centerPoint.x, centerPoint.y, secEndPoint.x,
				secEndPoint.y, secPaint);
	}

	/**
	 * 画分针
	 * 
	 * @param canvas
	 */
	private void drawMin(Canvas canvas) {

		minPaint.setColor(Color.RED);
		minPaint.setStyle(Style.STROKE);
		minPaint.setAlpha(255);
		minPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 6));
		minPaint.setAntiAlias(true);
		minEndPoint.x = (int) Math.round(Math
				.sin(-Math.PI - Math.PI / 30 * min)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.8f
				+ centerPoint.x);
		minEndPoint.y = (int) Math.round(Math
				.cos(-Math.PI - Math.PI / 30 * min)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.8f
				+ centerPoint.y);

		canvas.drawLine(centerPoint.x, centerPoint.y, minEndPoint.x,
				minEndPoint.y, minPaint);
	}

	/**
	 * 画时针
	 * 
	 * @param canvas
	 */
	private void drawHour(Canvas canvas) {

		houPaint.setColor(Color.BLACK);
		houPaint.setStyle(Style.STROKE);
		houPaint.setAlpha(255);
		houPaint.setAntiAlias(true);
		houPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 7));

		houEndPoint.x = (int) Math.round(Math
				.sin(-Math.PI - Math.PI / 6 * hour)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.6f
				+ centerPoint.x);
		houEndPoint.y = (int) Math.round(Math
				.cos(-Math.PI - Math.PI / 6 * hour)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.6f
				+ centerPoint.y);

		canvas.drawLine(centerPoint.x, centerPoint.y, houEndPoint.x,
				houEndPoint.y, houPaint);
	}

	/**
	 * 画时间标识点文字
	 * 
	 * @param canvas
	 */
	private void drawText(Canvas canvas) {
		textPaint.setColor(Color.BLACK);
		textPaint.setStyle(Style.STROKE);
		textPaint.setAlpha(255);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(30);

		canvas.drawText("30", centerPoint.x,
				centerPoint.y + (int) DisplayUtils.Dp2Px(mContext, secLength),
				textPaint);

		canvas.drawText("0", centerPoint.x,
				centerPoint.y - (int) DisplayUtils.Dp2Px(mContext, secLength),
				textPaint);

		canvas.drawText("15",
				centerPoint.x + DisplayUtils.Dp2Px(mContext, secLength - 5),
				centerPoint.y, textPaint);

		canvas.drawText("45",
				centerPoint.x - DisplayUtils.Dp2Px(mContext, secLength),
				centerPoint.y, textPaint);
	}

	private boolean isSelectedHand(MotionEvent e) {

		double cosLimit = 0.01;// cos阈值

		double cos;

		int distanceHand = (int) (Math.pow(selectPoint.x - centerPoint.x, 2) + Math
				.pow(selectPoint.y - centerPoint.y, 2));
		int distancePress = (int) (Math.pow(e.getX() - centerPoint.x, 2) + Math
				.pow(e.getY() - centerPoint.y, 2));
		int otherDisance = (int) (Math.pow(selectPoint.x - e.getX(), 2) + Math
				.pow(selectPoint.y - e.getY(), 2));

		
		System.out.println("distanceHand " + distanceHand);
		System.out.println("distancePress " + distancePress);
		System.out.println("otherDisance " + otherDisance);

		// 余弦公式计算夹角cos
		cos = (distanceHand + distancePress - otherDisance)
				/ (2 * Math.sqrt(distanceHand) * Math.sqrt(distancePress));
		
		System.out.println("------------cos"+cos);
		
		boolean result = 1-cos<=cosLimit;
		
		System.out.println("----------------------"+result);
		return result;
	}

	// 手势监听器
	private class MyListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

			// System.out.println("xxxxxx"+(e.getX()-centerPoint.x));
			// System.out.println("yyyyy"+(e.getY()-centerPoint.y));
			// System.out.println("distance"+distance);
			// System.out.println("radiusDp"+radiusDp);

			// 判断长按事件是否在时钟内部
			if (isInCircle(e) && setTimeModePre == false) {

				nearPointList = new ArrayList<HashMap<String, Point>>();
				selectPointList = new ArrayList<HashMap<String, Point>>();
				setTimeMode = false;
				setTimeModePre = false;

				System.out.println("此次点击在时钟范围内");

				// 判断长按事件是在1,4象限还是在2,3象限
				if (e.getX() >= centerPoint.x) {
					oneFour = true;
				} else {
					twoThree = true;
				}

				// 如果点击事件在第1,4象限，则把在1,4象限的指针都添加到一个LIST中。
				if (oneFour) {
					for (int i = 0; i < allPointList.size(); i++) {
						for (String key : allPointList.get(i).keySet()) {
							if (allPointList.get(i).get(key).x >= centerPoint.x) {
								nearPointList.add(allPointList.get(i));
							}
						}

					}

					// if (nearPointList.size() == 1) {
					// setTimeModePre = true;
					// selectPointList.add(nearPointList.get(0));
					// invalidate();
					// }
				}

				// 如果点击事件在第2,3象限，则把在2,3象限的指针都添加到一个LIST中。
				if (twoThree) {
					for (int i = 0; i < allPointList.size(); i++) {
						for (String key : allPointList.get(i).keySet()) {
							if (allPointList.get(i).get(key).x < centerPoint.x) {
								nearPointList.add(allPointList.get(i));
							}
						}

					}

					// if (nearPointList.size() == 1) {
					// setTimeModePre = true;
					// selectPointList.add(nearPointList.get(0));
					// invalidate();
					// }
				}

				// 如果当前象限有两个以上的点，则分别计算角度进行判断。
				if (setTimeModePre == false) {
					double cos[] = new double[3];
					int numSelect = 0;// cos值最大的点的下标
					double cosLimit = 0.01;// cos阈值

					double maxCos = 0;
					for (int i = 0; i < nearPointList.size(); i++) {
						for (String key : nearPointList.get(i).keySet()) {
							// 求三段线段的长度的平方，根据余弦定理求角度。
							int distanceHand = (int) (Math.pow(nearPointList
									.get(i).get(key).x - centerPoint.x, 2) + Math
									.pow(nearPointList.get(i).get(key).y
											- centerPoint.y, 2));
							int distancePress = (int) (Math.pow(e.getX()
									- centerPoint.x, 2) + Math.pow(e.getY()
									- centerPoint.y, 2));
							int otherDisance = (int) (Math.pow(nearPointList
									.get(i).get(key).x - e.getX(), 2) + Math
									.pow(nearPointList.get(i).get(key).y
											- e.getY(), 2));

							System.out.println("nearpointsize  "
									+ nearPointList.size());
							System.out.println("-----------i" + i);

							// 余弦公式计算夹角cos
							cos[i] = (distanceHand + distancePress - otherDisance)
									/ (2 * Math.sqrt(distanceHand) * Math
											.sqrt(distancePress));

							System.out.println("有两根指针在同一象限");
							System.out.println("cos的值" + cos[i]);
							System.out.println("distanceHand " + distanceHand);
							System.out.println("distancePress " + distancePress);
							System.out.println("otherDisance " + otherDisance);
						}

					}

					// 选出最大cos值
					for (int i = 0; i < cos.length; i++) {
						if (maxCos < cos[i]) {
							maxCos = cos[i];
							numSelect = i;
						}
					}

					System.out.println("角度差值" + (1.0 - maxCos));
					if (1.0 - maxCos < 0.01) {
						System.out.println("选中指针" + numSelect);
						selectPointList.add(nearPointList.get(numSelect));
						setTimeModePre = true;
					}

				}

			}

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

	}
	
	
	private class ClockThread extends Thread{
		
		private boolean flag = true;
		
		@Override
		public void run() {
			while (flag) {

				postInvalidate();
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		}

		
		public void stopTH(){
			flag = false;
		}
		
		public void runTH(){
			flag = true;
		}
		
		
		
		
		
	}

}
