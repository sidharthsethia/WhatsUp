//Custom list adapter
package com.pirateria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

//Array adapter is generic type
public class UserAdpater extends ArrayAdapter<ParseUser> {

	// Declaration of member variables

	protected Context mContext;
	protected List<ParseUser> mUsers;

	// Create constructor
	public UserAdpater(Context context, List<ParseUser> users) {
		// Set the custom layout here
		super(context, R.layout.user_item, users);
		mContext = context;
		mUsers = users;
	}

	// Define the getView method
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// Recycling list items to increase efficiency
		// Inflate the layout file to view
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.user_item, null);
			holder = new ViewHolder();

			holder.userImage = (ImageView) convertView
					.findViewById(R.id.emptyUserIcon);

			holder.textLabel = (TextView) convertView.findViewById(R.id.userId);
			holder.checkImage = (ImageView) convertView
					.findViewById(R.id.selectedUserIcon);
			convertView.setTag(holder);

		} else {
			// convertView already created
			holder = (ViewHolder) convertView.getTag();

		}
		// Now set the icons
		ParseUser user = mUsers.get(position);
		// Get the email of the user
		String email = user.getEmail().toLowerCase();
		if (email.equals("")) {
			holder.userImage.setImageResource(R.drawable.friendicon);
		} else {
			// Creating the MD5 hash for the email
			String hash = MD5Util.md5Hex(email);
			// Getting the gravatar URL
			String gravatarURL = "http://www.gravatar.com/avatar/" + hash
					+ "?s=180&d=404";
			// Use the picasso lib to show the image
			Picasso.with(mContext).load(gravatarURL)
					.placeholder(R.drawable.friendicon).into(holder.userImage);

		}

		holder.textLabel.setText(user.getUsername());

		// Define a gridview parent and use it to check whether an element is
		// checked or not
		GridView gridView = (GridView) parent;
		if (gridView.isItemChecked(position)) {
			holder.checkImage.setVisibility(View.VISIBLE);
		} else {
			holder.checkImage.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView userImage;
		TextView textLabel;
		ImageView checkImage;
	}

	public void refill(List<ParseUser> users) {
		// 1.Clear the list
		mUsers.clear();
		// 2.Add usernames
		mUsers.addAll(users);
		// Notify the user about the change
		notifyDataSetChanged();

	}
}
