package com.geobloc.activities;

import com.geobloc.R;

import android.app.ListActivity;
import android.os.Bundle;


public class FormLocalList extends ListActivity {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list_form);
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.list_form));
    }
}
