/**
 * 
 */
package com.geobloc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * A button designed with three states: unpressed, pressed (cross) and checked (tick) 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class ThreeStateButton extends Button {

	public static final int __STATE_UNPRESSED__ = 0;
	public static int __STATE_PRESSED__ = 1;
	public static int __STATE_CHECKED__ = 2;
	public static int __NUMBER_OF_STATES__ = 3;
	
	private boolean drawShadow;
	private int state;
	private Paint redPaint;
	private Paint greenPaint;
	private Paint grayPaint;
	
	private View.OnClickListener onStateChangedListener = null;
	
	/**
	 * @param context
	 */
	public ThreeStateButton(Context context) {
		super(context);
		initConfig();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ThreeStateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initConfig();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ThreeStateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initConfig();
	}

	private void nextState() {
		state++;
		state = state % ThreeStateButton.__NUMBER_OF_STATES__;
		//this.setPressed(false);
		// forces to redraw the view
		invalidate();
		if (onStateChangedListener != null)
			onStateChangedListener.onClick(this);
	}
	
	private void initConfig() {
		// initialize variables
		drawShadow = true;
		state = ThreeStateButton.__STATE_UNPRESSED__;
		redPaint = new Paint();
		redPaint.setAntiAlias(true);
		redPaint.setStyle(Paint.Style.FILL);
		redPaint.setStrokeWidth(5.0f);
		redPaint.setColor(Color.RED);
		greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setStyle(Paint.Style.FILL);
		greenPaint.setStrokeWidth(5.0f);
		greenPaint.setColor(Color.GREEN);
		grayPaint = new Paint();
		grayPaint.setAntiAlias(false);
		grayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		grayPaint.setStrokeWidth(6.5f);
		grayPaint.setColor(Color.GRAY);
		
		// listeners
		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextState();				
			}
		});
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) && (event.getAction() == KeyEvent.ACTION_UP)) {
			nextState();
			this.setPressed(false);
		}
		return false;
	}
	
	private void drawShadow(Canvas canvas) {
		// draw background to achieve effect similar to standard Android CheckBox
		// X background
		canvas.drawLine(0 + getCompoundPaddingLeft(), 0 + getCompoundPaddingTop(), getWidth() - getCompoundPaddingRight(), getHeight() - getCompoundPaddingBottom(), grayPaint);
		canvas.drawLine(getWidth() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), 0 + getCompoundPaddingLeft(), getHeight() - getCompoundPaddingBottom(), grayPaint);
		// tick background
		canvas.drawLine(getWidth() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), (float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - getCompoundPaddingBottom(), grayPaint);
		canvas.drawLine((float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - (0.9f)*getCompoundPaddingBottom(), 0 + getCompoundPaddingLeft(), (float) (getHeight() - (2f)*getCompoundPaddingBottom()), grayPaint);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (drawShadow)
			drawShadow(canvas);
		switch (state) {
			case (1):
				// draw an X using redPaint
				//canvas.drawLine(0 + getCompoundPaddingLeft(), 0 + getCompoundPaddingTop(), getRight() - getCompoundPaddingRight(), getHeight() - getCompoundPaddingBottom(), redPaint);
				//canvas.drawLine(getRight() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), 0 + getCompoundPaddingLeft(), getHeight() - getCompoundPaddingBottom(), redPaint);
				canvas.drawLine(0 + getCompoundPaddingLeft(), 0 + getCompoundPaddingTop(), getWidth() - getCompoundPaddingRight(), getHeight() - getCompoundPaddingBottom(), redPaint);
				canvas.drawLine(getWidth() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), 0 + getCompoundPaddingLeft(), getHeight() - getCompoundPaddingBottom(), redPaint);
				break;
			case 2:
				// draw a tick
				//canvas.drawLine(getRight() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), (float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - getCompoundPaddingBottom(), greenPaint);
				//canvas.drawLine((float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - (0.9f)*getCompoundPaddingBottom(), 0 + getCompoundPaddingLeft(), (float) (getHeight() - (2f)*getCompoundPaddingBottom()), greenPaint);
				canvas.drawLine(getWidth() - getCompoundPaddingRight(), 0 + getCompoundPaddingTop(), (float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - getCompoundPaddingBottom(), greenPaint);
				canvas.drawLine((float) (0 + (1.5f)*getCompoundPaddingLeft()), getHeight() - (0.9f)*getCompoundPaddingBottom(), 0 + getCompoundPaddingLeft(), (float) (getHeight() - (2f)*getCompoundPaddingBottom()), greenPaint);

				break;
			default: 
				break;
		}
	}
	
	public void setOnStateChangedListener(View.OnClickListener listener) {
		onStateChangedListener = listener;
	}
	
	public boolean isDrawShadowEnabled() {
		return drawShadow;
	}
	
	public void setDrawShadowEnabled(boolean enabled) {
		drawShadow = enabled;
	}
	
	public boolean isUnPressed() {
		return (state == ThreeStateButton.__STATE_UNPRESSED__);
	}
	
	public boolean isPressed() {
		return (state == ThreeStateButton.__STATE_PRESSED__);
	}
	
	public boolean isChecked() {
		return (state == ThreeStateButton.__STATE_CHECKED__);
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
		// forces to redraw the view
		invalidate();
	}
	
}
