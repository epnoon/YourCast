package edu.umich.yourcast;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umich.yourcast.RugbySport.RugbyTimer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class FieldActivity extends Activity implements
		EventPromptDialog.EventPromptDialogListener {
	float touchX, touchY;
	float imageX, imageY;
	Sport sport;
	SportEventTree eventTree;
	String eventString = "";
	EventListener connection;
	String home_team, away_team, time, sport_name;
	ArrayList<String> currentWords;
	int homeScore = 0, awayScore = 0;
	RugbyTimer timer; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcaster_interface);
		Intent intent = getIntent();
		String json = intent.getStringExtra(MainActivity.MATCH_INFO);
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
		
		// Set title.
		TextView opponents = (TextView) findViewById(R.id.opponents);
		opponents.setText(home_team + " vs. " + away_team);
		
		/*LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.timebar);
		TextView textview = (TextView) new TextView(this); 
		textview.setText("Not Started"); 
		textview.setId(100); 
		linearLayout2.addView(textview);*/ 
		
		
		/*ImageButton clock = (ImageButton) new ImageButton(this); 
		LayoutParams layoutParams = 
				new LayoutParams(150, 150); 
		layoutParams.gravity = Gravity.RIGHT; 
		
		clock.setImageResource(R.drawable.start_triangle); 
		clock.setAdjustViewBounds(true); 
		clock.setBackgroundColor(0); 
		clock.setLayoutParams(layoutParams);
		clock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				timer = sport.getClock(time); 
				TextView t = (TextView) findViewById(100); 
				timer.setTextView(t); 
				timer.start(); 
				Toast.makeText(getApplicationContext(), "Started Clock",
						Toast.LENGTH_SHORT).show();
			}		
		}); */
		
		/*android:id="@+id/mainImageView"
		        android:layout_width="match_parent"
		        android:layout_height="wrap_content"
		        android:src="@drawable/mylogo_250px60px"
		        android:scaleType="fitEnd"
		        android:adjustViewBounds="true"
		*/
		//linearLayout2.addView(clock);  
		
		ImageView fieldView = (ImageView) findViewById(R.id.fieldimage);
		fieldView.setImageResource(sport.getPictureID());

		fieldView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				imageX = touchX;
				imageY = touchY;
				currentWords = new ArrayList<String>();
				showEventPromptDialog();

				// Toast.makeText(getApplicationContext(), "X: " +
				// String.valueOf(touchX/imageX) + " Y: " +
				// String.valueOf(touchY/imageY),
				// Toast.LENGTH_SHORT).show();
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
		Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
		connection = new EventListener(getApplicationContext());
		try {
			InetAddress addr = InetAddress.getByName(getString(R.string.server_addr));
			connection.Connect(getString(R.string.server_addr), "bla");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	// Set time text and clock button
	public void timeButtonClick(View view) {
	    	timer = sport.getClock(time); 
		TextView t = (TextView) findViewById(R.id.timeText); 
		timer.setTextView(t); 
		timer.start(); 
		Toast.makeText(getApplicationContext(), "Started Clock", Toast.LENGTH_SHORT).show();
	}

	public void showEventPromptDialog() {
		eventTree = sport.getSportEventTree(home_team, away_team);
		EventPromptDialog dialog = EventPromptDialog.create(
				eventTree.options(), eventTree.title());
		dialog.show(getFragmentManager(), "EventPromptDialog");
	}

	// TODO Dummy method to put in current game Info,
	// should be changed later to listen to event and update game info
	public String getGameInfo() {
		JSONObject object = new JSONObject();
		try {
			object.put("Score: ", homeScore + " - " + awayScore);
			object.put("game time", "90:00");
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
		eventString += s + " - ";
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

			iv = new ImageView(this);
			iv.setImageResource(R.drawable.orangecircle);
			// iv.setBackgroundColor(Color.RED);
			params = new RelativeLayout.LayoutParams(40, 40);
			params.leftMargin = (int) touchX - 20;
			params.topMargin = (int) touchY - 20;
			rl.addView(iv, params);

			connection.broadcast(eventString);
			Log.d("MYMY", "Sending: "+eventString);
			eventString = "";

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

		}
	}

}
