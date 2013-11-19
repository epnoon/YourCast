package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
	HashMap<String, String> game_info;
	String home_team, away_team, time, sport_name;
	ArrayList<String> currentWords;
	int homeScore = 0, awayScore = 0;
	SportTimer timer;
	boolean timer_running = false;
	String access_token, access_token_secret;
	boolean logged_in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcaster_interface);
		Intent intent = getIntent();
		String json = intent.getStringExtra(MainActivity.MATCH_INFO);
		access_token = intent.getStringExtra(MainActivity.PREF_KEY_OAUTH_TOKEN);
		access_token_secret = intent
				.getStringExtra(MainActivity.PREF_KEY_OAUTH_SECRET);
		logged_in = intent.getBooleanExtra(MainActivity.PREF_KEY_TWITTER_LOGIN,
				false);
		JSONObject match_info;
		try {
			match_info = new JSONObject(json);
			sport_name = (String) match_info.getString("sport");
			home_team = (String) match_info.getString("home team");
			away_team = (String) match_info.getString("away team");
			time = (String) match_info.getString("time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (sport_name.equals(NewGameDialog.RUGBY)) {
			sport = new RugbySport();
		} else {
			assert false;
		}

		// Testing.
		if (home_team.isEmpty()) {
			home_team = "Michigan";
			away_team = "Ohio St.";
			time = "80";
		}

		TextView clock = (TextView) findViewById(R.id.timeText);
		ImageView clockButton = (ImageView) findViewById(R.id.timeButton);
		timer = sport.getClock(time, clock, clockButton);

		// Set title.
		TextView opponents = (TextView) findViewById(R.id.opponents);
		opponents.setText(home_team + " vs. " + away_team);

		ImageView fieldView = (ImageView) findViewById(R.id.fieldimage);
		fieldView.setImageResource(sport.getPictureID());

		fieldView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				imageX = touchX;
				imageY = touchY;
				currentWords = new ArrayList<String>();
				showEventPromptDialog();
				return true;
			}

		});

		fieldView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View field = (View) findViewById(R.id.fieldimage);
				touchX = event.getX();
				touchY = event.getY();
				return false;
			}
		});

		Log.d("MYMY", "Connecting to server");
		Toast.makeText(getApplicationContext(), "Connecting",
				Toast.LENGTH_SHORT).show();
		connection = new EventListener(getApplicationContext());
		connection.address_str = getString(R.string.server_addr);
		try {
			String gameName = home_team + " vs " + away_team;
			connection.Connect(getString(R.string.server_addr), gameName);
		} catch (Exception e) {
			e.printStackTrace();
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
		game_info = new HashMap<String, String>();
		game_info.put("Home Team", home_team);
		game_info.put("Away Team", away_team);
		game_info.put("Game Score", homeScore + " - " + awayScore);
		game_info.put("Game Time", time);

		JSONObject object = new JSONObject();
		try {
			object = JsonHelper.toJSON(game_info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
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
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.fieldlayout);
			ImageView iv;
			RelativeLayout.LayoutParams params;

			ImageView imageView = (ImageView) findViewById(R.id.fieldimage);

			float toSendWidth = touchX / imageView.getWidth();
			float toSendHeight = touchY / imageView.getHeight();

			iv = new ImageView(this);
			iv.setImageResource(R.drawable.orangecircle);
			// iv.setBackgroundColor(Color.RED);
			params = new RelativeLayout.LayoutParams(40, 40);
			params.leftMargin = (int) touchX - 20;
			params.topMargin = (int) touchY - 20;
			rl.addView(iv, params);

			String liveCast = eventTree.createText(currentWords);
			if (eventTree.getHomePoints(currentWords) > 0
					|| eventTree.getAwayPoints(currentWords) > 0) {
				homeScore += eventTree.getHomePoints(currentWords);
				awayScore += eventTree.getAwayPoints(currentWords);
				liveCast += " " + home_team + " " + homeScore + " - "
						+ away_team + " " + awayScore + ".";
			}

			Toast.makeText(getApplicationContext(), liveCast,
					Toast.LENGTH_SHORT).show();

			if (logged_in) {
				new updateTwitterStatus(this,
						access_token, access_token_secret).execute(liveCast);
			}

			int relX = (int) (touchX);
			int relY = (int) (touchY);
			connection.broadcast(liveCast, relX, relY);

		}
	}

}
