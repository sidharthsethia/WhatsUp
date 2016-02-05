package com.pirateria;



import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class WhatsUpApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		  Parse.initialize(this, "8ktzdZvH0zd5AUUKJlOviOsPTxTZ4RGFt43xRcvr", "MMOsBOnM4e1EPyxL22zPkYnTGzukMUXvRoUmGf5U");
		  
		}

	// Declaring the updateParseInstallation method
	public static void updateParseInstallation(ParseUser user) {
		// Declare a parse installation variable
		ParseInstallation intst = ParseInstallation.getCurrentInstallation();
		intst.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		// save this installation to parse backend
		intst.saveInBackground();
	}
}
