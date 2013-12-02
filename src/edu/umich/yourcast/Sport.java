package edu.umich.yourcast;

import java.util.HashMap;

import android.widget.ImageView;
import android.widget.TextView;

public abstract class Sport {

	public Sport() {
		// TODO Auto-generated constructor stub
	}

	abstract public int getPictureID(); 
	
	abstract public int getRotatedPictureID(); 
	
	abstract public SportEventTree getSportEventTree(String home_team, String away_team); 
	
	abstract public RugbyTimer getClock(String time, TextView t, ImageView i, HashMap<String, String> g); 
}