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

import android.os.Environment;

/**
 * A new class designed to take care over the files which make up a package. In this way, activities 
 * only need to create a new package manager with the package name, and send it all the data; PackageManager 
 * will make it persistent and allow you to access all the files within the package.
 * 
 * Currently, there will be no database implementation to handle the packages inside the file system. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GeoBlocPackageManager {
	
	private boolean ok;
	
	// we need to hold the directory
	private File directory;

	// package directory
	public static String __PACKAGE_DIRECTORY__ = "/GeoBloc/packages/";
	public static String __DEFAULT_PACKAGE_MANIFEST_FILENAME__ = "manifest.xml";
	public static String __DEFAULT_FORM_FILENAME__ = "form.xml";
	
	// absolute package directory
	private String packageDirectory;
	
	public GeoBlocPackageManager() {
		packageDirectory = "";
		directory = null;
		ok = false;
	}
	
	public GeoBlocPackageManager(String name) {
		packageDirectory = Environment.getExternalStorageDirectory() + 
			GeoBlocPackageManager.__PACKAGE_DIRECTORY__ + name;
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
	
	public boolean openPackage(String fullpath) {
		packageDirectory = fullpath;
		directory = new File(fullpath);
		if (directory.isDirectory())
			ok = true;
		else
			ok = false;
		return ok;
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
}
