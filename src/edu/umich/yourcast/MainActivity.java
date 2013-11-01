package edu.umich.yourcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		NewGameDialog.NewGameDialogListener {

	public final static String MATCH_INFO = "edu.umich.yourcast.match_info";

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

	public void newGameButtonClick(View view) {
		NewGameDialog dialog = new NewGameDialog();
		dialog.show(getFragmentManager(), "NewGameDialog");
	}

	@Override
	public void onDialogPositiveClick(NewGameDialog dialog) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, FieldActivity.class);
		intent.putExtra(MATCH_INFO, dialog.getMatchInfo());
		startActivity(intent);
	}

}
