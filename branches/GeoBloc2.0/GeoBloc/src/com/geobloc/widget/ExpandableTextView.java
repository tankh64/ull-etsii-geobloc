/**
 * 
 */
package com.geobloc.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.geobloc.R;

/**
 * Looking for a way to make ExpandableListView fit into my needs, I simply took the task of making my own 
 * simple ExpandableListView, which only has one child, a TextView.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class ExpandableTextView extends LinearLayout {

	private ImageButton mySwitch;
	private TextView titleText;
	private TextView bodyText;
	
	private String title;
	private String body;
	private boolean open;
	
	private View myself;
	
	private OnClickListener onClickListener;
	
	private Bitmap openBitmap;
	private Bitmap closedBitmap;
	
	private OnClickListener switchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			setOpen(!open);
			if (onClickListener != null)
				onClickListener.onClick(myself);
		}
	};
	
	public ExpandableTextView(Context context) {
		super(context);
		inflate(getContext(), R.layout.expandable_textview, this);
		mySwitch = (ImageButton) findViewById(R.id.expandable_textview_switchButton);
		titleText = (TextView) findViewById(R.id.expandable_textview_titleTextView);
		bodyText = (TextView) findViewById(R.id.expandable_textview_bodyTextView);
		myself = this;
		open = false;
		mySwitch.setOnClickListener(switchClickListener);
		titleText.setOnClickListener(switchClickListener);
		openBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_right_alt1_32x32);
		closedBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_down_alt1_32x32);
	}

	public ExpandableTextView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		// get attributes
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, 0, 0);
		title = a.getString(R.styleable.ExpandableTextView_expandableTextViewTitle);
		body = a.getString(R.styleable.ExpandableTextView_expandableTextViewBody);
		open = a.getBoolean(R.styleable.ExpandableTextView_expandableTextViewOpen, false);
		if (title == null)
			title = "";
		if (body == null)
			body = "";
		// free TypedArray
		a.recycle();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.expandable_textview, this);
		
		// get views and set their values
		mySwitch = (ImageButton) findViewById(R.id.expandable_textview_switchButton);
		titleText = (TextView) findViewById(R.id.expandable_textview_titleTextView);
		bodyText = (TextView) findViewById(R.id.expandable_textview_bodyTextView);
		myself = this;
		setOpen(open);
		titleText.setText(title);
		bodyText.setText(body);
		mySwitch.setOnClickListener(switchClickListener);
		titleText.setOnClickListener(switchClickListener);
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
		if (!this.open) {
			//mySwitch.setImageBitmap(closedBitmap);
			mySwitch.setImageResource(R.drawable.arrow_down_alt1_32x32);
			//mySwitch.setScaleType(ScaleType.FIT_CENTER);
			bodyText.setVisibility(View.GONE);
		}
		else {
			mySwitch.setImageResource(R.drawable.arrow_right_alt1_32x32);
			//mySwitch.setImageBitmap(openBitmap);
			//mySwitch.setScaleType(ScaleType.FIT_CENTER);
			bodyText.setVisibility(View.VISIBLE);
		}
	}
	
	public TextView getTitle() {
		return titleText;
	}
	
	public TextView getBody() {
		return bodyText;
	}
	
	@Override
	public void setOnClickListener(View.OnClickListener listener) {
		//super.setOnClickListener(listener);
		this.onClickListener = listener;
	}
}
