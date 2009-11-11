package ull.geobloc.mockup1;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSExample extends Activity {
	
	private LocationManager lm;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_example);
        
        //lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
    }
    
}
