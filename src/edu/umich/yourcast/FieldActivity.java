package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FieldActivity extends Activity implements
		EventPromptDialog.EventPromptDialogListener {
	float touchX, touchY;
	float imageX, imageY;
	Sport sport;
	SportEventTree eventTree;
	EventListener connection;
	public HashMap<String, String> game_info = new HashMap<String, String>();
	String home_team, away_team, time, sport_name, session_pass, session_id;
	ArrayList<String> currentWords;
	int homeScore = 0, awayScore = 0;
	SportTimer timer;
	boolean timer_running = false;
	String access_token, access_token_secret, json;
	boolean logged_in, twitter_broadcast, yourcast_broadcast;
	JSONObject match_info;
	SharedPreferences mSharedPreferences;
	Weather weather;

	TextView clock, opponents;
	ImageView clockButton;
	RelativeLayout fieldView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcaster_interface);

		// Get information from the Intent.
		Intent intent = getIntent();
		if (intent.hasExtra(Constants.MATCH_INFO)) {
			json = intent.getStringExtra(Constants.MATCH_INFO);
			Log.d("Got match info", json);
			getJSON();
			game_info.put("Home Team", home_team);
			game_info.put("Away Team", away_team);
		}

		// Get Shared Preferences.
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"TwitterLogin", MODE_PRIVATE);
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

		// Select Sport.
		if (sport_name.equals(Constants.RUGBY)) {
			sport = new RugbySport();
		} else {
			assert false;
		}

		// Testing.
		if (home_team.isEmpty()) {
			home_team = "Michigan";
		}
		if (away_team.isEmpty()) {
			away_team = "Ohio St.";
		}
		if (time.isEmpty() || time == null) {
			time = "80";
		}
		if (session_pass.isEmpty()) {
			session_pass = "";
		}

		// Get Views.
		clock = (TextView) findViewById(R.id.timeText);
		clockButton = (ImageView) findViewById(R.id.timeButton);
		opponents = (TextView) findViewById(R.id.opponents);
		fieldView = (RelativeLayout) findViewById(R.id.fieldlayout);

		// Set timer.
		timer = sport.getClock(time, clock, clockButton, game_info);

		// Set title.
		opponents.setText(home_team + " vs. " + away_team);

		// Set field picture.
		fieldView.setBackgroundResource(sport.getPictureID());

		fieldView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// Reset words arraylist.
				currentWords = new ArrayList<String>();
				showEventPromptDialog();
				return true;
			}
		});

		fieldView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touchX = event.getX();
				touchY = event.getY();
				return false;
			}
		});

		// Location stuff.
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		weather = new Weather(location, this);

		Log.d("MYMY", "Connecting to server");
		Toast.makeText(getApplicationContext(), "Connecting",
				Toast.LENGTH_SHORT).show();
		connection = new EventListener(getApplicationContext());
		if (session_id.equals("")) {
			try {
				String gameName = home_team + " vs " + away_team;
				Log.d("MYMY", "password: " + session_pass);
				connection.Connect(gameName, session_pass, home_team,
						away_team, time);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			connection.session = Integer.parseInt(session_id);
		}
	}

	// Set time text and clock button
	public void timeButtonClick(View view) {
		if (timer_running) {
			timer.pause();
		} else {
			timer.start();
		}
		timer_running = !timer_running;
	}

	public void showEventPromptDialog() {
		eventTree = sport.getSportEventTree(home_team, away_team);
		EventPromptDialog dialog = EventPromptDialog.create(
				eventTree.options(), eventTree.title());
		dialog.show(getFragmentManager(), "EventPromptDialog");
	}

	public String getGameInfo() {
		JSONObject object = new JSONObject();
		try {
			object = JsonHelper.toJSON(game_info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}

	private void getJSON() {
		try {
			match_info = new JSONObject(json);
			sport_name = (String) match_info.getString("sport");
			home_team = (String) match_info.getString("home team");
			away_team = (String) match_info.getString("away team");
			time = (String) match_info.getString("time");
			session_pass = (String) match_info.getString("session_pass");
			session_id = "";
			if (match_info.has("session_id")) {
				session_id = (String) match_info.getString("session_id");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void infoButtonClick(View view) {
		GameInfoDialog dialog = GameInfoDialog.create(getGameInfo());
		dialog.show(getFragmentManager(), "GameInfoDialog");
	}

	public void onClick(String s) {
		eventTree.next(s);
		currentWords.add(s);

		if (!eventTree.isDone()) {
			EventPromptDialog dialog = EventPromptDialog.create(
					eventTree.options(), eventTree.title());
			dialog.show(getFragmentManager(), "EventPromptDialog");
		} else {
			ImageView iv;
			RelativeLayout.LayoutParams params;

			float layout_width = fieldView.getWidth();
			float layout_height = fieldView.getHeight();

			String width_proportion = String.valueOf(touchX / layout_width);
			String height_proportion = String.valueOf(touchY / layout_height);

			int diameter = (int) Math.round(0.02 * layout_height);

			iv = new ImageView(this);
			iv.setImageResource(R.drawable.orangecircle);
			params = new RelativeLayout.LayoutParams(diameter, diameter);
			params.leftMargin = (int) touchX - diameter / 2;
			params.topMargin = (int) touchY - diameter / 2;
			fieldView.addView(iv, params);

			String liveCast = eventTree.createText(currentWords);
			if (eventTree.getHomePoints(currentWords) > 0
					|| eventTree.getAwayPoints(currentWords) > 0) {
				homeScore += eventTree.getHomePoints(currentWords);
				awayScore += eventTree.getAwayPoints(currentWords);
				game_info.put("Game Score", homeScore + " - " + awayScore);
				liveCast += " " + home_team + " " + homeScore + " - "
						+ away_team + " " + awayScore + ".";
			}

			broadcastLiveCast(liveCast, width_proportion, height_proportion); 
		}

	}

	public void broadcastLiveCast(String liveCast, String width, String height) {
		Toast.makeText(getApplicationContext(), liveCast,
				Toast.LENGTH_SHORT).show();

		if (logged_in && twitter_broadcast) {
			new updateTwitterStatus(this, access_token, access_token_secret)
					.execute(liveCast);
		}
		if (yourcast_broadcast) {
			connection.broadcast(liveCast, width, height, session_pass);
		}
	}

}
