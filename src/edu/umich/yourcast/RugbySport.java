package edu.umich.yourcast;

import android.os.CountDownTimer;

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
	public CountDownTimer getClock() {
		// TODO Auto-generated method stub
		return null;
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
