package edu.umich.yourcast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {

	TextView settingsButton, yourcastText, twitterText, broadcastText;
	CheckBox yourcastCheck, twitterCheck;
	Button saveButton; 

	String access_token, access_token_secret; 
	boolean logged_in, twitter_broadcast, yourcast_broadcast;  
	
	// Twitter Login.
	private TwitterLogin twitterLogin; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		twitterLogin = new TwitterLogin(this); 
		
		// Get information from the Intent. 
		Intent intent = getIntent();
		access_token = intent.getStringExtra(Constants.PREF_KEY_OAUTH_TOKEN);
		access_token_secret = intent.getStringExtra(Constants.PREF_KEY_OAUTH_SECRET);
		logged_in = intent.getBooleanExtra(Constants.PREF_KEY_TWITTER_LOGIN, false);
		twitter_broadcast = intent.getBooleanExtra(Constants.TWITTER_BROADCAST, false); 
		yourcast_broadcast = intent.getBooleanExtra(Constants.YOURCAST_BROADCAST, true); 
		

		settingsButton = (TextView) findViewById(R.id.settingsbutton);
		yourcastText = (TextView) findViewById(R.id.yourcastText);
		twitterText = (TextView) findViewById(R.id.twitterText);
		broadcastText = (TextView) findViewById(R.id.broadcastText);

		yourcastCheck = (CheckBox) findViewById(R.id.yourcastCheck);
		twitterCheck = (CheckBox) findViewById(R.id.twitterCheck);
		
		saveButton = (Button) findViewById(R.id.saveButton); 
		
		if (twitter_broadcast) {
			twitterCheck.setChecked(true); 
		}
		
		if (yourcast_broadcast) {
			twitterCheck.setChecked(true); 
		}
		
		
		// Parse on callback. 
		if (!logged_in) {
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
		

		
		



	}

}
