package edu.umich.yourcast;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umich.yourcast.WatchGameDialog.WatchGameDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ExistingGameDialog extends DialogFragment {
    // private String gamesList;
    private CharSequence[] items;
    public int[] sessions;
    private ExistingGameDialogListener mListener;

    public void setGames(String json_sessions) {
	try {
	    JSONObject json = new JSONObject(json_sessions);
	    Iterator it = json.keys();
	    sessions = new int[json.length()];
	    items = new CharSequence[json.length()];
	    int x = 0;
	    while (it.hasNext()) {
		String key = (String) it.next();
		Log.d("MYMY", "Key " + key);
		sessions[x] = json.getInt(key);
		items[x++] = (CharSequence) key;
	    }
	} catch (JSONException e) {
	    Log.d("MYMY", "Illegal JSON");
	}

    }

    // Override the Fragment.onAttach() method to instantiate this
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
	// Verify that the host activity implements the callback interface
	try {
	    // Instantiate the NoticeDialogListener so we can send events to the
	    // host
	    mListener = (ExistingGameDialogListener) activity;
	} catch (ClassCastException e) {
	    // The activity doesn't implement the interface, throw exception
	    throw new ClassCastException(activity.toString()
		    + " must implement ExistingGameDialogListener");
	}
    }

    public int getSession(int x) {
	return sessions[x];
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	builder.setTitle(R.string.existing_games)
		.setItems(items, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			/*
			 * Toast.makeText(getActivity(), items[id],
			 * Toast.LENGTH_SHORT).show();
			 */
			Log.d("MYMY", "id" + Integer.toString(id));
			int session_num = sessions[id];
			String session_title = (String) items[id];
			mListener.onSelectedExistingGameClick(ExistingGameDialog.this,
				session_num, session_title);
		    }
		})
		.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			    }
			});
	return builder.create();
    }

    public interface ExistingGameDialogListener {
	public void onSelectedExistingGameClick(ExistingGameDialog dialog, int id,
		String title);
    }
}
