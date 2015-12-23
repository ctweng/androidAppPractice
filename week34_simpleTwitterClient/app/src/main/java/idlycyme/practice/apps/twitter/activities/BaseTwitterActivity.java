package idlycyme.practice.apps.twitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import idlycyme.practice.apps.twitter.TwitterApplication;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;


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
        if (undo) {
            client.deleteTweet(new JsonHttpResponseHandler(), idToRetweet);
        } else {
            client.postRetweet(new JsonHttpResponseHandler(), idToRetweet);
        }
    }

    public void onFavorite(String idToReply, Boolean undo) {
        if (idToReply == null || idToReply.length() == 0) {
            return;
        }
        if (undo) {
            client.deleteFavorite(new JsonHttpResponseHandler(), idToReply);
        } else {
            client.postFavorite(new JsonHttpResponseHandler(), idToReply);
        }
    }

    public void onLoadCacheData(TimelineFragment fragment) {
        ArrayList tweets = new ArrayList<>();
        List<Tweet> cachedTweets = new Select().from(Tweet.class).orderBy("createdAt DESC").limit(limit).execute();
        if (cachedTweets != null && cachedTweets.size() > 0) {
            tweets.addAll(cachedTweets);
        }
        // quite ugly, need to refractor
        fragment.lastTweetId = "";
        fragment.aTweets.clear();
        fragment.aTweets.addAll(tweets);
        fragment.aTweets.notifyDataSetChanged();
        fragment.esListener.onLoadMore();
    }

    public void didLoadDataSuccess(ArrayList<Tweet> tweet, String type) {

    }

    public void didLoadDataFailure(JSONObject errorResponse, String type) {

    }

    public void onLoadData(String lastTweetId, final String type) {
        Log.i("type is ", String.valueOf(type) + "  last tweet id is " + lastTweetId);
        client.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                //super.onSuccess(statusCode, headers, json);
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
                didLoadDataFailure(errorResponse, type);
                Log.d("getHomeTimeline failed", errorResponse.toString());
            }
        }, lastTweetId, limit, type);
    }
}
