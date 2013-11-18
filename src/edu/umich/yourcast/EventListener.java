package edu.umich.yourcast;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.content.ContextWrapper;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.umich.yourcast.Ycpacket.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	private static HttpClient httpclient = new DefaultHttpClient();
	private static String uid = "12345";
	private static int session;
	private static InetAddress address = null;
	private Context ctx;
	private ListView listview;
	private int eventid=0;
	private FragmentManager currentFragment = null;
	public static String address_str;
	private List<String> eventStrings = new ArrayList<String>();
	public Activity callingAct;
	
	public EventListener(Context c) {
		this.ctx = c;
	}
	
	public EventListener() {
		this.ctx = null;
	}

	public int Connect (String... params) {
		try {
			new ConnectTask().execute(params);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	

	
	public int broadcast (String message, int x, int y) {
		try {
			new BroadcastTask(message, x, y).execute(message);
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public int get_sessions(WatchGameDialog dialog) {
		try {
			new GetSessionsTask().execute(dialog);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	public int poll(int session_num, int event_id) {
		try {
			new PollTask().execute(session_num, event_id);
			return 1;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
		
	public class PollTask extends AsyncTask<Integer, Boolean, Integer>{
		ArrayAdapter<String> new_array;
		protected Integer doInBackground(Integer... params) {
			int session_num = params[0];
			int event_id = params[1];
			Log.d("MYMY", "polling event "+Integer.toString(event_id)+" session "+Integer.toString(session_num));

			HttpPost httppost = new HttpPost(address_str);
			
			JSONObject object = new JSONObject();
			try {
				object.put("type", PTYPE_POLL);
				object.put("user_id", uid);
				object.put("event_id", eventid);
				object.put("session_num", session_num);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String serialized_request = object.toString();
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
	        
	        HttpResponse httpresponse;
	        InputStream content = null;
	        String result = "";
			try {
				httpresponse = httpclient.execute(httppost);
				HttpEntity response_ent = httpresponse.getEntity();
				result = EntityUtils.toString(response_ent);
			} catch (ClientProtocolException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			} catch (IOException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
	       
			Log.d("MYMY", "Got response: "+result);
			JSONObject response;
			try {
				response = new JSONObject(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
			
			try {
				if (response.getString("type").equals(PTYPE_EVENT)) {
					eventid = response.getInt("event_id");
					for (int x=0;x<100;x++) {
						if (response.has("event"+Integer.toString(x))) {
							String eventMsg = response.getString("event"+Integer.toString(x));
							JSONObject eventJson = new JSONObject(eventMsg);
							String eventStr = eventJson.getString("msg");
							final int eventX = eventJson.getInt("x");
							final int eventY = eventJson.getInt("y");
							eventStrings.add(eventStr);
							callingAct.runOnUiThread(new Runnable() {
								public void run(){
									addDot(eventX, eventY);
								}
							});
						}
						else {
							break;
						}
					}
				}
			}
			catch (Exception e) {
				Log.e("MYMY", Log.getStackTraceString(e));
				return 1;
			}
			
			if (eventStrings.size() == 0){
				return 1;
			}
			
			String[] strarray = eventStrings.toArray(new String[eventStrings.size()]);	
			ListAdapter adapter = listview.getAdapter();
			new_array = new ArrayAdapter<String>(ctx, R.layout.list_update, strarray);
			return 0;
		}
		protected void onProgessUpdate(Boolean...prog){
			if (new_array != null) {
				Log.d("MYMY", "Updating new array!");
				listview.setAdapter(new_array);
			}
		}
		protected void onPostExecute(Integer res){
			if (res == 0){
				Log.d("MYMY", "Updating new array!");
				listview.setAdapter(new_array);
			}
		}
	}
	
	public void setList(ListView view){
		listview = view;
	}
	
	public void addDot(int x, int y){
		RelativeLayout rl = (RelativeLayout) callingAct.findViewById(R.id.fanfieldlayout);
		ImageView iv;
		RelativeLayout.LayoutParams params;

		iv = new ImageView(callingAct);
		iv.setImageResource(R.drawable.orangecircle);
		params = new RelativeLayout.LayoutParams(40, 40);
		params.leftMargin = x;
		params.topMargin = y;
		rl.addView(iv, params);
	}
	
	public void setFragment(FragmentManager fm){
		currentFragment = fm;
	}
	
	public class GetSessionsTask extends AsyncTask<WatchGameDialog, Boolean, Integer> {
		protected Integer doInBackground(WatchGameDialog... dialogs) {
			HttpPost httppost = new HttpPost(address_str);
			
			JSONObject object = new JSONObject();
			try {
				object.put("type", PTYPE_GET_SESSIONS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String serialized_request = object.toString();
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
	        
	        HttpResponse httpresponse;
	        InputStream content = null;
	        String result = "";
			try {
				httpresponse = httpclient.execute(httppost);
				HttpEntity response_ent = httpresponse.getEntity();
				result = EntityUtils.toString(response_ent);
			} catch (ClientProtocolException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			} catch (IOException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
	       
			Log.d("MYMY", "Got response: "+result);
			JSONObject response;
			try {
				response = new JSONObject(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
			
			// Confirm server received event
			try {
				if (response.getString("type").equals(PTYPE_GET_SESSIONS)) {
					String json_str = response.getString("msg");
					Log.d("MYMY", "Got sessions"+json_str);
					dialogs[0].setGames(json_str);
					dialogs[0].show(currentFragment, "WatchGameDialog");
					return 0;
				}
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
			}
			return 1;

		}
		protected void onPostExecute(Integer result){
			if (result == 1) {
				if (ctx != null){
					Toast.makeText(ctx, "Couldn't connect to server", Toast.LENGTH_SHORT);
				}
			}
		}
	}
	
	public class BroadcastTask extends AsyncTask<String, Boolean, Integer> {
		private int x_coord;
		private int y_coord;
		String event_msg;
		public BroadcastTask(String msg, int x, int y) {
			x_coord = x;
			y_coord = y;
			event_msg = msg;
		}

		protected Integer doInBackground(String... params) {			
			HttpPost httppost = new HttpPost(address_str);
			JSONObject object = new JSONObject();
			JSONObject eventobject = new JSONObject();
			try {
				eventobject.put("msg", event_msg);
				eventobject.put("x", x_coord);
				eventobject.put("y", y_coord);
				object.put("type", PTYPE_BROADCAST);
				object.put("event", eventobject.toString());
				object.put("user_id", uid);
				object.put("session_num", session);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String serialized_request = object.toString();
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        HttpResponse httpresponse;
	        InputStream content = null;
	        String result = "";
			try {
				httpresponse = httpclient.execute(httppost);
				HttpEntity response_ent = httpresponse.getEntity();
				result = EntityUtils.toString(response_ent);
			} catch (ClientProtocolException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
			} catch (IOException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
			}
	       
			
			Log.d("MYMY", "Got response: "+result);
			JSONObject response;
			try {
				response = new JSONObject(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
			
			// Confirm server received event
			try {
				if (response.getString("type").equals(PTYPE_CONFIRM)) {
					Log.d("MYMY", "Sent event for session"+Integer.toString(session)+"uid "+uid);
					return 0;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return 1;
		}
		protected void onPostExecute(Integer result){
			if (result == 0) {
				Toast.makeText(ctx, "Event broadcast", Toast.LENGTH_SHORT);
				Log.d("MYMY", "event broadcasted");
			}
			else {
				Toast.makeText(ctx, "Event broadcast failed", Toast.LENGTH_SHORT);
				Log.d("MYMY", "event broadcast failed");
			}
		}
	}
	
	public class ConnectTask extends AsyncTask<String, Boolean, Integer> {
		protected Integer doInBackground(String... params) {
			// Convert addr to InetAddress
			try {
				address = InetAddress.getByName(params[0]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			HttpPost httppost = new HttpPost(address_str);
			
			JSONObject object = new JSONObject();
			try {
				object.put("type", PTYPE_CREATE);
				object.put("user_id", uid);
				object.put("msg", params[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String serialized_request = object.toString();
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        HttpResponse httpresponse;
	        InputStream content = null;
	        String result = "";
			try {
				httpresponse = httpclient.execute(httppost);
				HttpEntity response_ent = httpresponse.getEntity();
				result = EntityUtils.toString(response_ent);
			} catch (ClientProtocolException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
			} catch (IOException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
			}
	       
			
			Log.d("MYMY", "Got response: "+result);
			JSONObject response;
			try {
				response = new JSONObject(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 1;
			}
			
			if (params.length == 1) {
				// Dont create a session
				return 0;
			}	
			try {
				// Verify we have a new session
				if (response.getString("type").equals(PTYPE_CREATE)) {
					if (!response.has("session_num")){
						Log.d("MYMY", "No session num in packet");
						return 1;
					}
					
					Log.d("MYMY", "Session num "+Integer.toString(response.getInt("session_num")));
					session = response.getInt("session_num");
					return 0;
				}
				else {
					Log.d("MYMY", "type is not confirm");
					return 1;
				}
			}
			catch (Exception e) {
				Log.d("MYMY", "error yo");
				return 1;
			}
		}
		protected void onPostExecute(Integer result){
			if (result == 0) {
				Toast.makeText(ctx, "Session created", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(ctx, "Connection failed", Toast.LENGTH_SHORT).show();
			}
		}
			
	}
}


