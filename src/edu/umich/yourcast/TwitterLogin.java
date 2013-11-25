package edu.umich.yourcast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class TwitterLogin {
	private static Twitter twitter;
	private static RequestToken requestToken;

	private Activity mActivity;

	private String access_token, access_token_secret;
	boolean logged_in = false;

	public TwitterLogin(Activity a, String access_token,
			String access_token_secret) {
		this.access_token = access_token;
		this.access_token_secret = access_token_secret;
		this.logged_in = true;
		this.mActivity = a;
	}

	public TwitterLogin(Activity a) {
		this.mActivity = a;
	};

	public void login() {
		if (!logged_in) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
				mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(mActivity.getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
		}

	}

	public String getAccessToken() {
		return access_token;
	}

	public String getAccessTokenSecret() {
		return access_token_secret;
	}

	public boolean getLoggedIn() {
		return logged_in;
	}

	public void parseURI(Uri uri) {
		if (logged_in) {
			return;
		}
		if (uri != null
				&& uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {

			// oAuth verifier
			String verifier = uri
					.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);

			Log.d("Twitter", uri.toString());
			Log.d("Twitter", verifier);
			try {
				// Get the access token
				AccessToken accessToken = twitter.getOAuthAccessToken(
						requestToken, verifier);

				// After getting access token, access token secret
				// store them in application preferences
				access_token = accessToken.getToken();
				access_token_secret = accessToken.getTokenSecret();
				logged_in = true;

				Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
			} catch (Exception e) {
				// Check log for login errors
				Toast.makeText(mActivity.getApplicationContext(),
						"Login Failed", Toast.LENGTH_LONG).show();
				logged_in = false;
				Log.e("Twitter Login Error", "> " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		try {
			if (!logged_in) {
				return "";
			} else {
				AccessToken accessToken = new AccessToken(access_token,
						access_token_secret);
				long userId = accessToken.getUserId();
				User user = twitter.showUser(userId);
				return user.getScreenName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

}
