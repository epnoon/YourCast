package edu.umich.yourcast;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
	implements NewGameDialog.NewGameDialogListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void submitButton(View view) {
		NewGameDialog dialog = new NewGameDialog(); 
		dialog.show(getFragmentManager(), "NewGameDialog");	
		
	//	Intent i = new Intent(this, FieldActivity.class); 
	//	startActivity(i); 	
		
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		// TODO Auto-generated method stub
		
		Toast.makeText(getApplicationContext(), "Home Team: " + dialog.getHomeTeam(), Toast.LENGTH_SHORT).show(); 
	}

}
