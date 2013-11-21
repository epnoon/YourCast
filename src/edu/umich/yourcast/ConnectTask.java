package edu.umich.yourcast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import edu.umich.yourcast.Constants;
import edu.umich.yourcast.EventListener;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ConnectTask extends AsyncTask<String, Boolean, Integer> {
	// Connection stuff. 
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpPost httppost = new HttpPost(Constants.POST_ADDRESS); 
	private HttpResponse httpresponse;

	InetAddress address; 
	
	// Helper JSON. 
	JSONObject object = new JSONObject();
	JSONObject response; 
	
	// Data
	String user_id; 
	EventListener mEventListener; 
	
	// Other. 

    String result = "";
	
	public ConnectTask(String uid, EventListener el) {
		user_id = uid; 
		mEventListener = el; 
	}
	
	protected Integer doInBackground(String... params) {
		// Convert addr to InetAddress
		try {
			address = InetAddress.getByName(Constants.POST_ADDRESS);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		// Serialize data. 
		try {
			object.put("type", Constants.PTYPE_CREATE);
			object.put("user_id", user_id);
			object.put("msg", params[0]);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String serialized_request = object.toString();
		
		// Prepare data to be sent. 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        // Exchange occurs.
		try {
			httpresponse = httpclient.execute(httppost);
			HttpEntity response_ent = httpresponse.getEntity();
			result = EntityUtils.toString(response_ent);
		} catch (ClientProtocolException e) {
			Log.e("MYMY" ,Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e("MYMY" ,Log.getStackTraceString(e));
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
		
		if (params.length == 1) {
			// Don't create a session
			return 0;
		}	
		try {
			// Verify we have a new session
			if (response.getString("type").equals(Constants.PTYPE_CREATE)) {
				if (!response.has("session_num")){
					Log.d("MYMY", "No session num in packet");
					return 1;
				}
				
				Log.d("MYMY", "Session num "+Integer.toString(response.getInt("session_num")));
				mEventListener.setSession(response.getInt("session_num"));
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
			Toast.makeText(EventListener.ctx, "Session created", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(EventListener.ctx, "Connection failed", Toast.LENGTH_SHORT).show();
		}
	}
		
}
