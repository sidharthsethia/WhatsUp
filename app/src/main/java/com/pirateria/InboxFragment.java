package com.pirateria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends ListFragment {
	// Member variable for list of messages
	protected List<ParseObject> mMessages;
	// Member for swiperefreshlayout
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeRefreshLayout);
		// set the on refresh listener method
		// mRefreshListener defined below
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		// set colors for the animation
		mSwipeRefreshLayout.setColorScheme(R.color.swipe1, R.color.swipe2,
				R.color.swipe3, R.color.swipe4);

		return rootView;
	}

	// Defining OnResume method in order to refresh the inbox from time to time
	// In a fragment onResume is supposed to be public
	@Override
	public void onResume() {
		super.onResume();
		// For progress bar we need the activity method
		getActivity().setProgressBarIndeterminateVisibility(true);
		retrieveMessages();
	}

	private void retrieveMessages() {
		// Parse query is a generic type, so we need to specify the type of data
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_MESSAGES);
		// where clauses are used to check for required data in the dtatbase
		query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser
				.getCurrentUser().getObjectId());
		// Arrange the messages according to latest
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		// Run query
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				// to stop the animation
				if (mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.setRefreshing(false);
				}
				// Check for exceptions
				if (e == null) {
					// success
					// we found messages
					mMessages = messages;
					// Adapt the listView to show the messages data
					String[] usernames = new String[mMessages.size()];
					// Set a loop to set the different elements of the
					// string
					// array
					int i = 0;
					for (ParseObject message : mMessages) {
						usernames[i] = message
								.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					// Setting the list adpater to display the results
					if (getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(
								getListView().getContext(), mMessages);
						setListAdapter(adapter);
					} else {
						// refill the adapter !!
						((MessageAdapter) getListView().getAdapter())
								.refill(mMessages);
					}
				}

			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Create a parse object
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		// get Uri for the image sent
		Uri fileUri = Uri.parse(file.getUrl());
		// Check for file type
		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			// return image uri by starting a new activity
			Intent intent = new Intent(getActivity(), ViewImagesActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		} else {
			// return video uri and start activity
			Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
			intent.setDataAndType(fileUri, "video/*");
			startActivity(intent);

		}
		// Delete the message!
		// Check for the recipient
		List<String> ids = message.getList(ParseConstants.KEY_RECIPIENTS_IDS);
		// Check the count of recipients
		if (ids.size() == 1) {
			// Last recipient - remove the whole message
		} else {
			// remove the recipient and save in the Parse backend
			ids.remove(ParseUser.getCurrentUser().getObjectId());
			// Create a collection of objects to remove
			ArrayList<String> idsToRemove = new ArrayList<String>();
			idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

			message.removeAll(ParseConstants.KEY_RECIPIENTS_IDS, idsToRemove);
			// save changes to backend
			message.saveInBackground();
		}
	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			// call method to retrieve messages
			retrieveMessages();
		}
	};
}
