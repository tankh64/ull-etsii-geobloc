/**
 * 
 */
package com.geobloc.persistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.util.Log;

/**
 * Simple class which allows to create directories and write files to SDCard.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class SDFilePersistance extends Activity {
	
	/*public static boolean sdReady() {
		
	}
	*/
	
	public static boolean createDirectory(String directoryPath) {
		File f = new File(directoryPath);
		f.mkdirs();
		if (f.isDirectory())
			return true;
		else
			return false;
	}
	
	public static boolean writeToFile(String directoryPath, String fileName, String text) {
		boolean success = false;
		try {
			File directory = new File(directoryPath);
		    if (directory.canWrite()){
		    	success = true;
		        File file = new File(directory, fileName);
		        FileWriter writer = new FileWriter(file);
		        BufferedWriter out = new BufferedWriter(writer);
		        out.write(text);
		        out.close();
		    }
		} catch (IOException e) {
		    Log.e("IOException", "Could not write file " + e.getMessage());
		}
		return success;
	}
}
