package edu.umich.yourcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FansFieldActivity extends Activity {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_interface);
		Intent intent = getIntent();
    }
}
