package edu.umich.yourcast;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

public class FansFieldActivity extends Activity {
	private String[] EVENTS = new String[] {}; 
	private int session_num;
	private EventListener connection;
	private static Timer pollTimer = new Timer("pollTimer", false);
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);
		
		// Get session number
		Intent intent = getIntent();
		session_num = intent.getIntExtra("sessionNum", -1);
		
		Sport sport = new RugbySport(); 
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_update,  EVENTS); 
		
		ImageView fieldView = (ImageView) findViewById(R.id.fanfieldimage);
		fieldView.setImageResource(sport.getRotatedPictureID());
		
		ListView listview = (ListView) findViewById(R.id.listview); 
		listview.setAdapter(adapter); 
		
		connection = new EventListener(getApplicationContext());
		connection.setAddr(getString(R.string.server_addr));
		connection.setList(listview);
		
		pollTimer.scheduleAtFixedRate(new pollTimerTask(), 0, 5000);
    }
    
    /*protected void onPause() {
    	pollTimer.cancel();
    }*/
    
    public class pollTimerTask extends TimerTask {
    	private Context ctx;
    	private int last_id = 0;
    	
    	@Override
    	public void run() {
    		connection.poll(session_num, last_id);
    	}
    }
}
