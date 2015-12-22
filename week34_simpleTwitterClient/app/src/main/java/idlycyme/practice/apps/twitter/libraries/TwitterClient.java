package idlycyme.practice.apps.twitter.libraries;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "a0Met9QWnZiA7ShYEwvHVUqpW";       // Change this
	public static final String REST_CONSUMER_SECRET = "DbpoSeltwU3bK22GQi1SlwxEMO3oqezeXqr715BBogusQ37UZ6"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://idlycyme"; // Change this (here and in manifest)
    public static final int TIMELINE_MAX_COUNT_EX = 50;
    public static final int TIMELINE_MIN_COUNT_EX = 20;
    public static final int TIMELINE_DEFAULT_COUNT = 20;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler, String maxId, int count) {
        _getTimelineCommon("home", handler, maxId, count);
	}

    public void getUserTimeline(AsyncHttpResponseHandler handler, String maxId, int count) {
        _getTimelineCommon("user", handler, maxId, count);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler, String maxId, int count) {
        _getTimelineCommon("mentions", handler, maxId, count);
    }

    public void getTimeline(AsyncHttpResponseHandler handler, String maxId, int count, String type) {
        _getTimelineCommon(type, handler, maxId, count);
    }

    private void _getTimelineCommon(String type, AsyncHttpResponseHandler handler, String maxId, int count) {
        String apiBase = "";
        switch (type) {
            case "user": //user
                apiBase = "user_timeline";
                break;
            case "mentions": //mentions
                apiBase = "mentions_timeline";
                break;
            case "home": //home
            default:
                apiBase = "home_timeline";
                break;
        }
        String url = getApiUrl("statuses/" + apiBase + ".json");
        RequestParams params = new RequestParams();
        if (count > TIMELINE_MAX_COUNT_EX || count < TIMELINE_MIN_COUNT_EX) {
            count = TIMELINE_DEFAULT_COUNT;
        }
        params.put("count", count);
        if (!maxId.isEmpty()) {
            params.put("max_id", maxId);
        }
        Log.i("timeline params", params.toString());
        getClient().get(url, params, handler);
    }

	public void getLoggedInCredential(AsyncHttpResponseHandler handler) {
		String url = getApiUrl("account/verify_credentials.json");
		getClient().get(url, handler);
	}

    public void postTweet(AsyncHttpResponseHandler handler, String text, String idToReply) {
        if (text.equals("")) {
            return;
        }
        String url = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        // reply case
        if (!idToReply.isEmpty()) {
            params.put("in_reply_to_status_id", idToReply);
        }
        getClient().post(url, params, handler);
    }

    public void postRetweet(AsyncHttpResponseHandler handler, String tweetId) {
        if (tweetId.isEmpty()) {
            return;
        }
        String url = getApiUrl("statuses/retweet/" + tweetId + ".json");
        getClient().post(url, handler);
    }

    public void postFavorite(AsyncHttpResponseHandler handler, String tweetId) {
        if (tweetId.isEmpty()) {
            return;
        }
        String url = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().post(url, params, handler);
    }

    public void deleteFavorite(AsyncHttpResponseHandler handler, String tweetId) {
        if (tweetId.isEmpty()) {
            return;
        }
        String url = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().post(url, params, handler);
    }

    public void getTweet(AsyncHttpResponseHandler handler, String tweetId) {
        if (tweetId.isEmpty()) {
            return;
        }
        String url = getApiUrl("statuses/show/" + tweetId + ".json");
        RequestParams params = new RequestParams();
        params.put("include_my_retweet", 1);
        getClient().post(url, handler);
    }

    public void deleteTweet(AsyncHttpResponseHandler handler, String tweetId) {
        if (tweetId.isEmpty()) {
            return;
        }
        String url = getApiUrl("statuses/destroy/" + tweetId + ".json");
        RequestParams params = new RequestParams();
        params.put("include_my_retweet", 1);
        getClient().post(url, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}