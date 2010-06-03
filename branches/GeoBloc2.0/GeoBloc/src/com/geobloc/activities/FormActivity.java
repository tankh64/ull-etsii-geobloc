package com.geobloc.activities;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.geobloc.R;
//import com.geobloc.activities.FormActivity.FormsLoader_FormsTaskListener;
import com.geobloc.adapters.ImageAdapterPhoto;
import com.geobloc.form.FormDataPage;
import com.geobloc.form.FormPage.PageType;
import com.geobloc.handlers.FormHandler;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.LoadFormTask;
import com.geobloc.widget.MediaWidget;
import com.geobloc.widget.QuestionWidget;
import com.geobloc.widget.CreateWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity that loads the form and is responsible to handle it graphically
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormActivity extends Activity {
	private static final String TAG = "FormActivity";
	
	private static final int CAMERA_ACTIVITY = 1;
	private static final int GALLERY_ACTIVITY = 2;
	
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    
    private static final int MENU_PREVIOUS_PAGE = Menu.FIRST;
    private static final int MENU_NEXT_PAGE = MENU_PREVIOUS_PAGE + 1;
    private static final int MENU_SAVE_COMPLETE = MENU_NEXT_PAGE + 1;
    private static final int MENU_SAVE_INCOMPLETE = MENU_SAVE_COMPLETE + 1;
    private static final int MENU_JUMP_TO = MENU_SAVE_INCOMPLETE + 1;
    
    private static final int VIEW_PHOTO = 0;
    private static final int DELETE_PHOTO = 1;

    
	private class FormsLoader_FormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsLoader_FormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void taskComplete(Object result) {
			pDialog.dismiss();
			
			formH = (FormHandler)result;
			if (formH == null) {
			    new AlertDialog.Builder(callerContext)
					.setTitle("Error")
					.setMessage(loadTask.message)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                        finish();
	                        dialog.cancel();
	                   }
	               })
			      .show();
			}
			else {
				Utilities.showTitleAndMessageDialog(callerContext, formH.getNameForm(),
						"Formulario "+formH.getNameForm()+" cargado correctamente");
				postTaskFinished();
			}
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	
	private String filename;
	private String filepath;
	
	private static ProgressDialog pDialog;
	private LoadFormTask loadTask;
	private IStandardTaskListener listener;
	
	private static FormHandler formH;
	
	private SharedPreferences prefs;
	
	LinearLayout vista;
	
	Gallery g;
	ImageAdapterPhoto imageAdapter;
	
	/** Adaptador de la lista de páginas */
	ListAdapter lAdapter;
	ArrayList<String> mArray;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Aqui debemos conocer el nombre del fichero
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        	filename = bundle.getString(FormActivity.FILE_NAME);
        	filepath = bundle.getString(FormActivity.FILE_PATH);
        }
        else {
        	Utilities.showToast(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT);
        	finish();
        }
        initConfig();
        
        /** to use the custom title */
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.custom_title);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        
        final Object data = getLastNonConfigurationInstance();
        
        // The activity is starting for the first time
        if (data == null) {
            myLoadForm();
        } else {
            // El viewFlipper ya existe
        }
	}
	
	/*
	 * Establece los colores según el tema del programa
	 */
	private void setColors (int idTheme) {
		switch (idTheme) {
		// Oscuro
		case 0: Utilities.background = getResources().getColor(R.color.BackgroundDark);
				Utilities.fontColor = getResources().getColor(R.color.TextColorDark);
			break;
		// Calro
		case 1: Utilities.background = getResources().getColor(R.color.BackgroundLigth);
				Utilities.fontColor = getResources().getColor(R.color.TextColorLigth);
				/* Utilities.background = Color.WHITE;
				Utilities.fontColor = Color.argb(20, 25, 25, 25); */
			break;
			default:
				Utilities.background = R.color.SoftGreen;
				Utilities.fontColor = R.color.CandyRed;
				break;
		}
	}
	
	
	/**
	 * Initial config
	 */
	public void initConfig () {
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Log.v(TAG, "Esto: <"+GBSharedPreferences.__FORM_THEME_COLOR__+">");
		
		String pepe = prefs.getString(GBSharedPreferences.__FORM_THEME_COLOR__, "0");
		
		setColors (Integer.parseInt(prefs.getString(GBSharedPreferences.__FORM_THEME_COLOR__, "0")));
		
		Log.v(TAG, "Photo Big Size");
		Utilities.photoSizeBigEnable = prefs.getBoolean(GBSharedPreferences.__FORM_PHOTO_SIZE_BIG__, false);
		Log.v(TAG, "Photo Big Size Done");
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return viewFlipper;
	}
	
	private void myLoadForm () {
        setContentView(R.layout.flipper_question);
		setTitle(getString(R.string.app_name)+ " > " + filename);
        
		pDialog = ProgressDialog.show(this, "Working", "Loading form "+filename);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
				
		/*** Flipper *********/
	    viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
	    slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
	    slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	    slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
	    slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
	    
	    
	    gestureDetector = new GestureDetector(new MyGestureDetector());
	    gestureListener = new View.OnTouchListener() {
	    	public boolean onTouch(View v, MotionEvent event) {
	    		if (gestureDetector.onTouchEvent(event)) {
	    			return true;
	    		}
	    		return false;
	    	}
	    };
	    /**********************/
		
        
        loadTask = new LoadFormTask();
        loadTask.setContext(getApplicationContext());
        loadTask.setListener(new FormsLoader_FormsTaskListener(getApplicationContext(), this));
        
        loadTask.execute(filepath);
	}
	
	private void inflateFirstPage() {
		/* Insert first page of the form */
		ViewFlipper.inflate(getApplicationContext(), R.layout.first_page_flipper, viewFlipper);
		LinearLayout lL = (LinearLayout) findViewById(R.id.FormLayoutInit);
		lL.setBackgroundColor(Utilities.background);
		
		/** Rellenamos el Titulo y la descripción del formulario */
		/*** Colocamos texto en el viewFlipper */
		TextView tView = (TextView)findViewById(R.id.TitleForm);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.form_loaded, formH.getNameForm()));
		tView = (TextView)findViewById(R.id.FormVersion);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.form_version, formH.getVersionForm()));
		tView = (TextView)findViewById(R.id.FormDescription);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(formH.getDescription());
		tView = (TextView)findViewById(R.id.TextFingerMov);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.help_form_mov));
		/**********/		
	}
	
	private void inflateLastPage() {
		/* Insert last page of the form */
		ViewFlipper.inflate(getApplicationContext(), R.layout.last_page_flipper, viewFlipper);
		LinearLayout lL = (LinearLayout) findViewById(R.id.FormLayoutEnd);
		lL.setBackgroundColor(Utilities.background);	
	}
	
	private void postTaskFinished() {
		inflateFirstPage();
		setFlipperPages();
		inflateLastPage();
		
		setNumPage();
		
		setArrayListPage();
	}
	
	private void setNumPage () {
	
		final TextView leftText = (TextView) findViewById(R.id.left_text);
		final TextView rightText = (TextView) findViewById(R.id.right_text);
		
    	int page = viewFlipper.getDisplayedChild();
    	int max_page = viewFlipper.getChildCount();
    	
		if ((page >= 0) && (page < max_page)) {
			if ((page > 0) && (page < (max_page-1))) {
				leftText.setText(formH.getNameForm()+" > "+formH.getNamePage(page-1));
			}
			else {
				leftText.setText(formH.getNameForm());
			}
        	rightText.setText("Página: "+(page+1)+"/"+max_page);
		} else {
			rightText.setText("");
		}
	}
	
	private void setFlipperPages () {
		//Context context = FormActivity.this;
		Context context = getApplicationContext();
		QuestionWidget wdget;
		
	    if (formH != null) {
	    	for (int page=0; page < formH.getNumPages(); page++) {
	    	
	    		PageType mType = (formH.getPage(page)).getPageType();
	    		if (mType == null) {
	    			Log.e(TAG, "La página "+page+" no tiene tipo");
	    		}
	    		switch (mType) {
	    		
	    			case PHOTO: wdget = new MediaWidget (context, (ViewGroup)viewFlipper);
	    						viewFlipper.addView((View) wdget, page+1);
	    						
	    						// Reference the Gallery view
	    				        g = (Gallery) ((View)wdget).findViewById(R.id.gallery);
	    				        Log.v(TAG, "findView -> "+((View)wdget).findViewById(R.id.gallery));
	    				        
	    				        // Set the adapter to our custom adapter (below)
	    				        imageAdapter = new ImageAdapterPhoto(getApplicationContext());
	    				        g.setAdapter(imageAdapter);
	    				        
	    				        // Set a item click listener, and just Toast the clicked position
	    				        g.setOnItemClickListener(new OnItemClickListener() {
	    				            public void onItemClick(AdapterView parent, View v, int position, long id) {
	    				                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
	    				            }
	    				        });
	    				        
	    				        
	    				        
	    				        // ************  Makephoto button
	    				        Button button = (Button) ((View)wdget).findViewById (R.id.takePhotoButton);
	    				        button.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										//imageAdapter.addPhoto();
										Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
										
										if (Utilities.photoSizeBigEnable) {
											intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
											startActivityForResult(intent, CAMERA_ACTIVITY);
										} else {
											startActivityForResult(intent, CAMERA_ACTIVITY);
										}
									}
	    				        	
	    				        });
	    				        
	    				        // ************* get photo from gallery
	    				        button = (Button) ((View)wdget).findViewById (R.id.loadFromGalleryButton);
	    				        button.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										imageAdapter.addPhotoFromGallery();
										Intent intent = new Intent();
										intent.setType("image/*");
										intent.setAction(Intent.ACTION_GET_CONTENT);
										startActivityForResult(intent, GALLERY_ACTIVITY);
									}
	    				        	
	    				        });
	    				        
	    				        // ************* clear all photos
	    				        button = (Button) ((View)wdget).findViewById (R.id.clearButton);
	    				        button.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										imageAdapter.clearPhotos();
										reload();
									}
	    				        	
	    				        });
	    				        
	    				        TextView texto = (TextView) ((View)wdget).findViewById (R.id.loadPhotos);
	    				        texto.setTextColor(Utilities.fontColor);
	    				        texto.setText(getString(R.string.no_load_photos));
	    				        
	    				        // We also want to show context menu for longpressed items in the gallery
	    				        registerForContextMenu(g);
	    						break;
	    			case AUDIO:	
	    			case VIDEO: 
	    			case LOCATION: break;
	    			case DATA:
	    				ScrollView scrollV = new ScrollView(context);
	    				scrollV.setBackgroundColor(Utilities.background);
	    				
	    				LinearLayout vistaR = new LinearLayout(context);
	    	    		vistaR.setPadding(5, 5, 5, 5);
	    	    		vistaR.setOrientation(LinearLayout.VERTICAL);
	    	    		vistaR.setHorizontalScrollBarEnabled(true);
	    	    		vistaR.setVerticalScrollBarEnabled(true);
	    	    		vistaR.setBackgroundColor(Utilities.background);
	    	    		
	    	    		int numQuestions = formH.getNumQuestionOfPage(page);
	    	    		
	    	    		for (int question=0; question < numQuestions; question++) {
	    	    			/** create the appropriate widget depending on the question */
	    	    			wdget = CreateWidget.createWidget(formH.getQuestionOfPage(question, page), this, (ViewGroup)viewFlipper);
	    	    			vistaR.addView((View)wdget);
	    	    		}
	    	    		
	    	    		scrollV.addView(vistaR);
	    	    		viewFlipper.addView(scrollV, page+1); 			
	    			default: break;
	    		
	    		}
	    		
	    		
	    	}
	    }
	}
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
            	Log.i(TAG, "onFlinggggggg");
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() < (viewFlipper.getChildCount()-1)) {
                		nextPage();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_rigth), Toast.LENGTH_SHORT);
                	}
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() > 0) {
                		previousPage();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_left), Toast.LENGTH_SHORT);
                	}
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }

	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(0, VIEW_PHOTO, 0, "Ver foto");
    	menu.add(0, DELETE_PHOTO, 0, "Borrar foto seleccionada");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
        	case VIEW_PHOTO:
            	Utilities.showToast(getApplicationContext(), "No implementada todavía", Toast.LENGTH_LONG);
        		break;
        	case DELETE_PHOTO:
        		imageAdapter.deletePhoto(info.position);
                reload();
        		break;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.removeItem(MENU_PREVIOUS_PAGE);
    	menu.removeItem(MENU_NEXT_PAGE);
    	menu.removeItem(MENU_SAVE_COMPLETE);
    	menu.removeItem(MENU_SAVE_INCOMPLETE);
    	menu.removeItem(MENU_JUMP_TO);

        menu.add(0, MENU_JUMP_TO, 0, "Ir a la página ...").setIcon(
                android.R.drawable.ic_menu_view).setEnabled(true);
        menu.add(0, MENU_PREVIOUS_PAGE, 0, "Previous Page").setIcon(
                android.R.drawable.ic_media_previous).setEnabled(
                		viewFlipper.getDisplayedChild() != 0 ? true : false);
        menu.add(0, MENU_NEXT_PAGE, 0, "Next Page").setIcon(
                android.R.drawable.ic_media_next).setEnabled(
                		viewFlipper.getDisplayedChild() != (viewFlipper.getChildCount()-1) ? true : false);
        menu.add(0, MENU_SAVE_COMPLETE, 0, "Save as Complete").setIcon(
                android.R.drawable.ic_menu_save).setEnabled(false);
        menu.add(0, MENU_SAVE_INCOMPLETE, 0, "Save as Incomplete").setIcon(
                android.R.drawable.ic_menu_save).setEnabled(false);

		return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case MENU_JUMP_TO: listDialog();
        		break;
        	case MENU_PREVIOUS_PAGE: previousPage();
        		break;
        	case MENU_NEXT_PAGE: nextPage();
        		break;	
        	
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void previousPage() {
		viewFlipper.setInAnimation(slideRightIn);
		viewFlipper.setOutAnimation(slideRightOut);
		viewFlipper.showPrevious();
		
		setNumPage();
    }
    
    private void nextPage() {
		viewFlipper.setInAnimation(slideLeftIn);
		viewFlipper.setOutAnimation(slideLeftOut);
		viewFlipper.showNext();
		
		setNumPage();
    }
    
    /** 
     * Place the form on the page set
     * @param page
     */
    private void goToPage (int page) {
    	Utilities.showToast(getApplicationContext(), "Debo ir a la página "+page, Toast.LENGTH_LONG);
    	
		viewFlipper.setInAnimation(slideLeftIn);
		viewFlipper.setOutAnimation(slideLeftOut);
		viewFlipper.setDisplayedChild(page);
		
		setNumPage();
    }
    
    /**
     * 
     */
    private void setArrayListPage () {
    	mArray = formH.getAllNamesOfPages();
    	Log.i(TAG,"Establecidas "+mArray.size());
    }
    
    
    /** Simple dialog list to select the page.
     * Not implemented yet.
     */
    private void listDialog () {
    	
    	Utilities.showToast(getApplicationContext(), "Existen "+(formH.getNumPages()+2)+" páginas", Toast.LENGTH_LONG);
    	
    	//With (android.R.layout.simple_list_item_1) can't see the text
    	lAdapter = new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArray);
    	
    	AlertDialog dial = new AlertDialog.Builder(FormActivity.this)
        .setTitle("Ir a la página")
        .setInverseBackgroundForced(true)
        .setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                goToPage(which);
            }
        })
        .create();
    	
    	dial.show();
    }
    
	/**
	 * Retrieves the returned image from the Intent, inserts it into the MediaStore, which
	 *  automatically saves a thumbnail. Then assigns the thumbnail to the ImageView.
	 *  @param requestCode is the sub-activity code
	 *  @param resultCode specifies whether the activity was cancelled or not
	 *  @param intent is the data packet passed back from the sub-activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.i(TAG, "Result code = " + resultCode);

		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {


		/* Debo diferenciar desde que página fue llamada la cámara o la galería, para incluirla en su lugar */
		
		case CAMERA_ACTIVITY:
			Bundle b = intent.getExtras();
			Bitmap bm = (Bitmap) b.get("data");
			imageAdapter.addPhoto(bm);

			if (b != null && b.containsKey(MediaStore.EXTRA_OUTPUT)) { // large image?
				if (b.containsKey(MediaStore.EXTRA_OUTPUT)) { // large image?
					Log.i(TAG, "This is a large image");
					//showToast(this,"Large image");
				// Should have to do nothing for big images -- should already saved in MediaStore ... but
				MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
				} else {
					Log.i(TAG, "This is a small image");
				//showToast(this,"Small image");
				//MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
				}
			}
			break;
			
		case GALLERY_ACTIVITY:
			
			Uri uri = intent.getData();
		    ContentResolver cr = getApplicationContext().getContentResolver();
		    try {
				Bitmap bitmap = BitmapFactory.decodeStream( cr.openInputStream(uri) );
				imageAdapter.addPhoto(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			break;
		}
		reload();
		//displayGallery();
	}
	
	private void reload () {
		g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(imageAdapter);
		
		TextView texto = (TextView) findViewById(R.id.loadPhotos);
		
		switch (imageAdapter.getCount()) {
			case 0: texto.setText(getString(R.string.no_load_photos));
				break;
			case 1: texto.setText(getString(R.string.load_photo,""+imageAdapter.getCount()));
				break;
			default: texto.setText(getString(R.string.load_photos,""+imageAdapter.getCount()));
		}
	}
}
