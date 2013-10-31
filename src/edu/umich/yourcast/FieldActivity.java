package edu.umich.yourcast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class FieldActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_field);
	Intent intent = getIntent();
	String json = intent.getStringExtra(MainActivity.MATCH_INFO);
	JSONObject match_info;
	String home = null, away = null, time = null;
	try {
	    match_info = new JSONObject(json);
	    home = (String) match_info.getString("home team");
	    away = (String) match_info.getString("away team");
	    time = (String) match_info.getString("time");
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	String output = home + " vs. " + away;
	TextView t = (TextView) findViewById(R.id.gametitle);
	t.setText(output);

	Log.d("MYMY", output);
    }

    public void infoButtonClick(View view) {
	Bundle game_info = new Bundle(); // Used to pass game info.
	game_info.putString("Time", "90"); // TODO put in game info, e.g. score, remaining time
	
	GameInfoDialog dialog = new GameInfoDialog();
	dialog.setArguments(game_info);
	dialog.show(getFragmentManager(), "GameInfoDialog");
    }

}
