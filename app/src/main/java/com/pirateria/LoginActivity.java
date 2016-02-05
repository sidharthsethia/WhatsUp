package com.pirateria;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	protected TextView mSignupTextView;
	protected EditText mUsername;
	protected EditText mPassword;
	protected Button mLoginButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		mSignupTextView = (TextView) findViewById(R.id.signup_label);
		
		mSignupTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intent);
			}
		});
		mUsername = (EditText)findViewById(R.id.UsernameField);
		mPassword = (EditText)findViewById(R.id.PasswordField);
		
		
		mLoginButton = (Button)findViewById(R.id.Login_button);
		mLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
	
				username.trim();
				password.trim();
	
				
				if(username.isEmpty() || password.isEmpty())
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				builder.setMessage(R.string.login_error_message)
					.setTitle(R.string.login_error_title)
					.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else
			{
				ParseUser.logInInBackground(username,password, new LogInCallback() {
					  public void done(ParseUser user, ParseException e) {
					    if (e == null) {
					    	Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						      startActivity(intent);
					    } else {
					      // Signup failed. Look at the ParseException to see what happened.
					    	AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
							builder.setMessage(e.getMessage())
								.setTitle(R.string.login_error_title)
								.setPositiveButton(android.R.string.ok, null);
							AlertDialog dialog = builder.create();
							dialog.show();
					    }
					  }
					});
			}
			}
	
	});
		}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
}
