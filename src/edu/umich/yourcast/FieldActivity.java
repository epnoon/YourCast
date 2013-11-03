package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class FieldActivity extends Activity implements
	EventPromptDialog.EventPromptDialogListener {
	float touchX, touchY; 
	float imageX, imageY; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_field);
		Intent intent = getIntent();
		String json = intent.getStringExtra(MainActivity.MATCH_INFO);
		JSONObject match_info;
		String home = null, away = null, time = null;
		try {
			match_info = new JSONObject(json);
			home = (String) match_info.getString("home team");
			away = (String) match_info.getString("away team");
			time = (String) match_info.getString("time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String output = home + " vs. " + away;
		TextView t = (TextView) findViewById(R.id.gametitle);
		t.setText(output);

		View field = (View) findViewById(R.id.fieldimage);

		
		field.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				imageX = touchX;
				imageY = touchY; 
				showEventPromptDialog(); 
				
				// Toast.makeText(getApplicationContext(), "X: " + String.valueOf(touchX/imageX) + " Y: " + String.valueOf(touchY/imageY),
				// 		Toast.LENGTH_SHORT).show();
				return true;
			}

		});

		field.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View field = (View) findViewById(R.id.fieldimage);
				touchX = event.getX(); 
				touchY = event.getY(); 
				return false;
			}
		});
		Log.d("MYMY", output);
	}
	
	public void showEventPromptDialog() {
		ArrayList<String> s = new ArrayList<String>(); 
		s.add("Try");
		s.add("Lineout");
		s.add("Scrum"); 
		EventPromptDialog dialog = EventPromptDialog.create(s);
		dialog.show(getFragmentManager(), "EventPromptDialog");
	}

	// TODO Dummy method to put in current game Info,
	// should be changed later to listen to event and update game info
	public String getGameInfo() {
		JSONObject object = new JSONObject();
		try {
			object.put("game score", "1-1");
			object.put("game time", "90:00");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("MYMY", "getGameInfo");
		return object.toString();
	}

	public void infoButtonClick(View view) {
		GameInfoDialog dialog = GameInfoDialog.create(getGameInfo());
		dialog.show(getFragmentManager(), "GameInfoDialog");
	}

	public void onClick(String s) {
		// TODO Auto-generated method stub
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.fieldimage);
		ImageView iv;
		RelativeLayout.LayoutParams params;

		iv = new ImageView(this);
		iv.setBackgroundColor(Color.RED);
		params = new RelativeLayout.LayoutParams(40, 40);
		params.leftMargin = (int) touchX - 20;
		params.topMargin = (int) touchY - 20;
		rl.addView(iv, params);
		
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show(); 	
		Log.d("MYMY", String.valueOf(touchX)); 
		Log.d("MYMY", String.valueOf(touchY)); 
		Log.d("MYMY", String.valueOf(imageX)); 
		Log.d("MYMY", String.valueOf(imageY)); 
	}

}
