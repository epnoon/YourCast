package edu.umich.yourcast;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class WeatherStation extends AsyncTask<String, String, Integer> {
	// TODO Auto-generated method stub
	String TAG = "WeatherStation";

	// Connection stuff.
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpResponse httpresponse;
	private String url = "", weather;
	private int wind, temp;

	// Helper JSON.
	JSONObject current_observation;
	JSONObject response;

	// Data
	String user_id;
	EventListener mEventListener;

	// Other.

	String result = "";
	Location location;
	Weather mWeather;

	public WeatherStation(Weather w, Location l) {
		location = l;
		mWeather = w;
	}

	@Override
	protected Integer doInBackground(String... params) {
		url += Constants.WUNDERGROUND_BASE + Constants.WUNDERGROUND_KEY
				+ Constants.WUNDERGROUND_CONDITIONS
				+ String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude())
				+ Constants.WUNDERGROUND_JSON;
		Log.d(TAG, url);

		try {
			httpresponse = httpclient.execute(new HttpGet(url));
			HttpEntity response_ent = httpresponse.getEntity();
			result = EntityUtils.toString(response_ent);
		} catch (ClientProtocolException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		// Unpack response.
		// Log.d(TAG, "Got response: " + result);
		try {
			response = new JSONObject(result);
			current_observation = response.getJSONObject("current_observation");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Failed to convert Result");
			return 1;
		}

		mWeather.setWeather(current_observation);
		return 0;
	}

	protected void onPostExecute(Integer result) {

	}

}
