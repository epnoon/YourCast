package edu.umich.yourcast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Background classes. 

public class PollTask extends AsyncTask<Integer, Boolean, Integer>{
	// Connection vars. 
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpPost httppost = new HttpPost(Constants.POST_ADDRESS); 
	private HttpResponse httpresponse;
	
	
	// Data. 
	private int session; 
	private int event_id; 
	private String user_id; 
	
	// Helper JSON. 
	JSONObject object = new JSONObject();
	JSONObject eventobject = new JSONObject();
	JSONObject response; 
	
	// Other.
	String result = ""; 
	private ListView listView;
	private FansFieldActivity mActivity; 
	private List<String> livecasts = new ArrayList<String>();
	
	public PollTask(ListView lv, FansFieldActivity a) {
		listView = lv; 
		mActivity = a; 
	}
	
	
	ArrayAdapter<String> new_array;
	protected Integer doInBackground(Integer... params) {
		session = params[0];
		event_id = params[1];
		Log.d("MYMY", "polling event " + Integer.toString(event_id) + " session "+Integer.toString(session));

		// Serialize data. 
		try {
			object.put("type", Constants.PTYPE_POLL);
			object.put("user_id", user_id);
			object.put("event_id", event_id);
			object.put("session_num", session);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String serialized_request = object.toString();
		
		// Prepare package to be sent. 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 1;
		}
        
        // Make the exchange. 
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
       
		// Unpack response. 
		Log.d("MYMY", "Got response: " + result);
		try {
			response = new JSONObject(result);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 1;
		}
		
		// Show events. 
		try {
			if (response.getString("type").equals(Constants.PTYPE_EVENT)) {
				event_id = response.getInt("event_id");
				for (int x=0;x<100;x++) {
					String curEvent = "event" + Integer.toString(x);
					if (response.has(curEvent)) {
						String eventMsg = response.getString(curEvent);
						JSONObject eventJson = new JSONObject(eventMsg);
						String eventStr = eventJson.getString("msg");
						float eventX = Float.valueOf(eventJson.getString("x"));
						float eventY = Float.valueOf(eventJson.getString("y")); 
						livecasts.add(eventStr);
						mActivity.addDot(eventX, eventY); 
					} else {
						break;
					}
				}
			}
		}
		catch (Exception e) {
			Log.e("MYMY", Log.getStackTraceString(e));
			return 1;
		}
		
		if (livecasts.size() == 0){
			return 1;
		}
		
		String[] strarray = livecasts.toArray(new String[livecasts.size()]);	
		new_array = new ArrayAdapter<String>(EventListener.ctx, R.layout.list_update, strarray);
		return 0;
	}
	protected void onProgessUpdate(Boolean...prog){
		if (new_array != null) {
			Log.d("MYMY", "Updating new array!");
			listView.setAdapter(new_array);
		}
	}
	protected void onPostExecute(Integer res){
		if (res == 0){
			Log.d("MYMY", "Updating new array!");
			listView.setAdapter(new_array);
		}
	}
}
