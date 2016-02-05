package com.pirateria;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

    // Constant declaration for file size type conversions
    public static final int FILE_SIZE = 1024 * 1024 * 10;// 10 MB

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int CHOOSE_PHOTO_REQUEST = 2;
	public static final int CHOOSE_VIDEO_REQUEST = 3;
	
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	
	protected Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int position) {
			switch(position){
			case 0: 
				// Take Photo
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if(mMediaUri ==null){
					Toast.makeText(MainActivity.this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
				}
				else{
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: 
				// Take Video
                Intent takeVideoIntent = new Intent(
                        MediaStore.ACTION_VIDEO_CAPTURE);
                // Using mMediaUri
                mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                if (mMediaUri == null) {
                    // Display error
                    Toast.makeText(MainActivity.this,
                            "Problem in accessing the device external storage",
                            Toast.LENGTH_LONG).show();
                } else {
                    takeVideoIntent
                            .putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    // Limit the video duration to 10s
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,
                            10);
                    // Set the quality of the video
                    // 0=Low quality
                    // 1=High quality
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

                    // Starting activity for obtaining the result (ie the video)
                    startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);

                }
				break;
			case 2: 
				// Choose Photo
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // Set type of file to choose ie only photos
                choosePhotoIntent.setType("image/*");
                startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);

                break;
			case 3: 
				// Choose Video
                Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // Set type of file to choose ie only videos
                chooseVideoIntent.setType("video/*");
                // Warn users to not attach videos of size more than 10mb
                Toast.makeText(MainActivity.this,
                        "Attach Videos of size less than or equal to 10MB",
                        Toast.LENGTH_LONG).show();
                startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
				break;
				
			}
			
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			//First, we check whether external storage is mounted or not
			if(isExternalStorageMounted()){
				//get URI
				
				//1.Get the External storage directory
				String appname = MainActivity.this.getString(R.string.app_name);
				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appname);
				//2. create a subdirectory
				if(!mediaStorageDir.exists()){
					if(!mediaStorageDir.mkdirs()){
						Log.e(TAG, "There is a problem creating the directory");
						return null;
					}
				}
				//3. Create a file name
				//4. Create the file
				File mediaFile;
				Date now = new Date();
				String timestamp = new SimpleDateFormat("yyyyMMdd_hhmmss",Locale.US).format(now);
				String path = mediaStorageDir.getPath() + File.separator;
				if(mediaType == MEDIA_TYPE_IMAGE){
					mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
				}
				else if(mediaType == MEDIA_TYPE_VIDEO){
					mediaFile = new File(path + "VID_" + timestamp + ".mp4");
				}
				else{
					return null;
				}

				// For debugging purposes, storing the file Uri
				Log.d(TAG, "File" + Uri.fromFile(mediaFile));

				//5.return File URI
				return Uri.fromFile(mediaFile);
						
			}
			else{
				// No mounted storage
				return null;	
			}
			
		}

		private boolean isExternalStorageMounted() {
			String state = Environment.getExternalStorageState();
			if(state.equals(Environment.MEDIA_MOUNTED)){
				return true;
			}
			else{
				return false;
			}
			
		}
	};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser==null)
		{
		navigateToLoginScreen();
		}
		else
		{
			Log.i(TAG, currentUser.getUsername());
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private void navigateToLoginScreen() {
		Intent Loginlayout = new Intent(this,LoginActivity.class);
		Loginlayout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Loginlayout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(Loginlayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		  case R.id.logout_label :
			ParseUser.logOut();
			navigateToLoginScreen();
		
		  case R.id.action_edit_friends :
		    Intent intent = new Intent(this,EditFriendsActivity.class);
			startActivity(intent);
		
		  case R.id.camera_icon :
			  AlertDialog.Builder builder = new AlertDialog.Builder(this);
			  builder.setItems(R.array.camera_choices, mOnClickListener);
			  AlertDialog dialog = builder.create();
			  dialog.show();
		
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}



    // Defining onActivityResult listener
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // If condition to check for various values of resultCode
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO_REQUEST
                    || requestCode == CHOOSE_VIDEO_REQUEST) {
                // Choose photo or video from gallery or recent(android 4.4)
                if (data == null) {
                    // Show the user an error message
                    Toast.makeText(MainActivity.this,
                            "Sorry,there was an error", Toast.LENGTH_LONG)
                            .show();
                } else {
                    // Data is not null
                    mMediaUri = data.getData();
                }
                if (requestCode == CHOOSE_VIDEO_REQUEST) {
                    // Ensure file size is less than 10 MB
                    int fileSize = 0;
                    // Declare an inputstream which coverts byte by byte
                    // GetContentResolver resolves the Uri of the video
                    // We must always close the inputstreams to avoid memory
                    // leaks
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(
                                mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        // Catch FileNotFoundException
                        // Show the error message to the user
                        Toast.makeText(MainActivity.this,
                                "There was a problem with the selected file",
                                Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        // Catch IOexception
                        Toast.makeText(MainActivity.this,
                                "There was a problem with the selected file",
                                Toast.LENGTH_LONG).show();
                        return;

                    }
                    // The following block of code always gets executed
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            // Intentionally Blank
                        }
                    }
                    // File size checking and warning system
                    if (fileSize >= FILE_SIZE) {
                        // Show an error message to the user
                        Toast.makeText(MainActivity.this,
                                "Selected file is too large.",
                                Toast.LENGTH_LONG).show();
                        // Return so as to not to proceed further
                        return;

                    }

                }

            } else {
                // Add it to the gallery
                // We will store files in the gallery using a new intent and
                // then
                // broadcasting it to the gallery
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                // Set Uri of the intent
                mediaScanIntent.setData(mMediaUri);
                // Send Broadcast to the gallery application
                sendBroadcast(mediaScanIntent);
            }
            // Starts recipients activity
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            // Transfer uri as data to the recipients activity.
            recipientsIntent.setData(mMediaUri);
            // Determining the file type
            String fileType;
            if (requestCode == CHOOSE_PHOTO_REQUEST
                    || requestCode == TAKE_PHOTO_REQUEST) {
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            // Add the fileType data to the intent
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            // Start the Activity
            startActivity(recipientsIntent);

        } else if (resultCode != RESULT_CANCELED) {
            // Show the user error message
            Toast.makeText(this, "Sorry there was an error", Toast.LENGTH_LONG)
                    .show();

        }

    }
	}


