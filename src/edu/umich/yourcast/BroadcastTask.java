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
import android.widget.Toast;

public class BroadcastTask extends AsyncTask<String, Boolean, Integer> {
	String TAG = "MYMYBroadcastTask"; 
	
	// Connection vars.
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpPost httppost = new HttpPost(Constants.POST_ADDRESS);
	private HttpResponse httpresponse;

	// Data.
	private String x_coord;
	private String y_coord;
	private int session;
	private String password;
	private String event_msg;

	// Helper JSON.
	JSONObject object = new JSONObject();
	JSONObject eventobject = new JSONObject();
	JSONObject response;

	// Other.
	String result = "";

	public BroadcastTask(int sess, String pw, String msg, String x, String y) {
		x_coord = x;
		y_coord = y;
		event_msg = msg;
		session = sess;
		password = pw;
	}

	protected Integer doInBackground(String... params) {
		// Serialize data.
		try {
			eventobject.put("msg", event_msg);
			eventobject.put("x", x_coord);
			eventobject.put("y", y_coord);
			object.put("type", Constants.PTYPE_BROADCAST);
			object.put("event", eventobject.toString());
			object.put("password", password);
			object.put("session_num", session);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String serialized_request = object.toString();
		Log.d(TAG, serialized_request); 

		// Prepare data to be sent.
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Send the data.
		try {
			httpresponse = httpclient.execute(httppost);
			HttpEntity response_ent = httpresponse.getEntity();
			result = EntityUtils.toString(response_ent);
		} catch (ClientProtocolException e) {
			Log.e("MYMY", Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e("MYMY", Log.getStackTraceString(e));
		}

		Log.d("MYMY", "Got response: " + result);

		// Decrypt response.
		try {
			response = new JSONObject(result);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 1;
		}

		// Confirm server received event
		try {
			if (response.getString("type").equals(Constants.PTYPE_CONFIRM)) {
				Log.d("MYMY",
						"Sent event for session" + Integer.toString(session)
								+ "pw " + password);
				
				return 0;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 1;
	}

	protected void onPostExecute(Integer result) {
		if (result == 0) {
			Toast.makeText(EventListener.ctx, "Event broadcast",
					Toast.LENGTH_SHORT).show();
			Log.d("MYMY", "event broadcasted");
		} else {
			Toast.makeText(EventListener.ctx, "Event broadcast failed",
					Toast.LENGTH_SHORT).show();
			Log.d("MYMY", "event broadcast failed");
		}
	}
}