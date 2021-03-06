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

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class GetInfoTask extends AsyncTask<String, Boolean, Integer> {
	// Connection vars. 
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpPost httppost = new HttpPost(Constants.POST_ADDRESS); 
	private HttpResponse httpresponse;
	
	// Helper JSON. 
	JSONObject object = new JSONObject();
	JSONObject eventobject = new JSONObject();
	JSONObject response; 
	
	// Other.
	String result = ""; 
	FragmentManager fragmentManager; 
	private MainActivity mainAct;
	
	public GetInfoTask(MainActivity mainact) {
		mainAct = mainact;
	}
	
	protected Integer doInBackground(String... params) {
		// Serialize request. 
		try {
			object.put("type", Constants.PTYPE_GET_INFO);
			object.put("session_num", Integer.parseInt(params[0]));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String serialized_request = object.toString();
		
		// Package request accordingly.
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("data", serialized_request));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return 1;
		}
        
        // Actually exchange. 
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
			e1.printStackTrace();
			return 1;
		}
		
		
		// Confirm server received event and display dialog. 
		try {
			if (response.getString("type").equals(Constants.PTYPE_GET_INFO)) {
				response.put("session_id", params[0]);
				response.put("session_pass", params[1]);
				Intent intent = new Intent(mainAct, FieldActivity.class);
				intent.putExtra(Constants.MATCH_INFO, response.toString());
				mainAct.startActivity(intent);
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
			if (EventListener.ctx != null) { 
				Toast.makeText(EventListener.ctx, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

