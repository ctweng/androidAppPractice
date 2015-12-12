package idlycyme.practice.apps.twitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class TweetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
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
        lvTweets = (ListView)findViewById(R.id.lvTweetDetail);
        tweets = new ArrayList<>();
        tweets.add(tweet);
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
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

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
