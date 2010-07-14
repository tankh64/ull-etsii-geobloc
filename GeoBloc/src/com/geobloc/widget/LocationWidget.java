package com.geobloc.widget;

import com.geobloc.R;
import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LocationWidget extends RelativeLayout implements IQuestionWidget {
	protected static final String TAG = "LocationWidget";
	
	//private String longitud;
	//private String latitud;
	private Location location;
	
	Context mContext;
	LayoutInflater inflater;
	
	TextView text;
	ToggleButton gpsButton;
	Button settingsButton;
	int myProgress = 0;
	ProgressBar progressBar;
	
	private LocationManager locManager;
	private LocationListener onLocationChange;
	
	private Handler mHandler = new Handler();
	
	public LocationWidget(Context context, ViewGroup parent) {
		super(context);
		
		mContext = context;
				
		/* Así funciona, pero añade un Nivel mas en la jerarquía */
		inflate(context, R.layout.form_location_page, this);
		
		text = (TextView) findViewById(R.id.textInfoLocation);
		setText();
		
		progressBar = (ProgressBar) findViewById (R.id.progressbarLocation);
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		settingsButton = (Button) findViewById (R.id.settingsGPS);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				mContext.startActivity(settingsIntent);
			}
		});

		
		locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		gpsButton = (ToggleButton) findViewById (R.id.connectLocation);
		gpsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((ToggleButton)v).isChecked()) {
						if (getGPSStatus() == true) {
							settingsButton.setEnabled(false);
							progressBar.setVisibility(ProgressBar.VISIBLE);
							locManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER,
									0, 0f,
									onLocationChange);
							text.setText(mContext.getString(R.string.searching_position));
						} else {
							gpsButton.setChecked(false);
							Utilities.showToast(getContext(), "Debe activar GPS en los Ajustes del Teléfono", Toast.LENGTH_SHORT);
						}
					}
					else {
						clearAnswer();
					}
				}
		});
		
		// Location
		onLocationChange = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				text.setText(mContext.getString(R.string.find_position, arg0.getLatitude(), arg0.getLongitude()));
				Log.i(TAG, "Received location Update");
				
				// Lo que devolveremos cuando nos pidan la respuesta
				location = arg0;
				
				gpsButton.setChecked(false);
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				checkGPSStatus();
			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.i(TAG, "Location Provider "+provider+" disabled");
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.i(TAG, "Location Provider "+provider+" enabled");
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.i(TAG, "Location Provider change to "+status);
			}
			
		};
		
		// Se chequea el estado del GPS
		checkGPSStatus();
	}

	public LocationWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
	}

	public void buildView (QuestionPrompt qP) {

	}

	@Override
	public void clearAnswer() {
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		locManager.removeUpdates(onLocationChange);
		gpsButton.setChecked(false);
		text.setBackgroundColor(Color.TRANSPARENT);
		text.setText("Se ha desactivado la búsqueda de posición");
		location = null;
	}

	@Override
	public void mySetListener(OnTouchListener list) {
		
	}
	
	private boolean getGPSStatus()
	{
	    String allowedLocationProviders =
	        Settings.System.getString(mContext.getContentResolver(),
	        Settings.System.LOCATION_PROVIDERS_ALLOWED);
	 
	    if (allowedLocationProviders == null) {
	        allowedLocationProviders = "";
	    }
	 
	    return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
	}
	
	private void setText () {
		if (getGPSStatus()) {
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setText(mContext.getString(R.string.not_position));
		} else {
			text.setBackgroundColor(Color.RED);
			text.setText(mContext.getString(R.string.activateGPS));
		}
	}
	
	/**
	 * 
	 */
	public void checkGPSStatus () {
		if (getGPSStatus() == true) {
			text.setBackgroundColor(Color.TRANSPARENT);
			
			// Deshabilitamos botón de preferencias
			settingsButton.setVisibility(GONE);
			gpsButton.setEnabled(true);
		} else {
			text.setBackgroundColor(Color.RED);
			text.setText(mContext.getString(R.string.activateGPS));
			
			// Habilitamos botón de preferencias
			settingsButton.setVisibility(VISIBLE);
			gpsButton.setEnabled(false);
		}
	}

	@Override
	public Object getAnswer() {
		return location;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.LOCATION;
	}
	
}
