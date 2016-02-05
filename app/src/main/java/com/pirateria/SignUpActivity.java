package com.pirateria;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

	protected EditText mUsername;
	protected EditText mPassword;
	protected EditText mEmail;
	protected Button mSignUpButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		mUsername = (EditText)findViewById(R.id.Username);
		mPassword = (EditText)findViewById(R.id.password);
		mEmail = (EditText)findViewById(R.id.EmailId);
		
		mSignUpButton = (Button)findViewById(R.id.signup_button);
		mSignUpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
				String email = mEmail.getText().toString();
				username.trim();
				password.trim();
				email.trim();
				
				if(username.isEmpty() || password.isEmpty() || email.isEmpty())
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
				builder.setMessage(R.string.signup_error_message)
					.setTitle(R.string.Signup_error_title)
					.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else
			{
				//create user
				ParseUser user = new ParseUser();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				 
				user.signUpInBackground(new SignUpCallback() {
				  public void done(ParseException e) {
				    if (e == null) {
				      Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
				      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				      startActivity(intent);
				      
				      
				    } else {
				      // Sign up didn't succeed. Look at the ParseException
				      // to figure out what went wrong
				    	AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
						builder.setMessage(e.getMessage())
							.setTitle(R.string.Signup_error_title)
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
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
