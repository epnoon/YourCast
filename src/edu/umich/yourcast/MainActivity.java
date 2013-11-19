package edu.umich.yourcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import java.net.*;
import android.view.View.OnClickListener;
import android.net.Uri;

import edu.umich.yourcast.WatchGameDialog.WatchGameDialogListener;

public class MainActivity extends FragmentActivity implements
		NewGameDialog.NewGameDialogListener,
		WatchGameDialog.WatchGameDialogListener {

	// Constants
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	static String TWITTER_CONSUMER_KEY = "MPOVJ0CoeVuOZlX1I3ThAw";
	static String TWITTER_CONSUMER_SECRET = "regq3PMZaKNCwCwG3xe6WzzGrSVRSCGMLihFT1N8aYw";

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	public final static String MATCH_INFO = "edu.umich.yourcast.match_info";

	// Internet Connection detector
	private ConnectionDetector cd;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Shared Preferences
	public static SharedPreferences mSharedPreferences;

	ImageButton loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		cd = new ConnectionDetector(getApplicationContext());

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
		if (TWITTER_CONSUMER_KEY.trim().length() == 0
				|| TWITTER_CONSUMER_SECRET.trim().length() == 0) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens",
					"Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}

		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		// Set Login Button.
		loginButton = (ImageButton) findViewById(R.id.loginButton);

		// Twitter login button click event will call loginToTwitter() function
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Call login twitter function
				loginToTwitter();
			}
		});
		
		
	    /** This if conditions is tested once is
	     * redirected from twitter page. Parse the uri to get oAuth
	     * Verifier
	     * */
	    if (!isTwitterLoggedInAlready()) {
	        Uri uri = getIntent().getData();
	        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
	            // oAuth verifier
	            String verifier = uri
	                    .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
	 
	            try {
	                // Get the access token
	                AccessToken accessToken = twitter.getOAuthAccessToken(
	                        requestToken, verifier);
	 
	                // Shared Preferences
	                Editor e = mSharedPreferences.edit();
	 
	                // After getting access token, access token secret
	                // store them in application preferences
	                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
	                e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
	                // Store login status - true
	                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
	                e.commit(); // save changes
	 
	                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
	 
	                // Hide login button
	                // btnLoginTwitter.setVisibility(View.GONE);
	                 
	                // Getting user details from twitter
	                // For now i am getting his name only
	                long userID = accessToken.getUserId();
	                User user = twitter.showUser(userID);
	                String username = user.getName();
	                Toast.makeText(getApplicationContext(),
	    					"Hello, " + username, Toast.LENGTH_LONG).show();
	                 
	            } catch (Exception e) {
	                // Check log for login errors
	                Log.e("Twitter Login Error", "> " + e.getMessage());
	            }
	        }
	    }
	 

		// Font path
		String fontPath = "fonts/LaPerutaFLF-Bold.ttf";

		// text view label
		TextView newGame = (TextView) findViewById(R.id.newgamebutton);
		TextView watchGame = (TextView) findViewById(R.id.watchgamebutton);

		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

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

	}

	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void newGameButtonClick(View view) {
		NewGameDialog dialog = new NewGameDialog();
		dialog.show(getFragmentManager(), "NewGameDialog");
	}

	public void watchGameButtonClick(View view) {
		WatchGameDialog dialog = new WatchGameDialog();

		// Get list of games
		EventListener connection = new EventListener();
		connection.address_str = getString(R.string.server_addr);
		connection.setFragment(getFragmentManager());
		connection.get_sessions(dialog);

		// dialog.show(getFragmentManager(), "WatchGameDialog");
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		Intent intent = new Intent(this, FieldActivity.class);
		intent.putExtra(MATCH_INFO, dialog.getMatchInfo());
		intent.putExtra(PREF_KEY_OAUTH_TOKEN, mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "")); 
		intent.putExtra(PREF_KEY_OAUTH_SECRET, mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "")); 
		intent.putExtra(PREF_KEY_TWITTER_LOGIN, mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false)); 
		startActivity(intent);
	}

	@Override
	public void onSelectedGameClick(WatchGameDialog dialog, int id) {
		Intent intent = new Intent(this, FansFieldActivity.class);
		intent.putExtra("sessionNum", id);
		startActivity(intent);
	}

}
