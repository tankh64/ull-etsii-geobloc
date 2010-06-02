/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geobloc.activities;

import java.util.ArrayList;

import com.geobloc.FormList;
import com.geobloc.R;
import com.geobloc.db.FormDefinition;
import com.geobloc.shared.Utilities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

/**
 * Demonstrates how to write an efficient list adapter.
 * 
 * The adapter used has a ImageView, and 4 TextView containing the information of the templates
 * of forms: name, description, version and date.

 *
 * To work efficiently the adapter implemented here uses two techniques:
 * - It reuses the convertView passed to getView() to avoid inflating View when it is not necessary
 * - It uses the ViewHolder pattern to avoid calling findViewById() when it is not necessary
 *
 * The ViewHolder pattern consists in storing a data structure in the tag of the view returned by
 * getView(). This data structures contains references to the views we want to bind data to, thus
 * avoiding calls to findViewById() every time getView() is invoked.
 */
public class FormDefinitionList extends ListActivity {
	private static final String TAG = "FormDefinitionList";

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
            mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.formulario);
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return listForm.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.form_definition_list, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.description = (TextView) convertView.findViewById(R.id.description);
                holder.version = (TextView) convertView.findViewById(R.id.version);
                holder.date = (TextView) convertView.findViewById(R.id.date);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            FormDefinition form = listForm.get(position);
            
            // Bind the data efficiently with the holder.
            holder.title.setText((form.getForm_name()));
            holder.description.setText(form.getForm_description());
            holder.icon.setImageBitmap(mIcon);
            holder.date.setText(form.getForm_date());
            holder.version.setText(String.valueOf(form.getForm_version()));

            return convertView;
        }

        static class ViewHolder {
            ImageView icon;
            TextView title;
            TextView description;
            TextView version;
            TextView date;
        }
    }
    
    /****************** FormTemplateList **************/
    
    private static ArrayList<FormDefinition> listForm = new ArrayList<FormDefinition>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new EfficientAdapter(this));
        
        // Obtenemos la lista de Esquemas de Formularios Locales
        try {
        	//listForm = getListOfLocalForms ();
        } catch (Exception ex) {
        	Log.e(TAG, ex.getMessage());
        }
        
        // Si no tenemos nada, mostramos la de ejemplo
        if ((listForm == null) || (listForm.size() == 0)){
        	 listForm = new ArrayList<FormDefinition>();	// Form definition list
        	 
        	FormDefinition form1 = new FormDefinition("Fresas", "serverform11212", 0, "Formulario de fresas", "02/03/2010", -1);
        	FormDefinition form2 = new FormDefinition("Naranjas", "serverform11213", 1, "Formulario de naranjas", "04/03/2010", -1);
        	FormDefinition form3 = new FormDefinition("Tomates", "serverform11214", 2, "Formulario de tomates", "02/03/2009", -1);
        	FormDefinition form4 = new FormDefinition("Papas", "serverform11215", 2, "Formulario de papitas negras", "02/03/2010", -1);

        	listForm.add(form1);
        	listForm.add(form2);
        	listForm.add(form3);
        	listForm.add(form4);
        }

    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Utilities.showToast(getApplicationContext(), "Selected the "+position, Toast.LENGTH_SHORT);
    	
    	// TODO: must return the "formLocalId" and "filename"
        Intent result = new Intent();
        //result.putExtra(FormList.FILE_NAME, archivo.getName());
        //result.putExtra(FormList.FILE_PATH, __GEOBLOC_FORMS_DIRECTORY__+"/"+archivo.getName());
        setResult(RESULT_OK, result);
    	finish();
    }
    
}
