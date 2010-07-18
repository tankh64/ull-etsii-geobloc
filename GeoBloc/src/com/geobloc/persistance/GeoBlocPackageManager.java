/**
 * 
 */
package com.geobloc.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.geobloc.shared.GBSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A new class designed to take care over the files which make up a package. In this way, activities 
 * only need to create a new package manager with the package name, and send it all the data; PackageManager 
 * will make it persistent and allow you to access all the files within the package.
 * 
 * Currently, there will be no database implementation to handle the packages inside the file system. 
 * NOTE: Context is required in the second constructor, since this one creates a folder and requires the 
 * activity context to search in the SharedPreferences
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GeoBlocPackageManager {
	
	private static String LOG_TAG = "GeoBlocPackageManager";
	
	private boolean ok;
	
	// we need to hold the directory
	private File directory;
	
	// absolute package directory
	private String packageDirectory;
	
	// package name
	private String packageName;
	
	public GeoBlocPackageManager() {
		packageName = "";
		packageDirectory = "";
		directory = null;
		ok = false;
	}
	
	public GeoBlocPackageManager(Context context, String name) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		packageName = name;
		packageDirectory = prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_PACKAGES_PATH__) + name;
		directory = new File(packageDirectory);
		directory.mkdirs();
		if (directory.isDirectory())
			ok = true;
		else
			ok = false;
	}
	
	// if true, package manager is working
	public boolean OK(){
		return ok;
	}
	
	// opens a directory, doesn't create it
	public boolean openPackage(String fullpath) {
		packageName = packageDirectory.substring(packageDirectory.lastIndexOf('/')+1);
		packageDirectory = fullpath;
		directory = new File(fullpath);
		if (directory.isDirectory())
			ok = true;
		else
			ok = false;
		return ok;
	}
	
	// same as openPackage, but builds the directory if necessary
	public boolean openOrBuildPackage(String fullpath) {
		packageName = packageDirectory.substring(packageDirectory.lastIndexOf('/')+1);
		packageDirectory = fullpath;
		directory = new File(fullpath);
		if (!directory.isDirectory()) {
			directory.mkdirs();
			if (directory.isDirectory())
				ok = true;
			else
				ok = false;
		}
		else
			ok = true;
		return ok;
	}
	
	public boolean eraseFile(String filename) {
		boolean success = false;
		//String[] filenamesArray = directory.list();
		List<String> filenames = this.getAllFilenames();
		List<File> files = this.getAllFiles();
		String s;
		for (int i = 0; i < filenames.size(); i++) {
			s = filenames.get(i);
			if (s.contains(filename)) {
				File toBeErased = files.get(i);
				success = toBeErased.delete();
				// end loop
				i = filenames.size();
			}
		}
		return success;
	}
	
	public boolean eraseDirectory(String directoryname) {
		boolean success = false;
		boolean finished = false;
		List<File> directories = this.getAllDirectories();
		String s;
		int i = 0;
		while ((!finished) && (i < directories.size())) {
			s = directories.get(i).getName();
			if (s.contains(directoryname)) {
				success = true;
				File directoryToBeErased = directories.get(i);
				// first erase all files in the directory
				for (File f : directoryToBeErased.listFiles()) {	
					if (!f.delete())
						success = false;
				}
				if (directoryToBeErased.listFiles().length > 0)
						Log.e(LOG_TAG, "All files in directory " + directoryname + " could not be erased.");
				// if all files in the directory where deleted, delete the directory
				if (success)
					success = directoryToBeErased.delete();
				if ((directoryToBeErased.exists()) && success)
					Log.e(LOG_TAG, "Directory " + directoryname + " is supposed to be erased but isn't");
				// end loop
				i = directories.size();
			}
			i++;
		}
		return success;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public String getPackageFullpath() {
		return packageDirectory;
	}
	
	public boolean addFile(String fileName, String text) {
		return SDFilePersistance.writeToFile(packageDirectory, fileName, text);
	}
	
	public boolean addFile(String fileName, byte[] data) {
		return SDFilePersistance.writeToFile(packageDirectory, fileName, data);
	}
	
	public List<String> getAllFilenames() {
		String[] filenamesArray = directory.list();
		List<String> filenames = new ArrayList<String>();
		for (int i = 0; i < filenamesArray.length; i++)
			filenames.add(filenamesArray[i]);
		return filenames;
	}
	
	public List<String> getAllFilePaths() {
		String[] filenamesArray = directory.list();
		List<String> filenames = new ArrayList<String>();
		for (int i = 0; i < filenamesArray.length; i++)
			filenames.add(packageDirectory + filenamesArray[i]);
		return filenames;
	}
	
	public List<byte[]> getAllByteFiles() {
		File[] fileArray = directory.listFiles();
		List<byte[]> files = new ArrayList<byte[]>();
		byte[] buffer;
		FileInputStream is;
		try {
			for (int i = 0; i < fileArray.length; i++) {
				is = new FileInputStream(fileArray[i]);
				files.add(IOUtils.toByteArray(is));
				is.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
	
	public List<File> getAllFiles() {
		File[] fileArray = directory.listFiles();
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < fileArray.length; i++) {
			files.add(fileArray[i]);
		}
		return files;
	}
	
	public List<File> getAllDirectories() {
		File[] fileArray = directory.listFiles();
		List<File> directories = new ArrayList<File>();
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory())
				directories.add(fileArray[i]);
		}
		return directories;
	}
	
	/**
	 * Builds a ZIP file representing the instance/package filtering the 'form.xml' file.
	 * @param zipFileName The desired package filename.
	 * @return True if the operation was successful, false otherwise.
	 */
	public boolean buildZIPfromPackage(String zipFileName) {
		if (!isPackageEmpty()) {
			List<String> filepaths = this.getAllFilePaths();
			List<String> filenames = this.getAllFilenames();
			int location = filenames.indexOf("form.xml");
			filepaths.remove(location);
			filenames.remove(location);
			return SDFilePersistance.makeZIPFile(zipFileName, filepaths, filenames);
		}
		else
			return false;
	}
	
	public boolean isPackageEmpty() {
		return (directory.list().length == 0);
	}
}
