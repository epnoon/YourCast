package edu.umich.yourcast;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap; 

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class GameInfoDialog extends DialogFragment {

	public final static String GAME_INFO = "edu.umich.yourcast.game_info";
	public final static String[] ORDERING = {"Home Team", "Away Team", "Game Score", "Time"}; 
	
	class KeyComparator implements Comparator<String> { 
	    public int compare(String a, String b) {
	    	for (int i = 0; i < ORDERING.length; i++) {
	    		if (a.equals(ORDERING[i])) {
	    			return -1; 
	    		} else if (b.equals(ORDERING[i])) {
	    			return 1; 
	    		}
	    	}
	    	return a.compareTo(b); 
	    }
	}

	/**
	 * Since Fragments are created/recreated using a default constructor, supply
	 * a create function that will provide arguments that are maintained across
	 * instances of this Fragment class.
	 * 
	 * @param GameInfoDialog
	 * @return
	 */
	public static GameInfoDialog create(String gameInfo) {
		GameInfoDialog f = new GameInfoDialog();
		Bundle args = new Bundle();
		args.putString(GAME_INFO, gameInfo);
		f.setArguments(args);
		return f;
	}

	private TreeMap<String, String> game_info = new TreeMap<String, String>(new KeyComparator());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retrieve the arguments that were supplied by create()
		JSONObject object;
		try {
		    	object = new JSONObject(getArguments().getString(GAME_INFO));
		    	HashMap<String, String> hash_map = JsonHelper.toMap(object);
		    	game_info.putAll(hash_map); 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private View v;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		v = inflater.inflate(R.layout.dialog_game_info, null);

		// Set dialog content
		LinearLayout contentLayout = (LinearLayout) v.findViewById(R.id.dialog_content);
		
		for (HashMap.Entry<String, String> entry : game_info.entrySet()) {
		    RelativeLayout relativeLayout = new RelativeLayout(getActivity());
	            String key = entry.getKey();
	            String value = entry.getValue();
	            
	            TextView textview1 = (TextView) new TextView(getActivity());
	            textview1.setText(key);
	            textview1.setTextSize(20);
	            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	            textview1.setLayoutParams(layoutParams1);
	            relativeLayout.addView(textview1);
	            
	            TextView textview2 = (TextView) new TextView(getActivity());
	            textview2.setText(value);
	            textview2.setTextSize(20);
	            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	            textview2.setLayoutParams(layoutParams2);
	            relativeLayout.addView(textview2);
	            
	            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	            contentLayout.addView(relativeLayout, params);
	        } 

		builder.setView(v)
		// Add action buttons
				.setNeutralButton(R.string.back,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		return builder.create();
	}
}
