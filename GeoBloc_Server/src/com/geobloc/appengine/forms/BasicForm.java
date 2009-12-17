package com.geobloc.appengine.forms;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

/**
 * This is a JavaBean or POJO (Plain Old Java Object), representing the first form the GeoBloc Client in 
 * the Android device will upload. Right now, it only stores author, a name and text, which is not 
 * saved as a string because strings in the datastore are limited to 500 chars. It is currently being used 
 * in the server to store uploaded files.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BasicForm {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private User author;
	
	@Persistent
	private String name;
	
	@Persistent
	private Blob file;

	@Persistent
	private Date date;
	
	public BasicForm(User author, String name, Blob file, Date date)
	{
		this.author = author;
		this.name = name;
		this.file = file;
		this.date = date;	
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

	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
