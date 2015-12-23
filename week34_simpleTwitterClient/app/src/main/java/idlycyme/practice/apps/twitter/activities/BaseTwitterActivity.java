package idlycyme.practice.apps.twitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


import idlycyme.practice.apps.twitter.TwitterApplication;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;


/**
 * Created by cyme on 12/22/15.
 */
public class BaseTwitterActivity extends AppCompatActivity {
    protected TwitterClient client;
    protected User loggedInUser;
    protected int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedInUser = (User)getIntent().getSerializableExtra("loggedInUser");
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
        willMakeRequest();
        if (undo) {
            client.deleteTweet(getJsonHttpResponseHandler(), idToRetweet);
        } else {
            client.postRetweet(getJsonHttpResponseHandler(), idToRetweet);
        }
    }

    public void onFavorite(String idToReply, Boolean undo) {
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
                Log.d("getHomeTimeline failed", errorResponse.toString());
            }
        }, lastTweetId, limit, type);
    }
}
