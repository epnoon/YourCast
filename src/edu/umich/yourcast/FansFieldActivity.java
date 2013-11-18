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
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

public class FansFieldActivity extends Activity {
	private String[] EVENTS = new String[] {}; 
	private int session_num;
	private EventListener connection;
	private static Timer pollTimer;
	
	
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
		connection.address_str = getString(R.string.server_addr);
		connection.callingAct = this;
		connection.setList(listview);
		
    }
    
   protected void onPause() {
	   super.onPause();
	   pollTimer.cancel();
   }
   
   protected void onResume() {
	   super.onResume();
	   pollTimer = new Timer("pollTimer", false);
	   pollTimer.scheduleAtFixedRate(new pollTimerTask(), 0, 2000);
   }
   

    
   public class pollTimerTask extends TimerTask {
    	private Context ctx;
    	private int last_id = 0;
    
    	@Override
    	public void run() {
    		connection.poll(session_num, last_id);
    	}
    }
}
