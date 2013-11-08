package edu.umich.yourcast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FansFieldActivity extends Activity {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);
		//Intent intent = getIntent();
		Sport sport = new RugbySport(); 
		
		
		ImageView fieldView = (ImageView) findViewById(R.id.fanfieldimage);
		fieldView.setImageResource(sport.getRotatedPictureID());
    }
}
