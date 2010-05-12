package com.geobloc.adapters;

import java.util.ArrayList;

import com.geobloc.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageAdapterPhoto extends BaseAdapter {

    private int mGalleryItemBackground;
    private Context mContext;
    
    private ArrayList<Bitmap> mArray = new ArrayList<Bitmap>();
    
    public ImageAdapterPhoto(Context c) {
        mContext = c;
        // See res/values/attrs.xml for the <declare-styleable> that defines
        // Gallery1.
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.GalleryPhoto);
        mGalleryItemBackground = a.getResourceId(
                R.styleable.GalleryPhoto_android_galleryItemBackground, 0);
        a.recycle();
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArray.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
        ImageView i = new ImageView(mContext);
        
        i.setImageBitmap(mArray.get(position));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        //i.setLayoutParams(new Gallery.LayoutParams(136, 88));
        i.setLayoutParams(new Gallery.LayoutParams(170, 120));
        
        // The preferred Gallery item background
        i.setBackgroundResource(mGalleryItemBackground);
        
        return i;
	}
	
	public void addPhoto (Bitmap photo) {
		mArray.add(photo);
		Toast.makeText(mContext, "Añado una nueva foto", Toast.LENGTH_SHORT).show();
	}

	public void addPhotoFromGallery () {
		Toast.makeText(mContext, "Añado una nueva foto desde la galería", Toast.LENGTH_SHORT).show();
	}
	
	public void clearPhotos () {
		Toast.makeText(mContext, "Elimino todas las fotos", Toast.LENGTH_SHORT).show();
		for (int i=mArray.size() - 1; i >= 0; i--) {
			mArray.remove(i);
			//Toast.makeText(mContext, "Elimino la foto: "+i, Toast.LENGTH_SHORT).show();
		}
	}
	
}
