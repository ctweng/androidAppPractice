package idlycyme.practice.apps.twitter.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.TwitterApplication;
import idlycyme.practice.apps.twitter.libraries.EndlessScrollListener;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.adapters.TweetsArrayAdapter;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class TimelineActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TweetComposeFragment.OnComposeDoneListener{
    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private TweetComposeFragment tcfReply;
    private User loggedInUser;
    private String lastTweetId = "";
    private int limit = 20;
    private EndlessScrollListener esListener;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // setup pull-to-refresh
        setupPullToRefresh();

        // setup ui content
        loggedInUser = (User)getIntent().getSerializableExtra("loggedInUser");
        lvTweets = (ListView)findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnItemClickListener(TimelineActivity.this);
        client = TwitterApplication.getRestClient();
        if (loggedInUser == null) {
            client.getLoggedInCredential(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    loggedInUser = User.fromJSON(response);
                }
            });
        }

        // setup load-more
        esListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                populateTimeline();
            }
        };
        lvTweets.setOnScrollListener(esListener);
    }

    private void setupPullToRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.activity_timeline);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastTweetId = "";
                tweets.clear();
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                //super.onSuccess(statusCode, headers, json);
                ArrayList<Tweet> tweets;
                if (loggedInUser != null) {
                    tweets = Tweet.fromJSONArrayAddRetweeteable(jsonArray, loggedInUser.getUid());
                } else {
                    tweets = Tweet.fromJSONArray(jsonArray);
                }
                if (tweets.get(tweets.size() - 1).getId().equals(lastTweetId) == true) {
                    esListener.noMoreData = true;
                } else {
                    lastTweetId = tweets.get(tweets.size() - 1).getId();
                    aTweets.addAll(tweets);
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                String mockJSON = "[{\"created_at\":\"Sat Jun 27 14:06:36 +0000 2015\",\"id\":614796977096658944,\"id_str\":\"614796977096658944\",\"text\":\"@idlycyme reply test\",\"source\":\"<a href=\\\"http:\\/\\/twitter.com\\\" rel=\\\"nofollow\\\">Twitter Web Client<\\/a>\",\"truncated\":false,\"in_reply_to_status_id\":614621387693514752,\"in_reply_to_status_id_str\":\"614621387693514752\",\"in_reply_to_user_id\":3256309285,\"in_reply_to_user_id_str\":\"3256309285\",\"in_reply_to_screen_name\":\"idlycyme\",\"user\":{\"id\":3256309285,\"id_str\":\"3256309285\",\"name\":\"yd\",\"screen_name\":\"idlycyme\",\"location\":\"\",\"description\":\"\",\"url\":null,\"entities\":{\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":1,\"friends_count\":0,\"listed_count\":0,\"created_at\":\"Fri Jun 26 04:36:22 +0000 2015\",\"favourites_count\":0,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":2,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/abs.twimg.com\\/sticky\\/default_profile_images\\/default_profile_5_normal.png\",\"profile_image_url_https\":\"https:\\/\\/abs.twimg.com\\/sticky\\/default_profile_images\\/default_profile_5_normal.png\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"has_extended_profile\":false,\"default_profile\":true,\"default_profile_image\":true,\"following\":false,\"follow_request_sent\":false,\"notifications\":false},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"symbols\":[],\"user_mentions\":[{\"screen_name\":\"idlycyme\",\"name\":\"yd\",\"id\":3256309285,\"id_str\":\"3256309285\",\"indices\":[0,9]}],\"urls\":[]},\"favorited\":false,\"retweeted\":false,\"lang\":\"en\"},{\"created_at\":\"Sat Jun 27 02:28:52 +0000 2015\",\"id\":614621387693514752,\"id_str\":\"614621387693514752\",\"text\":\"wtf i got blocked?\",\"source\":\"<a href=\\\"http:\\/\\/twitter.com\\\" rel=\\\"nofollow\\\">Twitter Web Client<\\/a>\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":3256309285,\"id_str\":\"3256309285\",\"name\":\"yd\",\"screen_name\":\"idlycyme\",\"location\":\"\",\"description\":\"\",\"url\":null,\"entities\":{\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":1,\"friends_count\":0,\"listed_count\":0,\"created_at\":\"Fri Jun 26 04:36:22 +0000 2015\",\"favourites_count\":0,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":2,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/abs.twimg.com\\/sticky\\/default_profile_images\\/default_profile_5_normal.png\",\"profile_image_url_https\":\"https:\\/\\/abs.twimg.com\\/sticky\\/default_profile_images\\/default_profile_5_normal.png\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"has_extended_profile\":false,\"default_profile\":true,\"default_profile_image\":true,\"following\":false,\"follow_request_sent\":false,\"notifications\":false},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":1,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"symbols\":[],\"user_mentions\":[],\"urls\":[]},\"favorited\":false,\"retweeted\":false,\"lang\":\"en\"}]";
                try {
                    aTweets.addAll(Tweet.fromJSONArray(new JSONArray(mockJSON)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
                Log.d("getHomeTimeline failed", errorResponse.toString());
            }
        }, lastTweetId, limit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.miLogout:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.miCompose:
                FragmentManager fm = getSupportFragmentManager();
                tcfReply = TweetComposeFragment.newInstance(loggedInUser, null);
                tcfReply.show(fm, "fragment_edit_name");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("zzzz", "zzzzzz");
        Intent intent = new Intent(this, TweetActivity.class);
        //Bundle params = new Bundle();
        //params.putSerializable("tweet", tweets.get(i));
        intent.putExtra("tweet", tweets.get(i));
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
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
                ImageButton button = (ImageButton)view;
                button.setImageResource(R.drawable.ic_favorite_on);
                break;
            case R.id.ibRetweet:
                onRetweet(id, tweet.getRetweeted());
                break;
            default:
                break;
        }
    }

    public void onRetweet(String idToRetweet, Boolean undo) {
        if (undo) {
            client.deleteTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    super.onFailure(statusCode, headers, throwable, response);
                    Log.e("Favorite failure", response.toString());
                }
            }, idToRetweet);
        } else {
            client.postRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    super.onFailure(statusCode, headers, throwable, response);
                    Log.e("Favorite failure", response.toString());
                }
            }, idToRetweet);
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
}
