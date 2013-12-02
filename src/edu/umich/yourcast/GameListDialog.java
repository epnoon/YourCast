package edu.umich.yourcast;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.util.Log;
import edu.umich.yourcast.WatchGameDialog.WatchGameDialogListener;

public class GameListDialog extends DialogFragment {
	// private String gamesList;
	protected CharSequence[] items;
	public int[] sessions;

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
}