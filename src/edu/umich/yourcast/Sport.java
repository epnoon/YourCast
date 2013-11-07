package edu.umich.yourcast;

import android.os.CountDownTimer;

public abstract class Sport {

	public Sport() {
		// TODO Auto-generated constructor stub
	}
	
	abstract public int getPictureID(); 
	
	abstract public SportEventTree getSportEventTree(String home_team, String away_team); 
	
	abstract public CountDownTimer getClock(); 
}
