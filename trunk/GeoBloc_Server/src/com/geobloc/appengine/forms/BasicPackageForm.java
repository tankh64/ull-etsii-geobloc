/**
 * 
 */
package com.geobloc.appengine.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

/**
 * An improvement over the BasicForm, BasicPackageForm can hold several files inside.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BasicPackageForm {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	// author of package upload
	@Persistent
	private User author;
	
	// package name
	@Persistent
	private String name;
	
	// files
	@Persistent
	private List<Blob> files;
	
	// filenames
	@Persistent
	private List<String> filenames;
	
	// upload date
	@Persistent
	private Date date;
	
	// basic constructor
	public BasicPackageForm() {
		this.files = new ArrayList<Blob>();
		this.filenames = new ArrayList<String>();
	}
	
	
	public BasicPackageForm(User author, String name, Date date)
	{
		this.author = author;
		this.name = name;
		this.date = date;
		this.files = new ArrayList<Blob>();
		this.filenames = new ArrayList<String>();
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Blob> getFiles() {
		return files;
	}
	
	public void setFiles(List<Blob> files) {
		this.files = files;
	}
	
	public List<String> getFilenames() {
		return filenames;
	}
	
	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}
	
	public void addFile(Blob file, String filename) {
		this.files.add(file);
		this.filenames.add(filename);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
