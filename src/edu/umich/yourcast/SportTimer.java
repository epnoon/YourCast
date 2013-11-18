package edu.umich.yourcast;

import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

abstract public class SportTimer {
	public class STimer extends CountDownTimer {
		public STimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			SportTimer.this.onFinish(); 
			// TODO Auto-generated method stub

		}

		@Override
		public void onTick(long millisUntilFinished) {
			SportTimer.this.onTick();
		}
	}

	public SportTimer(String time, TextView t, ImageView i) {}; 
	abstract public void onTick(); 
	abstract public void onFinish(); 
	abstract public void start(); 
	abstract public void pause(); 
	abstract public String getCurrentTime(); 
}
