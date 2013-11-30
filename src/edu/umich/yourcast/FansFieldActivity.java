package edu.umich.yourcast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FansFieldActivity extends Activity {
	private class Locations {
		public float eventx; 
		public float eventy; 
	}
	
	String TAG = "MYMYFansFieldActivity";

	private String[] EVENTS = new String[] {};
	private int session_num;
	private String session_title; 
	private EventListener connection;
	private static Timer pollTimer;
	RelativeLayout fieldView;
	private HashMap<Integer, Locations> locations = 
			new HashMap<Integer, Locations>(); 

	// For adding dot. 
	ImageView iv;
	RelativeLayout.LayoutParams params;
	TextView textview; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);

		// Get session number
		Intent intent = getIntent();
		session_num = intent.getIntExtra("sessionNum", -1);
		session_title = intent.getStringExtra("sessionTitle"); 

		Sport sport = new RugbySport();
		
		textview = (TextView) findViewById(R.id.fanstitlebar); 
		textview.setText(session_title); 

		fieldView = (RelativeLayout) findViewById(R.id.fanfieldlayout);
		fieldView.setBackgroundResource(sport.getRotatedPictureID());

		ListView listview = (ListView) findViewById(R.id.listview);
		listview.setClickable(true); 
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				showDot(position); 
				Log.d(TAG, "position: " + String.valueOf(position)); 
				Log.d(TAG, "id: " + String.valueOf(id)); 				
			}
		}); 

		connection = new EventListener(getApplicationContext(), listview, this);
	}

	protected void onPause() {
		super.onPause();
		pollTimer.cancel();
	}

	protected void onResume() {
		super.onResume();
		pollTimer = new Timer("pollTimer", false);
		pollTimer.scheduleAtFixedRate(new pollTimerTask(), 0, 2000);
	}

	public class pollTimerTask extends TimerTask {
		private int last_id = 0;

		@Override
		public void run() {
			connection.poll(session_num, last_id);
		}
	}

	public void addDot(float eventx, float eventy, int position) {
		Locations l = new Locations(); 
		l.eventx = eventx; 
		l.eventy = eventy; 
		locations.put(position, l); 	
		showDot(position); 
	}	
	
	public void showDot(int position) {
		// x is actually y.
		// y is actually x.
		Locations l = locations.get(position); 

		float layout_width = fieldView.getWidth();
		float layout_height = fieldView.getHeight();

		float place_width = l.eventy * layout_width;
		float place_height = l.eventx * layout_height;

		int diameter = (int) Math.round(0.04 * layout_width);

		iv = new ImageView(this);
		iv.setImageResource(R.drawable.orangecircle);
		params = new RelativeLayout.LayoutParams(diameter, diameter);
		params.leftMargin = (int) place_width - diameter / 2;
		params.topMargin = (int) place_height - diameter / 2;

		this.runOnUiThread(new Runnable() {
			public void run() {
				fieldView.removeAllViews(); 
				fieldView.addView(iv, params);
			}
		});
	}
	
	public void infoButtonClick(View view) {
		GameInfoDialog dialog = GameInfoDialog.create(getIntent().getStringExtra(Constants.GAME_INFO));
		dialog.show(getFragmentManager(), "GameInfoDialog");
	}
}
