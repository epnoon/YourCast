package edu.umich.yourcast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

public class FansFieldActivity extends Activity {
	private String[] EVENTS = new String[] {"Evan", "Quentin", "Owen", "Quentin", "Owen", "Quentin", "Owen", "Quentin", "Owen", "Quentin", "Owen", "Quentin", "Owen", "Quentin", "Owen"}; 
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);
		//Intent intent = getIntent();
		Sport sport = new RugbySport(); 
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_update,  EVENTS); 
		
		ImageView fieldView = (ImageView) findViewById(R.id.fanfieldimage);
		fieldView.setImageResource(sport.getRotatedPictureID());
		
		ListView listview = (ListView) findViewById(R.id.listview); 
		listview.setAdapter(adapter); 
    }
}
