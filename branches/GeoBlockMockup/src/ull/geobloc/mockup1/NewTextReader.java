/**
 * 
 */
package ull.geobloc.mockup1;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import dh.anddev.persistance.SDFilePersistance;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class NewTextReader extends Activity {
	
	private TextView text;
	public static String __TEXT_READER_TEXT__ = "textToBeDisplayedByTextReader";
	public static String __TEXT_READER_OUTPUT__ = "/sdcard/TextReader/";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_reader);
        
        initialConfig();
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	text.setText(extras.getString("textToBeDisplayedByTextReader"));
        }
        else {
        	text.setText("No text to display.");
        }
	}
	
	private void initialConfig() {
		text = (TextView) findViewById(R.id.textReaderText);
	}
	
	public void outputToFileOnClickHandler(View target) {
		OutputToFile("form.xml", text.getText().toString());		
	}
	
	private void OutputToFile(String fileName, String text) {
		//SDFilePersistance.createDirectory(NewTextReader.__TEXT_READER_OUTPUT__);
		
		Calendar cal = Calendar.getInstance();
		String formDirectory = NewTextReader.__TEXT_READER_OUTPUT__+"form_";
		formDirectory += cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.YEAR) 
						+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) 
						+ "-" + cal.get(Calendar.SECOND)+"/";
		SDFilePersistance.createDirectory(formDirectory);
		
		boolean written = SDFilePersistance.writeToFile(formDirectory, fileName, text);
		
		CharSequence toastText;
		if (written)
			toastText = "File saved!";
		else
			toastText = "Error! File could not be saved";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), toastText, duration);
		toast.show();
		
		
	}
}
