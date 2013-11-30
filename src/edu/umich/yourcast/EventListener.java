package edu.umich.yourcast;

import android.app.FragmentManager;
import android.content.Context;
import android.widget.ListView;

public class EventListener {
	String TAG = "MYMYEventListener"; 
	
	private static String user_id = "12345";
	private static int session;
	public static Context ctx = null; 
	public static String address_str;
	private ListView listView; 
	private FansFieldActivity mActivity; 
	
	public EventListener(Context c) {
		EventListener.ctx = c;
	}
	
	public EventListener() {}
	
	public EventListener(Context c, ListView l, FansFieldActivity a) {
		ctx = c; 
		listView = l;
		mActivity = a; 
	}


	public int Connect (String... params) {
		try {
			new ConnectTask(user_id, this).execute(params);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	// Interface Functions. 
	
	public int broadcast (String message, String x, String y) {
		try {
			new BroadcastTask(session, user_id, message, x, y).execute(message);
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public int get_sessions(FragmentManager fragmentManager, WatchGameDialog dialog) {
		try {
			new GetSessionsTask(fragmentManager).execute(dialog);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	public int poll(int session_num, int event_id) {
		try {
			new PollTask(listView, mActivity).execute(session_num, event_id);
			return 1;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	public void setSession(int s) {
		session = s; 
	}
}


