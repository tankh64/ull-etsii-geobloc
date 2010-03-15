/**
 * 
 */
package com.geobloc.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geobloc.R;

/**
 * A Compound Button built with a ThreeStateButton and a TextView. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class ThreeStateCheckBox extends LinearLayout {

	private ThreeStateButton box;
	private TextView label;
	
	private boolean drawShadow;
	private String labelText;
	private int state;
	/**
	 * @param context
	 */
	public ThreeStateCheckBox(Context context) {
		super(context);
		initConfig();
		
		inflate(getContext(), R.layout.three_state_checkbox, this);
		//((Activity)getContext()).getLayoutInflater().inflate(R.layout.three_state_checkbox, this);

		// get views and set their values
		box = (ThreeStateButton) findViewById(R.id.box);
		setDrawShadowEnabled(drawShadow);
		setState(state);
		label = (TextView) findViewById(R.id.label);
		label.setTextColor(Color.WHITE);
		label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		setText(labelText);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ThreeStateCheckBox(final Context context, AttributeSet attrs) {
		super(context, attrs);
		initConfig();

		// get attributes		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThreeStateCheckBox, 0, 0);
		drawShadow = a.getBoolean(R.styleable.ThreeStateCheckBox_shadow, false);
		labelText = a.getString(R.styleable.ThreeStateCheckBox_label);
		if (labelText == null)
			labelText = "";
		state = a.getInt(R.styleable.ThreeStateCheckBox_state, ThreeStateButton.__STATE_UNPRESSED__);
		// free TypedArray
		a.recycle();
		
	}

	private void initConfig() {
		this.setOrientation(HORIZONTAL);
		// set attribute defaults
		drawShadow = false;
		labelText = "";
		state = 0;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.three_state_checkbox, this);

		// get views and set their values
		box = (ThreeStateButton) findViewById(R.id.box);
		setDrawShadowEnabled(drawShadow);
		setState(state);
		label = (TextView) findViewById(R.id.label);
		label.setTextColor(Color.WHITE);
		setText(labelText);
		label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
	}
	
	public boolean isStatePressed() {
		return box.isPressed();
	}
	
	public boolean isStateChecked() {
		return box.isChecked();
	}
	
	public int getState() {
		return box.getState();
	}
	
	public void setState(int state) {
		box.setState(state);
	}
	
	//@Override
	public CharSequence getText() {
		return label.getText();
	}
	
	//@Override
	public final void setText(CharSequence text) {
		label.setText(text);
	}
	
	public TextView getLabel() {
		return label;
	}
	
	public void setLabel(TextView label) {
		this.label = label;
	}
	
	public boolean isDrawShadowEnabled() {
		return box.isDrawShadowEnabled();
	}
	
	public void setDrawShadowEnabled(boolean enabled) {
		box.setDrawShadowEnabled(enabled);
	}
	
	public void setOnClickListener(View.OnClickListener listener) {
		box.setOnClickListener(listener);
	}
	
	public void setOnStateChangedListener(View.OnClickListener listener) {
		box.setOnStateChangedListener(listener);
	}
}
