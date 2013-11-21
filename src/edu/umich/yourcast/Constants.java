package edu.umich.yourcast;

public class Constants {
	static final String TWITTER_CONSUMER_KEY = "MPOVJ0CoeVuOZlX1I3ThAw";
	static final String TWITTER_CONSUMER_SECRET = "regq3PMZaKNCwCwG3xe6WzzGrSVRSCGMLihFT1N8aYw";

	static final String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	static final String MATCH_INFO = "edu.umich.yourcast.match_info";
	
	// Font path
	static final String FONT_PATH = "fonts/LaPerutaFLF-Bold.ttf";

	public static final String PTYPE_CREATE = "create_session";
	public static final String PTYPE_BROADCAST = "broadcast";
	public static final String PTYPE_POLL = "poll";
	public static final String PTYPE_UPTODATE = "up_to_date";
	public static final String PTYPE_EVENT = "event";
	public static final String PTYPE_NEWUSER = "new_user";
	public static final String PTYPE_EXCEPTION = "exception";
	public static final String PTYPE_CONFIRM = "confirm_event";
	public static final String PTYPE_GET_SESSIONS = "get_sessions";
	
	public static final String POST_ADDRESS = "http://yourcast-server.appspot.com"; 
}
