package ull.geobloc.mockup1;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GPSExample extends Activity {
	
	private TextView tv;
	private LocationManager mgr;
	private String best;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_example);
        
        tv = (TextView) findViewById(R.id.gpsLocationLabel);
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        
    }
    
    public void getGPSLocationButtonOnClickHandler (View target) {
    	
    	Criteria criteria = new Criteria();
    	// No need for fine accuracy
    	//criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	best = mgr.getBestProvider(criteria, true);
    	Location location = mgr.getLastKnownLocation(best);
    	tv.setText("Last Known Location: \n\tBest Provider = " + best + "\n\tLocation: " + 
    			location.getLongitude() + " Long, \n\t\t\t\t" + location.getLatitude() + " Lat");
    	
    }
}
