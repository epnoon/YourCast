package edu.umich.yourcast;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class WatchGameDialog extends DialogFragment {
	// private String gamesList;
	private CharSequence[] items;
	public int[] sessions;
	private WatchGameDialogListener mListener;

	public void setGames(String json_sessions) {
		try {
			JSONObject json = new JSONObject(json_sessions);
			Iterator it = json.keys();
			sessions = new int[json.length()];
			items = new CharSequence[json.length()];
			int x=0;
			while (it.hasNext()){
				String key = (String) it.next();
				Log.d("MYMY", "Key "+key);
				sessions[x] = json.getInt(key);
				items[x++] = (CharSequence) key;
			}
		}
		catch (JSONException e){
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
			mListener = (WatchGameDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement WatchGameDialogListener");
		}
	}
	
	public int getSession(int x){
		return sessions[x];
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.live_games)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// TODO Auto-generated method stub
						/*
						 * Toast.makeText(getActivity(), items[id],
						 * Toast.LENGTH_SHORT).show();
						 */
						Log.d("MYMY", "id"+Integer.toString(id));
						int session_num = sessions[id];
						String session_title = (String) items[id]; 
						mListener.onSelectedGameClick(WatchGameDialog.this, getGameInfo(session_num, session_title), session_num, session_title);
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

	public interface WatchGameDialogListener {
		public void onSelectedGameClick(WatchGameDialog dialog, String game_info, int session_num, String session_title);
	}

	// Need to be revised to put in real-time game info
	public String getGameInfo(int session_num, String session_title) {
	    	HashMap<String, String> game_info;
	    	game_info = new HashMap<String, String>();
	    	game_info.put("session_num", String.valueOf(session_num));
	    	game_info.put("session_title", session_title);
	    	JSONObject object = new JSONObject();
		try {
			object = JsonHelper.toJSON(game_info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}

}
