package com.geobloc.form;

import android.location.Location;

public class FormLocationPage extends FormPage {

	private Location location;
	
	public FormLocationPage () {
		this.setPageType(PageType.LOCATION);
	}
}
