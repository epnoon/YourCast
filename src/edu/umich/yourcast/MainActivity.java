package edu.umich.yourcast;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import java.net.*;
import android.view.View.OnClickListener;

import edu.umich.yourcast.WatchGameDialog.WatchGameDialogListener;

public class MainActivity extends FragmentActivity implements
		NewGameDialog.NewGameDialogListener,
		WatchGameDialog.WatchGameDialogListener {

	public final static String MATCH_INFO = "edu.umich.yourcast.match_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	      // Font path
        String fontPath = "fonts/LaPerutaFLF-Bold.ttf";
 
        // text view label
        TextView newGame = (TextView) findViewById(R.id.newgamebutton);
        TextView watchGame = (TextView) findViewById(R.id.watchgamebutton);
        
 
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
 
        // Applying font
        newGame.setTypeface(tf);
        watchGame.setTypeface(tf);
        
        newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				newGameButtonClick(view); 
			}    	
        }); 
        
        watchGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				watchGameButtonClick(view); 
			}    	
        }); 
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void newGameButtonClick(View view) {
		NewGameDialog dialog = new NewGameDialog();
		dialog.show(getFragmentManager(), "NewGameDialog");
	}

	public void watchGameButtonClick(View view) {
		WatchGameDialog dialog = new WatchGameDialog();
		
		// Get list of games
		EventListener connection = new EventListener();
		connection.setAddr(getString(R.string.server_addr));
		connection.setFragment(getFragmentManager());
		connection.get_sessions(dialog);

		//dialog.show(getFragmentManager(), "WatchGameDialog");
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		Intent intent = new Intent(this, FieldActivity.class);
		intent.putExtra(MATCH_INFO, dialog.getMatchInfo());
		startActivity(intent);
	}

	@Override
	public void onSelectedGameClick(WatchGameDialog dialog, int id) {
		Intent intent = new Intent(this, FansFieldActivity.class);
		intent.putExtra("sessionNum", id);
		startActivity(intent);
	}

}
