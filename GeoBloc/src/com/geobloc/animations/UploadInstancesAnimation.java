package com.geobloc.animations;

import com.geobloc.shared.GBSharedPreferences;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class UploadInstancesAnimation extends Animation {

	private int width;
	private int height;
	private int parentHeight;
	// 3D graphics camera
	private Camera camera = new Camera();
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight)   {
		super.initialize(width, height, parentWidth, parentHeight);
		this.width = width;
		this.height = height;
		this.parentHeight = parentHeight;
		setDuration(GBSharedPreferences.__DEFAULT__ANIMATION_TIME__);
		setInterpolator(new LinearInterpolator());
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t)    {
		final Matrix matrix = t.getMatrix();		
		camera.save();
		
		camera.translate(0.0f, 0.0f + parentHeight * interpolatedTime, 0.0f);

		camera.getMatrix(matrix);
		
		matrix.preTranslate(-width/2, -height/2);
		matrix.postTranslate(width/2, height/2);
		camera.restore();
		
	}
	
}
