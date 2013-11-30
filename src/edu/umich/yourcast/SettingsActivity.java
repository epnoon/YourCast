package edu.umich.yourcast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	String TAG = "TwitterSettings"; 

	TextView settingsButton, yourcastText, twitterText, broadcastText;
	CheckBox yourcastCheck, twitterCheck;
	Button saveButton; 

	String access_token, access_token_secret; 
	boolean logged_in, twitter_broadcast, yourcast_broadcast; 
	
	// Shared Preferences. 
	private SharedPreferences mSharedPreferences;
	
	// Twitter Login.
	private TwitterLogin twitterLogin; 

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Thread for Twitter Login. 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"TwitterLogin", MODE_PRIVATE);	

		getPreferences(); 
		
		settingsButton = (TextView) findViewById(R.id.settingsbutton);
		yourcastText = (TextView) findViewById(R.id.yourcastText);
		twitterText = (TextView) findViewById(R.id.twitterText);
		broadcastText = (TextView) findViewById(R.id.broadcastText);

		yourcastCheck = (CheckBox) findViewById(R.id.yourcastCheck);
		twitterCheck = (CheckBox) findViewById(R.id.twitterCheck);
		
		saveButton = (Button) findViewById(R.id.saveButton); 
		
		if (twitter_broadcast) {
			twitterCheck.setChecked(true); 
		} else {
			twitterCheck.setChecked(false); 
		}
		
		if (yourcast_broadcast) {
			yourcastCheck.setChecked(true); 
		} else {
			yourcastCheck.setChecked(false); 
		}
		
		Log.d(TAG, "Login: " + String.valueOf(logged_in)); 

		if (logged_in) {
			twitterLogin = new TwitterLogin(this, access_token, access_token_secret); 
		} else {
			twitterLogin = new TwitterLogin(this); 
			// Parse if call back. 
			twitterLogin.parseURI(getIntent().getData()); 
			if (twitterLogin.getLoggedIn()) {
				logged_in = twitterLogin.getLoggedIn(); 
				access_token = twitterLogin.getAccessToken(); 
				access_token_secret = twitterLogin.getAccessTokenSecret(); 
				twitterCheck.setChecked(true); 
				twitter_broadcast = true; 
			} 
		}
		
		
		if (logged_in) {
			twitterText.setText("Twitter (as " + twitterLogin.getName() + ")"); 
		} else {

			twitterText.setText("Twitter (not logged in)"); 
		}

		// Title
		Typeface tf = Typeface
				.createFromAsset(getAssets(), Constants.FONT_PATH_B);
		settingsButton.setTypeface(tf);
		yourcastText.setTypeface(tf);
		twitterText.setTypeface(tf);
		broadcastText.setTypeface(tf);
		saveButton.setTypeface(tf); 

		twitterCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (twitterCheck.isChecked() && !logged_in) {
					twitterLogin.login(); 
				} else if (twitterCheck.isChecked() && logged_in) {
					twitter_broadcast = true; 
				} else {
					twitter_broadcast = false; 
				}
			}
		});
		
		yourcastCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (yourcastCheck.isChecked()) {
					yourcast_broadcast = true; 
				} else {
					yourcast_broadcast = false; 
				}
			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMain(); 
			}
		});
		
	}
	
	private void goToMain() {
		if (!twitter_broadcast) {
			twitterLogin.logout(); 
			logged_in = false; 
			access_token = "";
			access_token_secret = "";
		}
		savePreferences(); 
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	private void getPreferences() {
		Log.d(TAG, "Getting Preferences"); 
		access_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, ""); 
		access_token_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, ""); 
		logged_in = mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false); 
		twitter_broadcast = mSharedPreferences.getBoolean(Constants.TWITTER_BROADCAST, false); 
		yourcast_broadcast = mSharedPreferences.getBoolean(Constants.YOURCAST_BROADCAST, false); 
		Log.d(TAG, "token: " + access_token); 
		Log.d(TAG, "secret: " + access_token_secret); 
		Log.d(TAG, "twitter: " + String.valueOf(twitter_broadcast)); 
		Log.d(TAG, "yourcast: " + String.valueOf(yourcast_broadcast)); 
	}
	
	private void savePreferences() {
		Log.d(TAG, "Saving Preferences"); 
		Editor e = mSharedPreferences.edit(); 
		e.putString(Constants.PREF_KEY_OAUTH_TOKEN, access_token); 
		e.putString(Constants.PREF_KEY_OAUTH_SECRET, access_token_secret); 
		e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, logged_in);
		e.putBoolean(Constants.TWITTER_BROADCAST, twitter_broadcast); 
		e.putBoolean(Constants.YOURCAST_BROADCAST, yourcast_broadcast); 
		e.commit(); 
	}


}
