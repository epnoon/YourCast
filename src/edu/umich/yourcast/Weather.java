package edu.umich.yourcast;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class Weather {
	Location location;
	JSONObject weather;
	FieldActivity mActivity;

	public Weather(Location l, FieldActivity fa) {
		location = l;
		mActivity = fa;
		getWeatherAtLocation();
	}

	private void getWeatherAtLocation() {
		try {
			new WeatherStation(this, location).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setWeather(JSONObject w) {
		weather = w;
	}

	public Object getWeather(String name) {
		try {
			return weather.getString(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showWeather() {
		try {
			mActivity.game_info.put("Weather",
					(String) weather.getString("weather"));
			mActivity.game_info.put("Temp", (String) weather
					.getString("temp_f").toString() + (char) 0x00B0 + "F");
			mActivity.game_info.put("Wind",
					(String) weather.getString("wind_mph").toString() + " mph");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
