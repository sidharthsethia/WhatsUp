//Custom list adapter
package com.pirateria;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

//Array adapter is generic type
public class MessageAdapter extends ArrayAdapter<ParseObject> {

	// Declaration of member variables

	protected Context mContext;
	protected List<ParseObject> mMessages;

	// Create constructor
	public MessageAdapter(Context context, List<ParseObject> messages) {
		// Set the custom layout here
		super(context, R.layout.list_item, messages);
		mContext = context;
		mMessages = messages;
	}

	// Define the getView method
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// Recycling list items to increase efficiency
		// Inflate the layout file to view
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
			holder = new ViewHolder();
			holder.iconImage = (ImageView) convertView
					.findViewById(R.id.iconimage);
			holder.textLabel = (TextView) convertView
					.findViewById(R.id.iconText);
			convertView.setTag(holder);
			holder.timeLabel = (TextView) convertView
					.findViewById(R.id.timeLabel);

		} else {
			// convertView already created
			holder = (ViewHolder) convertView.getTag();

		}
		// Now set the icons
		ParseObject message = mMessages.get(position);
		
		// To set the time the message was received at
		Date createdAt = message.getCreatedAt();
		// get current time
		long now = new Date().getTime();
		// get the string value
		String convertedDate = DateUtils.getRelativeTimeSpanString(
				createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS)
				.toString();
		// set the string convertedDate in the textview
		holder.timeLabel.setText(convertedDate);
		
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(
				ParseConstants.TYPE_IMAGE)) {
			holder.iconImage.setImageResource(R.drawable.picture);
		} else {
			holder.iconImage.setImageResource(R.drawable.play);
		}
		holder.textLabel.setText(message
				.getString(ParseConstants.KEY_SENDER_NAME));

		return convertView;
	}

	private static class ViewHolder {
		ImageView iconImage;
		TextView textLabel;
		TextView timeLabel;
	}

	public void refill(List<ParseObject> messages) {
		// 1.Clear the list
		mMessages.clear();
		// 2.Add data
		mMessages.addAll(messages);
		// Notify the user about the change
		notifyDataSetChanged();

	}
}
