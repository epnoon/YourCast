package edu.umich.yourcast;

import java.util.HashMap;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

public class EventListener {
	String TAG = "MYMYEventListener"; 
	
	private static String password = "";
	public static int session;
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
			new ConnectTask(password, this).execute(params);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	// Interface Functions. 
	
	public int broadcast (String message, String x, String y, String pass, HashMap<String, String> info) {
		try {
			new BroadcastTask(session, pass, message, x, y, info).execute(message);
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public int get_sessions(FragmentManager fragmentManager, GameListDialog dialog) {
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
	
	public int get_info(MainActivity mainAct, String id, String password) {
		try {
			new GetInfoTask(mainAct).execute(id, password);
			return 0;
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


