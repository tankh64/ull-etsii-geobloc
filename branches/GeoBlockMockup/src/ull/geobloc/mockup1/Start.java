package ull.geobloc.mockup1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Start extends Activity {
	
	private Button startGPSExample;
	private Button startGeoBlockMockup;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startGPSExample = (Button) findViewById(R.id.GPSExampleStartButton);
        startGeoBlockMockup = (Button) findViewById(R.id.GeoBlocMockupStartButton);
    }
    
    /* 
     * Only in Android 1.6
     */
    public void startGPSExampleClick(View target) {
    	startGPSExample();
    }
    
    public void startGPSExample() {
    	Intent i = new Intent(this, GPSExample.class);
    	startActivity(i);
    }
    
    public void startGeoBlockMockupClick(View target) {
    	startGeoBlockMockup();
    }
    
    public void startGeoBlockMockup() {
    	Intent i = new Intent(this, GeoBlocForm.class);
    	startActivity(i);
    }
}