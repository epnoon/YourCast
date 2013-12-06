package edu.umich.yourcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		NewGameDialog.NewGameDialogListener,
		ExistingGameDialog.ExistingGameDialogListener,
		WatchGameDialog.WatchGameDialogListener,
		BroadcastGameDialog.BroadcastGameDialogListener {
	private String TAG = "MainActivity";

	// Existing game intent
	private Intent newGameIntent;

	// Internet Connection detector
	private ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Shared Preferences
	private SharedPreferences mSharedPreferences;

	// Buttons
	ImageButton loginButton;
	TextView broadcastGame, watchGame, settingsButton;

	// Oauth stuff.
	String access_token, access_token_secret;
	boolean logged_in = false, twitter_broadcast, yourcast_broadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Thread for Twitter Login.
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		cd = new ConnectionDetector(getApplicationContext());
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"TwitterLogin", MODE_PRIVATE);

		// Check Internet and keys.
		doChecks();

		getPreferences();
		Log.d(TAG, String.valueOf(logged_in));
		Log.d(TAG, access_token);
		Log.d(TAG, access_token_secret);

		// Set Buttons.
		broadcastGame = (TextView) findViewById(R.id.broadcastgamebutton);
		watchGame = (TextView) findViewById(R.id.watchgamebutton);
		settingsButton = (TextView) findViewById(R.id.settingsbutton);

		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(),
				Constants.FONT_PATH_B);

		// Applying font
		broadcastGame.setTypeface(tf);
		watchGame.setTypeface(tf);
		settingsButton.setTypeface(tf);

		broadcastGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				broadcastGameButtonClick(view);
			}
		});

		watchGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				watchGameButtonClick(view);
			}
		});

		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToSettings();
			}
		});
	}

	private void goToSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void getPreferences() {
		access_token = mSharedPreferences.getString(
				Constants.PREF_KEY_OAUTH_TOKEN, "");
		access_token_secret = mSharedPreferences.getString(
				Constants.PREF_KEY_OAUTH_SECRET, "");
		logged_in = mSharedPreferences.getBoolean(
				Constants.PREF_KEY_TWITTER_LOGIN, false);
		twitter_broadcast = mSharedPreferences.getBoolean(
				Constants.TWITTER_BROADCAST, false);
		yourcast_broadcast = mSharedPreferences.getBoolean(
				Constants.YOURCAST_BROADCAST, false);

		Log.d(TAG, "token: " + access_token);
		Log.d(TAG, "secret: " + access_token);
		Log.d(TAG, "twitter: " + String.valueOf(twitter_broadcast));
		Log.d(TAG, "yourcast: " + String.valueOf(yourcast_broadcast));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void broadcastGameButtonClick(View view) {
		BroadcastGameDialog dialog = new BroadcastGameDialog();

		// Shows dialog.
		dialog.show(getFragmentManager(), "BroadcastGameDialog");
	}

	/*
	 * public void newGameButtonClick(View view) { NewGameDialog dialog = new
	 * NewGameDialog();
	 * 
	 * // Shows dialog. dialog.show(getFragmentManager(), "NewGameDialog"); }
	 */
	public void watchGameButtonClick(View view) {
		WatchGameDialog dialog = new WatchGameDialog();

		// Get list of games and shows in dialog.
		new EventListener().get_sessions(getFragmentManager(), dialog);
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		if (!dialog.isAFieldEmpty()) {
			Intent intent = new Intent(this, FieldActivity.class);
			intent.putExtra(Constants.MATCH_INFO, dialog.getMatchInfo());
			startActivity(intent);
		} else {
			alert.showAlertDialog(MainActivity.this,
					"Empty Fields",
					"Please fill out all fields in the new game form", false);
		}
	}

	@Override
	public void onSelectedGameClick(WatchGameDialog dialog, String game_info,
			int session_num, String session_title) {
		Intent intent = new Intent(this, FansFieldActivity.class);
		intent.putExtra(Constants.GAME_INFO, game_info);
		intent.putExtra("sessionNum", session_num);
		intent.putExtra("sessionTitle", session_title);
		startActivity(intent);
	}

	@Override
	public void onSelectedExistingGameClick(ExistingGameDialog dialog, int id,
			String title, String password) {
		// Intent intent = new Intent(this, FieldActivity.class);
		// intent.putExtra("sessionTitle", title);
		// intent.putExtra("sessionNum", id);
		// intent.putExtra("password", password);
		// startActivity(intent);
		new EventListener().get_info(this, Integer.toString(id), password);
	}

	@Override
	public void onOptionClick(BroadcastGameDialog dialog, int id) {
		/*
		 * Intent intent = new Intent(this, FansFieldActivity.class);
		 * intent.putExtra("sessionTitle", title); intent.putExtra("sessionNum",
		 * id); startActivity(intent);
		 */
		if (id == 0) {
			NewGameDialog newGameDialog = new NewGameDialog();
			// Shows dialog.
			newGameDialog.show(getFragmentManager(), "NewGameDialog");
		} else if (id == 1) {
			ExistingGameDialog existingGameDialog = new ExistingGameDialog();
			// Get list of sessions before showing dialog
			new EventListener().get_sessions(getFragmentManager(),
					existingGameDialog);
			// existingGameDialog.show(getFragmentManager(),
			// "existingGameDialog");
		} else
			throw new ClassCastException(this.toString()
					+ " encounterd problem with option selecting");
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
