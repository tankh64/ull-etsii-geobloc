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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geobloc.R;

/**
 * My own attempt at building a widget with a progress bar and a text view next to it.
 * With it you can display you're perfoming a background action or not.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class ProgressItem extends LinearLayout {

	private ProgressBar bar;
	private TextView text;
	
	private boolean barVisible;
	private String label;
	
	public ProgressItem(Context context) {
		super(context);
		initConfig();
		inflate(getContext(), R.layout.progress_item, this);
		bar = (ProgressBar) findViewById(R.id.progress_item_bar);
		text = (TextView) findViewById(R.id.progress_item_text);
		
	}
	
	public ProgressItem(final Context context, AttributeSet attrs) {
		super(context, attrs);
		initConfig();

		// get attributes		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressItem, 0, 0);
		barVisible = a.getBoolean(R.styleable.ProgressItem_bar_visible, true);
		
		label = a.getString(R.styleable.ProgressItem_progress_label);
		if (label == null)
			label = "";
		
		// free TypedArray
		a.recycle();
		
	}
	
	private void initConfig() {
		this.setOrientation(HORIZONTAL);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		((Activity)getContext()).getLayoutInflater().inflate(R.layout.progress_item, this);

		// get views and set their values
		bar = (ProgressBar) findViewById(R.id.progress_item_bar);
		if (!barVisible)
			toggleBarVisibility();
		text = (TextView) findViewById(R.id.progress_item_text);
		text.setText(label);
	}
	
	public TextView getText() {
		return text;
	}
	
	public void setText(TextView tv) {
		text = tv;
	}
	
	public void setText(String s) {
		text.setText(s);
	}

	public View getBar() {
		return bar;
	}
	
	public void toggleBarVisibility() {
		if (bar.getVisibility() == View.VISIBLE)
			bar.setVisibility(View.GONE);
		else
			bar.setVisibility(View.VISIBLE);
	}
	
}
