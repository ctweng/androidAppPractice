package idlycyme.practice.apps.twitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.TwitterApplication;
import idlycyme.practice.apps.twitter.adapters.TweetsArrayAdapter;
import idlycyme.practice.apps.twitter.libraries.EndlessScrollListener;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;

/**
 * Created by cyme on 11/24/15.
 */
public class TweetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TweetComposeFragment.OnComposeDoneListener {
    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private TweetComposeFragment tcfReply;
    private User loggedInUser;
    private String lastTweetId = "";
    private int limit = 20;
    private EndlessScrollListener esListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        Tweet tweet = (Tweet)getIntent().getSerializableExtra("tweet");
        loggedInUser = (User)getIntent().getSerializableExtra("loggedInUser");
        lvTweets = (ListView)findViewById(R.id.lvTweetDetail);
        tweets = new ArrayList<>();
        tweets.add(tweet);
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        //lvTweets.setOnItemClickListener(TweetActivity.this);
        esListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                //populateTimeline();
            }
        };
        lvTweets.setOnScrollListener(esListener);
    }

    @Override
    public void onClick(View view) {
        int position = (Integer)view.getTag();
        Tweet tweet = tweets.get(position);
        String id = tweet.getId();
        switch (view.getId()) {
            case  R.id.ibReply:
                FragmentManager fm = getSupportFragmentManager();
                tcfReply = TweetComposeFragment.newInstance(loggedInUser, tweet);
                tcfReply.show(fm, "fragment_edit_name");
                break;
            case R.id.ibFavorite:
                onFavorite(id, tweet.getFavorited());
                break;
            case R.id.ibRetweet:
                break;
            default:
                break;
        }
    }

    public void onFavorite(String idToReply, Boolean undo) {
        if (undo) {
            client.deleteFavorite(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    super.onFailure(statusCode, headers, throwable, response);
                    Log.e("Favorite failure", response.toString());
                }
            }, idToReply);
        } else {
            client.postFavorite(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    super.onFailure(statusCode, headers, throwable, response);
                    Log.e("Favorite failure", response.toString());
                }
            }, idToReply);
        }
    }

    public void onComposeDone(String text, String idToReply) {
        tcfReply.dismiss();
        Tweet newTweet = new Tweet();
        newTweet.setUser(tweets.get(0).getUser());
        newTweet.setBody(text);
        newTweet.setCreatedAt(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy").format(Calendar.getInstance().getTime()));
        tweets.add(0, newTweet);
        aTweets.notifyDataSetChanged();

        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Tweet failure", responseString);
            }
        }, text, idToReply);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
