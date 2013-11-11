package edu.umich.yourcast;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

public class RugbySport extends Sport {

	public RugbySport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPictureID() {
		// TODO Auto-generated method stub
		return R.drawable.rugby_field;
	}

	@Override
	public RugbyTimer getClock(String time, TextView t, ImageButton i) {
		return new RugbyTimer(time, t, i); 
	}

	public class RugbyTimer {
		private TextView textview = null;
		private int total_time = 0;
		private long total, interval = 1000;
		RTimer timer;
		boolean first_half = true; 

		private class RTimer extends CountDownTimer {
			public RTimer(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
				// TODO Auto-generated constructor stub
			}

			@Override
			public void onFinish() {
				RugbyTimer.this.onFinish(); 
				// TODO Auto-generated method stub

			}

			@Override
			public void onTick(long millisUntilFinished) {
				RugbyTimer.this.onTick();
			}
		}

		public RugbyTimer(String time, TextView textview, ImageButton imagebutton) {
			total = Long.parseLong(time) * 1000;
			this.textview = textview; 
			timer = new RTimer(total / 2, interval);
		}

		public void onTick() {
			total_time += 1;
			textview.setText(String.valueOf(total_time) + "'");
		}

		public void onFinish() {
			if (first_half) {
				first_half = false; 
			} else {
				
			}
		}

		public int getCurrentTime() {
			return total_time; 
		}

		public void pause() {

		}
		
		public void start() {
			
		}
	}

	@Override
	public SportEventTree getSportEventTree(String home_team, String away_team) {
		// TODO Auto-generated method stub
		return new RugbyEventTree(home_team, away_team);
	}

	@Override
	public int getRotatedPictureID() {
		// TODO Auto-generated method stub
		return R.drawable.rugby_field_rotated;
	}
}
