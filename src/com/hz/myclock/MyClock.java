package com.hz.myclock;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.hz.myclock.util.DisplayUtils;

public class MyClock extends View {

	private int width;
	private int height;

	private Context mContext;

	private Point houEndPoint;
	private Point minEndPoint;
	private Point secEndPoint;
	private Point centerPoint;

	private Paint houPaint;
	private Paint minPaint;
	private Paint secPaint;
	private Paint circlePaint;
	private Paint textPaint;
	
	private Calendar calendar;
	
	private int hour;
	private int min;
	private int sec;

	private int circleRadius = 100;
	private int secLength = (int) (circleRadius * 0.9);

	// 自定义标志，如果为false表示为系统时间， true为自定时间。
	private boolean customTime = false;

	public MyClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initObject();
		Date date = new Date();
//		Calendar calendar = Calendar.getInstance();
//		SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis()-3*60*60*1000);
		new Thread() {
			public void run() {
				int i = 0;
				while (true) {
					if(i==5){
						calendar.set(2015, 3, 26, 14, 0 , 0);
					}
					postInvalidate();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					i++;
				}

			};
		}.start();
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
		initTime();

		drawText(canvas);
		drawCircle(canvas);
		drawHour(canvas);
		drawMin(canvas);
		drawSec(canvas);

	}

	private void initTime() {
		
		hour = calendar.get(Calendar.HOUR);
		min = calendar.get(Calendar.MINUTE);
		sec = calendar.get(Calendar.SECOND);
		
		System.out.println("--------------"+hour);
		System.out.println("--------------"+min);
		System.out.println("--------------"+sec);
		
		
	}

	private void initObject() {
		centerPoint = new Point();
		houEndPoint = new Point();
		minEndPoint = new Point();
		secEndPoint = new Point();

		circlePaint = new Paint();
		houPaint = new Paint();
		secPaint = new Paint();
		minPaint = new Paint();
		textPaint = new Paint();
		
		calendar = Calendar.getInstance();
	}

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

	private void drawSec(Canvas canvas) {
		secPaint.setColor(Color.GREEN);
		secPaint.setStyle(Style.STROKE);
		secPaint.setAlpha(255);
		secPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 2));
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

	private void drawMin(Canvas canvas) {

		minPaint.setColor(Color.RED);
		minPaint.setStyle(Style.STROKE);
		minPaint.setAlpha(255);
		minPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 2));
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
				+ centerPoint.x);

		canvas.drawLine(centerPoint.x, centerPoint.y, minEndPoint.x,
				minEndPoint.y, minPaint);
	}

	private void drawHour(Canvas canvas) {

		houPaint.setColor(Color.BLACK);
		houPaint.setStyle(Style.STROKE);
		houPaint.setAlpha(255);
		houPaint.setAntiAlias(true);
		houPaint.setStrokeWidth(DisplayUtils.Dp2Px(mContext, 5));

		houEndPoint.x = (int) Math.round(Math
				.sin(-Math.PI - Math.PI / 6 * hour)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.6f
				+ centerPoint.x);
		houEndPoint.y = (int) Math.round(Math
				.cos(-Math.PI - Math.PI / 6 * hour)
				* DisplayUtils.Dp2Px(mContext, secLength)
				* 0.6f
				+ centerPoint.x);

		canvas.drawLine(centerPoint.x, centerPoint.y, houEndPoint.x,
				houEndPoint.y, houPaint);
	}

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
		
		canvas.drawText("15", centerPoint.x + DisplayUtils.Dp2Px(mContext, secLength-5),
				centerPoint.y ,
				textPaint);
		
		canvas.drawText("45", centerPoint.x - DisplayUtils.Dp2Px(mContext, secLength),
				centerPoint.y ,
				textPaint);
	}

}