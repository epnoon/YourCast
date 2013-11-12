package edu.umich.yourcast;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    public static JSONObject toJSON(HashMap<String, String> map)
	    throws JSONException {
	JSONObject json = new JSONObject();
	for (HashMap.Entry<String, String> entry : map.entrySet()) {
	    String key = entry.getKey();
	    String value = entry.getValue();
	    json.put(key.toString(), value.toString());
	}
	return json;
    }

    public static HashMap<String, String> toMap(JSONObject object)
	    throws JSONException {
	HashMap<String, String> map = new HashMap<String, String>();
	Iterator keys = object.keys();
	while (keys.hasNext()) {
	    String key = (String) keys.next();
	    map.put(key, object.get(key).toString());
	}
	return map;
    }

}
