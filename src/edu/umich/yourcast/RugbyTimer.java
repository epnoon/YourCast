package edu.umich.yourcast;

import android.widget.ImageView;
import android.widget.TextView;

public class RugbyTimer extends SportTimer {
	ImageView imagebutton; 
	TextView textview; 		
	private int total_time = 0;
	private long MINUTE = 1000; 
	private long total, interval = MINUTE;
	STimer timer;
	boolean first_half = true; 
	
	public RugbyTimer(String time, TextView t, ImageView i) {
		// TODO Auto-generated constructor stub
		super(time, t, i); 
		total = Long.parseLong(time) * MINUTE;
		this.textview = t; 
		this.imagebutton = i; 
	}

	@Override
	public void onTick() {
		total_time += 1;
		textview.setText(String.valueOf(total_time) + "'");
	}

	@Override
	public void onFinish() {
		if (first_half) {
			first_half = false; 
		} else {
			
		}

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (first_half) {
			timer = new STimer((Long) total / 2 - total_time, interval); 
		} else {
			timer = new STimer((Long) total - total_time, interval); 
		}
		timer.start(); 
		imagebutton.setImageResource(R.drawable.stopsign); 
		textview.setText(String.valueOf(total_time) + "'");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		timer.cancel(); 
		imagebutton.setImageResource(R.drawable.start_triangle); 
	}

	@Override
	public String getCurrentTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
