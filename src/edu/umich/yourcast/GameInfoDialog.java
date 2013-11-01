package edu.umich.yourcast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class GameInfoDialog extends DialogFragment {

    public final static String GAME_INFO = "edu.umich.yourcast.game_info";

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

    private String game_score;
    private String game_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// Retrieve the arguments that were supplied by create()
	String json = getArguments().getString(GAME_INFO);
	JSONObject game_info;
	try {
	    game_info = new JSONObject(json);
	    game_score = (String) game_info.getString("game score");
	    game_time = (String) game_info.getString("game time");
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

	TextView tv = (TextView) v.findViewById(R.id.game_score);
	tv.setText(game_score);
	tv = (TextView) v.findViewById(R.id.game_time);
	tv.setText(game_time);

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
