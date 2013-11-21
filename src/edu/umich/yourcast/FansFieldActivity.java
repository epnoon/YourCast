package edu.umich.yourcast;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class FansFieldActivity extends Activity {
	private String[] EVENTS = new String[] {}; 
	private int session_num;
	private EventListener connection;
	private static Timer pollTimer;
	RelativeLayout fieldView; 
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);
		
		// Get session number
		Intent intent = getIntent();
		session_num = intent.getIntExtra("sessionNum", -1);
		
		Sport sport = new RugbySport(); 
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_update,  EVENTS); 
		
		fieldView = (RelativeLayout) findViewById(R.id.fanfieldlayout);
		fieldView.setBackgroundResource(sport.getRotatedPictureID());
		
		ListView listview = (ListView) findViewById(R.id.listview); 
		listview.setAdapter(adapter); 
		
		connection = new EventListener(getApplicationContext(), listview, this);
		
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
    	private int last_id = 0;
    
    	@Override
    	public void run() {
    		connection.poll(session_num, last_id);
    	}
    }
   
   public void addDot(float eventx, float eventy) {
	    // x is actually y.
	    // y is actually x. 
		ImageView iv;
		RelativeLayout.LayoutParams params;
	   
	   	float layout_width = fieldView.getWidth();
		float layout_height = fieldView.getHeight(); 
		
		float place_width = eventy * layout_width; 
		float place_height = eventx * layout_height; 
		
		int diameter = (int) Math.round(0.02 * layout_width); 

		iv = new ImageView(this);
		iv.setImageResource(R.drawable.orangecircle);
		params = new RelativeLayout.LayoutParams(diameter, diameter);
		params.leftMargin = (int) place_width - diameter/2;
		params.topMargin = (int) place_height - diameter/2;
		fieldView.addView(iv, params);
   }
}
