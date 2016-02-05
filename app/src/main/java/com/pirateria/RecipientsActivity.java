package com.pirateria;

//Activity for selecting recipients for sending photos and videos

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipientsActivity extends Activity {

	// Member Variable Declaration
	public static final String TAG = FriendsFragment.class.getSimpleName();
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendrelation;
	protected ParseUser mCurrentUser;
	protected MenuItem mMenuSend;
	protected Uri mMediaUri;
	protected String mFileType;
	// Declaring a grid view member variable
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Request window feature comes before setContentView
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		// Set visibility of the send button to true once there is a friend
		// selected
		// initialize the grid view
		mGridView = (GridView) findViewById(R.id.friendsGrid);

		// For setting the checking functionality on
		mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// set empty text
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);
		// Set on item click listener
		mGridView.setOnItemClickListener(mOnItemClickListener);
		// Get the Uri data being passed into this intent
		mMediaUri = getIntent().getData();
		// WE call getExtras as we put fileType as an extra into the
		// recipientsintent
		// getExtras returns a bundle.
		// We just require the fileType string hence we use getString
		mFileType = getIntent().getExtras().getString(
				ParseConstants.KEY_FILE_TYPE);
	}

	// Setting the onResume activity
	public void onResume() {
		super.onResume();
		// To define relationships
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendrelation = mCurrentUser
				.getRelation(ParseConstants.KEY_RELATION_FRIENDS);
		setProgressBarIndeterminateVisibility(true);
		// Declaring variable
		ParseQuery<ParseUser> query = mFriendrelation.getQuery();
		// Arrange in ascending order
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {

				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mFriends = friends;
					String[] usernames = new String[mFriends.size()];
					// Set a loop to set the different elements of the
					// string
					// array
					int i = 0;
					for (ParseUser user : mFriends) {
						usernames[i] = user.getUsername();
						i++;
					}
					// Showing the grid view
					// check whether gridView is empty or not
					if (mGridView.getAdapter() == null) {
						// Setting the custom grid adpater to display the
						// results
						UserAdpater adapter = new UserAdpater(
								RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
					} else {
						// refill the grid view if already created
						((UserAdpater) mGridView.getAdapter()).refill(mFriends);
					}

				} else {
					// Logging exception
					Log.e(TAG, e.getMessage());

				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		// Set the send item in the menu to the MenuItem member variable
		mMenuSend = menu.getItem(0);// since there is only one item
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_send) {
			// Upload the photo or video on the parse backend
			ParseObject message = createMessage();
			// check whether message is null or not
			if (message != null) {
				send(message);
				// Finish recipients activity to go back to Main Activity
				finish();
			} else {
				// error
				Toast.makeText(this, "Error with the file selected",
						Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Method to create the Parse Object message
	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		// Put the sender id
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser()
				.getObjectId());
		// Put the sender name
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser()
				.getUsername());
		// Put the recipients ids
		message.put(ParseConstants.KEY_RECIPIENTS_IDS, getRecipientsIds());
		// Put the file type
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
		// Create a byte Array variable for ParseFile creation
		byte[] fileBytes;
		// Now we'll use code from github
		fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
		// Check for any errors
		if (fileBytes == null) {
			// Error
			return null;
		} else {
			// Success
			// Check for file type
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				// Reduce the size of the image to less than 10MB
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);

			}
			String filename = FileHelper
					.getFileName(this, mMediaUri, mFileType);
			// Create parse file
			ParseFile file = new ParseFile(filename, fileBytes);
			// Attach this parse file to parse object message
			message.put(ParseConstants.KEY_FILE, file);
			return message;
		}

	}

	// Use arraylist as its preferred by parse to denote a collection of objects
	protected ArrayList<String> getRecipientsIds() {
		ArrayList<String> recipientsIds = new ArrayList<String>();
		// Loop to checked everyone who is checked
		for (int i = 0; i < mGridView.getCount(); i++) {
			if (mGridView.isItemChecked(i)) {
				// Add to recipientsIds
				// get the object id of the user at current position i
				recipientsIds.add(mFriends.get(i).getObjectId());
			}
		}
		return recipientsIds;

	}

	// Send message method
	// Sending message means saving it to the parse bak-end
	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// success
					// Show success message to user
					Toast.makeText(RecipientsActivity.this, "Message sent",
							Toast.LENGTH_LONG).show();
					// Send Push Notification
					sendPushNotification();
				} else {
					// Show the user the error message
					Toast.makeText(RecipientsActivity.this,
							"Error sending message", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			// Set the send button visible if items are checked
			if (mGridView.getCheckedItemCount() > 0) {
				mMenuSend.setVisible(true);
			} else {
				mMenuSend.setVisible(false);
			}
			ImageView checkedImageView = (ImageView) view
					.findViewById(R.id.selectedUserIcon);
			// To check whether the item is clicked or not
			if (mGridView.isItemChecked(position)) {
				// add recipient
				// getting the position of the user tapped
				// Show checked mark to notify users
				checkedImageView.setVisibility(View.VISIBLE);

			} else { // remove a recipient

				checkedImageView.setVisibility(View.INVISIBLE);

			}

		}
	};

	protected void sendPushNotification() {
		// Declare a Parse Query
		// Use this query to find out who should be notified
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		// For the collection we require the recipient ids
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());
		// We have to store the user's id in the ParseInstallation Object
		// We will do this in the done method of our login and signup activities
		// Send Push Notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, ParseUser
				.getCurrentUser().getUsername()));
		push.sendInBackground();

	}
}
