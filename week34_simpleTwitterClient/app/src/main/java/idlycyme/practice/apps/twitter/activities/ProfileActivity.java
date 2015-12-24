package idlycyme.practice.apps.twitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.adapters.TweetsArrayAdapter;
import idlycyme.practice.apps.twitter.libraries.EndlessScrollListener;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;
import idlycyme.practice.apps.twitter.templates.TweetDetailFragment;

/**
 * Created by cyme on 12/24/15.
 */
public class ProfileActivity extends BaseTwitterActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private User user;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private EndlessScrollListener esListener;
    private SwipeRefreshLayout swipeContainer;
    private String lastTweetId = "";
    private TweetComposeFragment tcfReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("user");
        Log.i("user is ", user.getScreenname());

        setupItemProfileMainView();
        setupItemStatView();
        setupListViewHelper();
    }

    private void setupListViewHelper() {
        esListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                Log.i("on load more ", "in scroll listener");
                onLoadData(lastTweetId, "user");
            }
        };

        // setup ui content
        aTweets = new TweetsArrayAdapter(this, new ArrayList());

        View view = findViewById(R.id.fragment_timeline);
        // setup pull-to-refresh
        setupPullToRefresh(view);

        lvTweets = (ListView)findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnItemClickListener(this);
        lvTweets.setOnScrollListener(esListener);
    }

    private void setupItemProfileMainView() {
        TextView tvScreenname = (TextView)findViewById(R.id.tvScreenname);
        tvScreenname.setText("@" + user.getScreenname());

        TextView tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvUsername.setText(user.getName());

        TextView tvDescription = (TextView)findViewById(R.id.tvDescription);
        tvDescription.setText(user.getDescription());

        ImageView ivProfile = (ImageView)findViewById(R.id.ivProfileImage);
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfile);

        ImageView ivBanner = (ImageView)findViewById(R.id.ivProfileBanner);
        Picasso.with(this).load(user.getProfileBannerUrl()).into(ivBanner);
    }

    private void setupItemStatView() {
        TextView tvTitle1 = (TextView)findViewById(R.id.tvTitle1);
        tvTitle1.setText("#Following");

        TextView tvNumber1 = (TextView)findViewById(R.id.tvNumber1);
        tvNumber1.setText(String.valueOf(user.getFriendsCount()));

        TextView tvTitle2 = (TextView)findViewById(R.id.tvTitle2);
        tvTitle2.setText("#Follwers");

        TextView tvNumber2 = (TextView)findViewById(R.id.tvNumber2);
        tvNumber2.setText(String.valueOf(user.getFollowersCount()));
    }

    @Override
    public void onLoadData(String lastTweetId, final String type) {
        Log.i("type is ", String.valueOf(type) + "  last tweet id is " + lastTweetId);
        if (!isNetworkAvailable(true)) {
            return;
        }
        if (user != null && user.getScreenname().length() == 0) {
            return;
        }
        willMakeRequest();
        client.getUserTimeline(new JsonHttpResponseHandler() {
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
                    Log.d("getUserTimeline failed", errorResponse.toString());
                }
            }
        }, user.getScreenname(), lastTweetId, limit);
    }

    private void setupPullToRefresh(View view) {
        swipeContainer = (SwipeRefreshLayout)view;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable(true)) {
                    swipeContainer.setRefreshing(false);
                    return;
                }
                lastTweetId = "";
                onLoadData(lastTweetId, "user");
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void onComposeDone(String text, String idToReply) {
        tcfReply.dismiss();
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();
        client.postTweet(getJsonHttpResponseHandler(), text, idToReply);
    }

    public void onReply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        tcfReply = TweetComposeFragment.newInstance(loggedInUser, tweet);
        tcfReply.show(fm, "fragment_edit_name");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fm = getSupportFragmentManager();
        TweetDetailFragment tcf = TweetDetailFragment.newInstance(aTweets.getItem(i));
        tcf.show(fm, "fragment_tweet_detail");
    }

    @Override
    public void didLoadDataSuccess(ArrayList<Tweet> tweets, String type) {
        swipeContainer.setRefreshing(false);

        if (lastTweetId == null || lastTweetId.equals("")) {
            aTweets.clear();
            aTweets.notifyDataSetChanged();
        }

        if (lastTweetId != null && lastTweetId.length() > 0 && tweets.get(tweets.size() - 1).getIdString().equals(lastTweetId) == true) {
            esListener.noMoreData = true;
        } else {
            lastTweetId = tweets.get(tweets.size() - 1).getIdString();
            aTweets.addAll(tweets);
            aTweets.notifyDataSetChanged();
        }
    }

    @Override
    public void didLoadDataFailure(JSONObject errorResponse, String type) {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        if (!isNetworkAvailable(true)) {
            return;
        }
        Log.i("on click listener","");
        int position = (Integer)view.getTag();
        Tweet tweet = aTweets.getItem(position);
        String id = tweet.getIdString();
        ImageButton button;
        switch (view.getId()) {
            case  R.id.ibReply:
               onReply(tweet);

                break;
            case R.id.ibFavorite:
                Log.i("on click listener"," favorite ");
                button = (ImageButton)view;
                onFavorite(id, tweet.getFavorited());
                tweet.setFavorited(!tweet.getFavorited());
                if (tweet.getFavorited()) {
                    button.setImageResource(R.drawable.ic_favorite_on);
                } else {
                    button.setImageResource(R.drawable.ic_favorite);
                }

                break;
            case R.id.ibRetweet:
                button = (ImageButton)view;
                onRetweet(id, tweet.getRetweeted());
                tweet.setRetweeted(!tweet.getRetweeted());
                if (tweet.getRetweeted()) {
                    button.setImageResource(R.drawable.ic_retweet_on);
                } else {
                    button.setImageResource(R.drawable.ic_retweet);
                }

                break;
            case R.id.ivProfileImage:
                //onProfileImage(tweet.getUser());

                break;
            default:
                break;
        }
    }

}
