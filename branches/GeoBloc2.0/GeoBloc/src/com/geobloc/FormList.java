package com.geobloc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * Display forms
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormList extends ListActivity {
	
	private static String __GEOBLOC_DIRECTORY__ = "/sdcard/GeoBloc";
	private static String __GEOBLOC_FORMS_DIRECTORY__ =
		__GEOBLOC_DIRECTORY__+"/forms";
	
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	
	// Lista de formularios
    private List<String> elementos = null;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.list_form);
        setTitle(getString(R.string.app_name)+ " > " + getString(R.string.list_form));
        
        // Directory creation
        createDir(__GEOBLOC_DIRECTORY__);
        
        String formDir = __GEOBLOC_FORMS_DIRECTORY__;
        createDir (formDir);
        
        rellenar(new File(formDir).listFiles());
    }
	
	/**
	 * Crea un directorio cuya ruta se pasa como parámetro. Si falla la creación
	 * devuelve un mensaje. (Si se crea o existe tambien lo devuelve)
	 * @param directory Path completo del directorio
	 * @return True si se creó el directorio
	 */
	private boolean createDir (String directory) {
		if (createDirectory (directory)) {
			return true;
		}
		else {
            Toast.makeText(getApplicationContext(),
            		getString(R.string.uncreated_dir, directory),
                    Toast.LENGTH_SHORT).show();
            return false;
		}
	}
	
	/**
	 * Crea un directorio
	 * @param directory Path absoluto del directorio
	 * @return True si se creó
	 */
	private boolean createDirectory (String directory) {
		File f = new File(directory);
		f.mkdirs();
		if (f.isDirectory())
			return true;
		else
			return false;
	}
	

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int IDFilaSeleccionada = position;
        
        File archivo = new File(elementos.get(IDFilaSeleccionada));
        
        Intent result = new Intent();
        result.putExtra(FormList.FILE_NAME, archivo.getName());
        result.putExtra(FormList.FILE_PATH, __GEOBLOC_FORMS_DIRECTORY__+"/"+archivo.getName());
        
        setResult(RESULT_OK, result);
        finish();
    }
	
	/**
	 * Rellena mediante un simple list item con el nombre de los archivos
	 * @param archivos
	 */
    private void rellenar(File[] archivos) {
        elementos = new ArrayList<String>();
        for( File archivo: archivos) {
            elementos.add(archivo.getName());
        }
        
        
        ArrayAdapter<String> listaArchivos =
        		new ArrayAdapter<String>(this,
        								android.R.layout.simple_list_item_1,
        								elementos);
        setListAdapter(listaArchivos);
    }
}
