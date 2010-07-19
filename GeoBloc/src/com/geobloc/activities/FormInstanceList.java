/*
 *
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

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.geobloc.FormList;
import com.geobloc.R;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormInstance;
import com.geobloc.shared.IFormDefinition;
import com.geobloc.shared.IInstanceDefinition;
import com.geobloc.shared.IJavaToDatabaseForm;
import com.geobloc.shared.IJavaToDatabaseInstance;
import com.geobloc.shared.Utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
public class FormInstanceList extends ListActivity {
        private static final String TAG = "FormInstanceList";
        
        public static final String FILE_PATH = "filepath";
        public static final String LOCAL_ID = "localId";

        private static final int DIALOG_ERROR = 1;
        // mensaje de error en strError
        private String strError = "Desconocido";
        
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
                return listInstance.size();
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
                    convertView = mInflater.inflate(R.layout.form_instance_list, null);

                    // Creates a ViewHolder and store references to the two children views
                    // we want to bind data to.
                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.title);
                    holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                    holder.description = (TextView) convertView.findViewById(R.id.description);
                    holder.complete = (TextView) convertView.findViewById(R.id.complete);
                    holder.date = (TextView) convertView.findViewById(R.id.date);

                    convertView.setTag(holder);
                } else {
                    // Get the ViewHolder back to get fast access to the TextView
                    // and the ImageView.
                    holder = (ViewHolder) convertView.getTag();
                }

                IInstanceDefinition instance = listInstance.get(position);
                
                // Bind the data efficiently with the holder.
                holder.title.setText((instance.getLabel()));
                holder.description.setText(instance.getPackage_path());
                holder.icon.setImageBitmap(mIcon);
                DateFormat df = DateFormat.getInstance();
                if (instance.getDate() == null)
                	holder.date.setText("Desconocida");
                else 
                	holder.date.setText(df.format(instance.getDate()));
                holder.complete.setText("Siempre Completo");

                return convertView;
            }

            static class ViewHolder {
                ImageView icon;
                TextView title;
                TextView description;
                TextView complete;
                TextView date;
            }
        }
    
    IJavaToDatabaseInstance instanceInterface;
    static ArrayList<IInstanceDefinition> listInstance = new ArrayList<IInstanceDefinition>();

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.app_name)+" > "+getString(R.string.list_local_instances));
        
        setListAdapter(new EfficientAdapter(this));

        instanceInterface = DbFormInstance.getParserInterfaceInstance(this);
        
        // Obtenemos la lista de Esquemas de Formularios Locales
        try {
        	listInstance = (ArrayList<IInstanceDefinition>) instanceInterface.getListOfLocalInstances();
        } catch (Exception ex) {
        	ErrorMessage("Error al cargar la lista de Instancias");
        	Log.e(TAG, ex.getMessage());
        }
        
        if ((listInstance == null) || (listInstance.size() == 0)) {
        	Utilities.showToast(getApplicationContext(), "Lista de Instancias Vacía ", Toast.LENGTH_LONG);
        }
        


    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Utilities.showToast(getApplicationContext(), "Selected the "+position, Toast.LENGTH_SHORT);
        
        //IFormDefinition form = listForm.get(position);
        //long localId = form.getForm_local_id();
        
        //Utilities.showToast(getApplicationContext(), "Selected the localId: "+localId, Toast.LENGTH_SHORT);
        
        // TODO: must return the "formLocalId" and "filename"
        Intent result = new Intent();
        //result.putExtra(FormList.FILE_NAME, archivo.getName());
        String path = "";
        
        try {
        	//path = formsInterface.getPathLocalForm(localId);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        
        result.putExtra(FormInstanceList.FILE_PATH, path);
        //result.putExtra(FormInstanceList.LOCAL_ID, localId);
        
        setResult(RESULT_OK, result);
        finish();
    }
    
    
    @Override
    protected void onDestroy () {
    	instanceInterface.close();
    	
    	super.onDestroy();
    }
    
	private void ErrorMessage (String localError) {
		strError = localError;
		showDialog(DIALOG_ERROR);
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ERROR:
            return new AlertDialog.Builder(FormInstanceList.this)
            .setIcon(R.drawable.alert_dialog_icon)
            .setTitle("Error")
            .setMessage(strError)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
         		   finish();
                }
            })
            .create();
        }
        return null;
        }
    
}