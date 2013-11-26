package edu.umich.yourcast;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class Weather {
	Location location; 
	JSONObject weather; 
	
	public Weather(Location l) {
		location = l; 
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
	

}
