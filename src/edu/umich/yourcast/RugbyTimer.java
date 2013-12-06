package edu.umich.yourcast;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class RugbyTimer extends SportTimer {
	final String TAG = "RugbyTimer"; 
	ImageView imagebutton; 
	TextView textview; 		
	private int total_time = 0;
	private long MINUTE = 60000; 
	private long total, interval = MINUTE;
	STimer timer;
	boolean first_half = true, half_time = false, full_time = false, game_done = false;  
	FieldActivity mActivity; 
	
	public RugbyTimer(String time, TextView t, ImageView i, FieldActivity f) {
		// TODO Auto-generated constructor stub
		super(time, t, i); 
		total = Long.parseLong(time) * MINUTE;
		textview = t; 
		imagebutton = i; 
		mActivity = f; 
	}

	@Override
	public void onTick() {
		total_time += 1;
		textview.setText(String.valueOf(total_time) + "'");
		mActivity.game_info.put("Time", String.valueOf(total_time) + " mins"); 
	}

	@Override
	public void onFinish() {
		total_time += 1;
		if (first_half) {
			Log.d(TAG, "First half done");
			first_half= false; 
		} else {
			Log.d(TAG, "Second half done");
			full_time = true; 
		}
		textview.setText(String.valueOf(total_time) + "'");
		mActivity.game_info.put("Time", String.valueOf(total_time) + " mins"); 
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (game_done) {
			return; 
		}
		if (first_half) {
			timer = new STimer((Long) total / 2 - (total_time) * MINUTE, interval); 
		} else {
			timer = new STimer((Long) total - (total_time) * MINUTE, interval); 
		}
		timer.start(); 
		imagebutton.setImageResource(R.drawable.stopsign); 
		textview.setText(String.valueOf(total_time) + "'");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if (game_done) {
			return; 
		} else if (full_time) {
			game_done = true; 
			mActivity.broadcastLiveCast("Full time!", "0", "0"); 
			mActivity.game_info.put("Time", "Full Time"); 
		} else if (!half_time && !first_half) {
			half_time = true; 
			mActivity.broadcastLiveCast("Halftime!", "0", "0"); 
			mActivity.game_info.put("Time", "Half Time"); 
		} else {
			timer.cancel(); 
		}
		imagebutton.setImageResource(R.drawable.start_triangle); 
	}

	@Override
	public String getCurrentTime() {
		// TODO Auto-generated method stub
		return String.valueOf(total_time); 
	}

}
