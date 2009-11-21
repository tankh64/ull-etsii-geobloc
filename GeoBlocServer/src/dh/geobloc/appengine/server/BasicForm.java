package dh.geobloc.appengine.server;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Text;

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
	private Text xml;

	@Persistent
	private Date date;
	
	public BasicForm(User author, String name, Text xml, Date date)
	{
		this.author = author;
		this.name = name;
		this.xml = xml;
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

	public Text getXml() {
		return xml;
	}

	public void setXml(Text xml) {
		this.xml = xml;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
