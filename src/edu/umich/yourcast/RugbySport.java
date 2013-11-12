package edu.umich.yourcast;

import android.widget.ImageButton;
import android.widget.ImageView;
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
	public RugbyTimer getClock(String time, TextView t, ImageView i) {
		return new RugbyTimer(time, t, i); 
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
