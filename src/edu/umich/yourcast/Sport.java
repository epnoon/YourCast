package edu.umich.yourcast;

import edu.umich.yourcast.RugbySport.RugbyTimer;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;

public abstract class Sport {

	public Sport() {
		// TODO Auto-generated constructor stub
	}

	abstract public int getPictureID(); 
	
	abstract public int getRotatedPictureID(); 
	
	abstract public SportEventTree getSportEventTree(String home_team, String away_team); 
	
	abstract public RugbyTimer getClock(String time, TextView t, ImageButton i); 
}
