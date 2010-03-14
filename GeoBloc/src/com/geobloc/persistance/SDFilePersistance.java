/**
 * 
 */
package com.geobloc.persistance;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.util.Log;

/**
 * Simple class which allows to create directories and write files to SDCard.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class SDFilePersistance extends Activity {
	
	public static String LOG_TAG = "SDFilePersistance";
	
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
	
	public static boolean writeToFile(String directoryPath, String fileName, byte[] data) {
		boolean success = false;
		try {
			File directory = new File(directoryPath);
		    if (directory.canWrite()){
		    	success = true;
		        File file = new File(directory, fileName);
		        FileOutputStream fouts = new FileOutputStream(file);
		        fouts.write(data);
		        fouts.close();
		    }
		} catch (IOException e) {
		    Log.e("IOException", "Could not write file " + e.getMessage());
		}
		return success;
	}
	
	public static boolean makeZIPFile(String targetFile, List<String> sourcePaths, List<String> sourceFilenames) {
		boolean success = true;
		try {
			Log.i(LOG_TAG, "Making a ZIP file.");
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile));
			FileInputStream fis;
			BufferedInputStream bis;
			int bytesRead;
			byte[] buffer = new byte[1024];
			// for each file
			for (int i = 0; i < sourcePaths.size(); i++) {
				File file = new File(sourcePaths.get(i));

	            bis = new BufferedInputStream(
	                    new FileInputStream(file));
	            ZipEntry entry = new ZipEntry(sourceFilenames.get(i));
	            //entry.setMethod(ZipEntry.STORED);
	            //entry.setCompressedSize(file.length());
	            //entry.setSize(file.length());
	            //entry.setCrc(crc.getValue());
	            zos.putNextEntry(entry);
	            while ((bytesRead = bis.read(buffer)) != -1) {
	               zos.write(buffer, 0, bytesRead);
	            }
	            zos.closeEntry();
	            bis.close();
			}
			
			// finish
			zos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			success = false;
			Log.e(LOG_TAG, "Exception while making a ZIP file.");
		}
		return success;
	}
}
