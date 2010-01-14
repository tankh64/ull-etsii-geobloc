/**
 * 
 */
package com.geobloc.animations;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormsDownloaderListViewAnimation extends Animation {
	
	private int width;
	private int height;
	// 3D graphics camera
	private Camera camera = new Camera();
	
	public FormsDownloaderListViewAnimation() {
		
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight)   {
		super.initialize(width, height, parentWidth, parentHeight);
		this.width = width;
		this.height = height;
		setDuration(GBSharedPreferences.__DEFAULT__ANIMATION_TIME__);
		setFillAfter(true);
		setInterpolator(new LinearInterpolator());
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t)    {
		final Matrix matrix = t.getMatrix();
		/*
		// First animation (no Camera, only 2D)
		matrix.setScale(interpolatedTime, interpolatedTime);
		matrix.preTranslate(-width/2, -height/2);
		matrix.postTranslate(width/2, height/2);
		*/
		
		camera.save();
		camera.translate(0.0f, 0.0f, (1300 - 1300.0f * interpolatedTime));
		camera.rotateY(360 * interpolatedTime);
		camera.getMatrix(matrix);
		
		matrix.preTranslate(-width/2, -height/2);
		matrix.postTranslate(width/2, height/2);
		camera.restore();
	}
}
