/**
 * 
 */
package com.geobloc.persistance;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
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
	
	public static boolean copyFile(String sourceFile, String destFile) {
		boolean success = true;
		try {
			File source = new File(sourceFile);
			File destination = new File(destFile);
			
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
			Log.i(LOG_TAG, "Succesfully copied source= " + sourceFile + " to destination= " + destFile);
		}
		catch (Exception e) {
			Log.e(LOG_TAG, "Encountered an error while copying file " + sourceFile + " to " + destFile);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	 /**
	  * Fetch the entire contents of a text file, and return it in a String.
	  * This style of implementation does not throw Exceptions to the caller.
	  * Source: http://www.javapractices.com/topic/TopicAction.do?Id=42
	  *
	  * @param aFile is a file which already exists and can be read.
	  */
	  static public String getContents(File aFile) {
	    //...checks on aFile are elided
	    StringBuilder contents = new StringBuilder();
	    
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      try {
	        String line = null; //not declared within while loop
	        /*
	        * readLine is a bit quirky :
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    return contents.toString();
	  }

}
