package edu.umich.yourcast;

import android.widget.ListView;
import android.app.FragmentManager;
import android.content.Context;

public class EventListener {
	public static final String PTYPE_CREATE = "create_session";
	public static final String PTYPE_BROADCAST = "broadcast";
	public static final String PTYPE_POLL = "poll";
	public static final String PTYPE_UPTODATE = "up_to_date";
	public static final String PTYPE_EVENT = "event";
	public static final String PTYPE_NEWUSER = "new_user";
	public static final String PTYPE_EXCEPTION = "exception";
	public static final String PTYPE_CONFIRM = "confirm_event";
	public static final String PTYPE_GET_SESSIONS = "get_sessions";
	
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


