package edu.umich.yourcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MainActivity extends FragmentActivity implements
		NewGameDialog.NewGameDialogListener,
		WatchGameDialog.WatchGameDialogListener {
	private String TAG = "TWITTER"; 

	// Internet Connection detector
	private ConnectionDetector cd;

	// Twitter Login.
	private TwitterLogin twitterLogin; 

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Shared Preferences
	public static SharedPreferences mSharedPreferences;

	// Buttons
	ImageButton loginButton;
	TextView newGame, watchGame; 
	
	// Oauth stuff. 
	String access_token, access_token_secret; 
	boolean logged_in = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Thread for Twitter Login. 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		cd = new ConnectionDetector(getApplicationContext());
		twitterLogin = new TwitterLogin(this); 
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"TwitterLogin", MODE_PRIVATE);		

		// Check internet and keys. 
		doChecks(); 
		
		getPreferences(); 
		Log.d(TAG, String.valueOf(logged_in)); 
		Log.d(TAG, access_token); 
		Log.d(TAG, access_token_secret); 
		

		// Set Buttons.
		loginButton = (ImageButton) findViewById(R.id.loginButton);
		newGame = (TextView) findViewById(R.id.newgamebutton);
		watchGame = (TextView) findViewById(R.id.watchgamebutton);
		
		// Parse on callback. 
		if (!logged_in) {
			twitterLogin.parseURI(getIntent().getData()); 
			if (twitterLogin.getLoggedIn()) {
				Log.d(TAG, "Logged in successfully"); 
				logged_in = twitterLogin.getLoggedIn(); 
				access_token = twitterLogin.getAccessToken(); 
				access_token_secret = twitterLogin.getAccessTokenSecret(); 
				savePreferences(); 
			}
		}	 

		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), Constants.FONT_PATH);

		// Applying font
		newGame.setTypeface(tf);
		watchGame.setTypeface(tf);

		newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				newGameButtonClick(view);
			}
		});

		watchGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				watchGameButtonClick(view);
			}
		});
		
		// Twitter login button click event will call loginToTwitter() function
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Call login twitter function
				twitterLogin.login(); 
			}
		});

	}
	
	private void getPreferences() {
		access_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, ""); 
		access_token_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, ""); 
		logged_in = mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false); 
	}
	
	private void savePreferences() {
		Editor e = mSharedPreferences.edit(); 
		e.putString(Constants.PREF_KEY_OAUTH_TOKEN, access_token); 
		e.putString(Constants.PREF_KEY_OAUTH_SECRET, access_token_secret); 
		e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, logged_in);
		e.commit(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void newGameButtonClick(View view) {
		NewGameDialog dialog = new NewGameDialog();
		
		// Shows dialog. 
		dialog.show(getFragmentManager(), "NewGameDialog");
	}

	public void watchGameButtonClick(View view) {
		WatchGameDialog dialog = new WatchGameDialog();

		// Get list of games and shows in dialog. 
		new EventListener().get_sessions(getFragmentManager(), dialog);
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		Intent intent = new Intent(this, FieldActivity.class);
		intent.putExtra(Constants.MATCH_INFO, dialog.getMatchInfo());
		intent.putExtra(Constants.PREF_KEY_OAUTH_TOKEN, access_token); 
		intent.putExtra(Constants.PREF_KEY_OAUTH_SECRET, access_token_secret); 
		intent.putExtra(Constants.PREF_KEY_TWITTER_LOGIN, logged_in); 
		startActivity(intent);
	}

	@Override
	public void onSelectedGameClick(WatchGameDialog dialog, int id) {
		Intent intent = new Intent(this, FansFieldActivity.class);
		intent.putExtra("sessionNum", id);
		startActivity(intent);
	}
	
	public void doChecks() {
		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		

		// Check if twitter keys are set
		if (Constants.TWITTER_CONSUMER_KEY.trim().length() == 0
				|| Constants.TWITTER_CONSUMER_SECRET.trim().length() == 0) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens",
					"Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}		
	}

}
