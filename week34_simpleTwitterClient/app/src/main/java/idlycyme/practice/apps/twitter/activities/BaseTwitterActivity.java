package idlycyme.practice.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.TwitterApplication;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;


/**
 * Created by cyme on 12/22/15.
 */
public class BaseTwitterActivity extends AppCompatActivity implements TweetComposeFragment.OnComposeDoneListener {
    protected TwitterClient client;
    protected User loggedInUser;
    protected TweetComposeFragment tcfReply;
    protected int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");
        client = TwitterApplication.getRestClient();
        if (loggedInUser == null) {
            client.getLoggedInCredential(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    loggedInUser = User.fromJSON(response);
                }
            });
        }
    }

    public void onRetweet(String idToRetweet, Boolean undo) {
        if (idToRetweet == null || idToRetweet.length() == 0) {
            return;
        }
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();
        if (undo) {
            client.deleteTweet(getJsonHttpResponseHandler(), idToRetweet);
        } else {
            client.postRetweet(getJsonHttpResponseHandler(), idToRetweet);
        }
    }

    public void onFavorite(String idToReply, Boolean undo) {
        if (!isNetworkAvailable(true)) {
            return;
        }
        if (idToReply == null || idToReply.length() == 0) {
            return;
        }
        willMakeRequest();
        if (undo) {
            client.deleteFavorite(getJsonHttpResponseHandler(), idToReply);
        } else {
            client.postFavorite(getJsonHttpResponseHandler(), idToReply);
        }
    }

    public JsonHttpResponseHandler getJsonHttpResponseHandler() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                didMakeRequest();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                didMakeRequest();
            }
        };
    }

    public void willMakeRequest() {

    }

    public void didMakeRequest() {

    }

    public void didLoadDataSuccess(ArrayList<Tweet> tweet, String type) {

    }

    public void didLoadDataFailure(JSONObject errorResponse, String type) {

    }

    public void onLoadData(String lastTweetId, final String type) {
        Log.i("type is ", String.valueOf(type) + "  last tweet id is " + lastTweetId);
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();

        client.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                //super.onSuccess(statusCode, headers, json);
                didMakeRequest();
                ArrayList<Tweet> tweets;
                if (loggedInUser != null) {
                    tweets = Tweet.fromJSONArrayAddRetweeteable(jsonArray, loggedInUser.getUid());
                } else {
                    tweets = Tweet.fromJSONArray(jsonArray);
                }
                didLoadDataSuccess(tweets, type);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                didMakeRequest();
                didLoadDataFailure(errorResponse, type);
                if (errorResponse != null) {
                    Log.d("getHomeTimeline failed", errorResponse.toString());
                }
            }
        }, lastTweetId, limit, type);
    }

    public void onLoadSearchData(String lastTweetId, final String query) {
        Log.i("q is ", String.valueOf(query) + "  last tweet id is " + lastTweetId);
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();

        client.getTweetsByQuery(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                //super.onSuccess(statusCode, headers, json);
                didMakeRequest();
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("statuses");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<Tweet> tweets;
                Log.i("jsonOBject", jsonObject.toString());
                if (loggedInUser != null) {
                    tweets = Tweet.fromJSONArrayAddRetweeteable(jsonArray, loggedInUser.getUid());
                } else {
                    tweets = Tweet.fromJSONArray(jsonArray);
                }
                didLoadSearchDataSuccess(tweets, "search");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                didMakeRequest();
                didLoadSearchDataFailure(errorResponse, "search");
                Log.i("failed info", String.valueOf(statusCode) + " ");
                if (errorResponse != null) {
                    Log.d("search failed", errorResponse.toString());
                }
            }
        }, lastTweetId, limit, query);
    }


    public void didLoadSearchDataSuccess(ArrayList<Tweet> tweet, String type) {

    }

    public void didLoadSearchDataFailure(JSONObject errorResponse, String type) {

    }

    public Boolean isNetworkAvailable(boolean showToast) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean available = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (showToast && !available) {
            Toast.makeText(getBaseContext(), "Network is not available", Toast.LENGTH_LONG).show();
            Log.i("make toast", "network gg");
        }

        Log.i("network status ", String.valueOf(available));
        return available;
    }

    public void onReply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        tcfReply = TweetComposeFragment.newInstance(loggedInUser, tweet);
        tcfReply.show(fm, "fragment_edit_name");
    }

    public void onProfileImage(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onComposeDone(String text, String idToReply) {
        tcfReply.dismiss();
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();
        client.postTweet(getJsonHttpResponseHandler(), text, idToReply);
    }
}
