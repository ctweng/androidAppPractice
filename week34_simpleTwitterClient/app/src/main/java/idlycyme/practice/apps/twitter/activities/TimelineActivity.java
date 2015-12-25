package idlycyme.practice.apps.twitter.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.adapters.TimelineFragmentPagerAdapter;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.SearchFragment;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;

public class TimelineActivity extends BaseTwitterActivity {
    private ViewPager viewPager;
    private String titleMapForPageIndex[] = new String[] {"Home", "Mentions"};
    private String apiMapForPageIndex[] = new String[] {"home", "mentions"};
    private MenuItem miActionProgressItem;
    private SearchFragment sfm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager(), titleMapForPageIndex, apiMapForPageIndex));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Log.i("user is ", loggedInUser.getScreenname().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        //searchView.setMaxWidth(200);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                if (query.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter something to search", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Log.i("new query", query);
                //Log.i("onQtwice", String.valueOf(onQueryTextSubmitTwice));
                //if (onQueryTextSubmitTwice) {
                //fetchSearchResults(query, 0);
                sfm = SearchFragment.newInstance(query);
                sfm.show(getSupportFragmentManager(), "search_fragment");

                //}
                searchView.setQuery("", false);
                searchView.setQueryHint("Current search is " + query);
                //onQueryTextSubmitTwice = !onQueryTextSubmitTwice;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                client.clearAccessToken();
                return true;
            case R.id.miCompose:
                FragmentManager fm = getSupportFragmentManager();
                tcfReply = TweetComposeFragment.newInstance(loggedInUser, null);
                tcfReply.show(fm, "fragment_edit_name");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // dupcliate code, need to refractor
    @Override
    public void onComposeDone(String text, String idToReply) {
        tcfReply.dismiss();
        if (!isNetworkAvailable(true)) {
            return;
        }
        willMakeRequest();
        getTimelineFragmentForType("home").didPostTweet(createPostedTweet(text));
        client.postTweet(getJsonHttpResponseHandler(), text, idToReply);
    }

    private Tweet createPostedTweet(String text) {
        Tweet newTweet = new Tweet();
        newTweet.setUser(loggedInUser);
        newTweet.setBody(text);
        newTweet.setCreatedAt(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy").format(Calendar.getInstance().getTime()));
        newTweet.setFavorited(false);
        newTweet.setRetweeted(false);
        newTweet.setRetweeteable(false);
        return newTweet;
    }

    private TimelineFragment getFragment(int type) {
        TimelineFragmentPagerAdapter adapter = (TimelineFragmentPagerAdapter)viewPager.getAdapter();
        Log.i("current item", String.valueOf(viewPager.getCurrentItem()));
        TimelineFragment fragment = (TimelineFragment)adapter.getItem(viewPager.getCurrentItem());
        return fragment;
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

    private int getPageIndexByType(String type) {
        for (int i=0; i<apiMapForPageIndex.length; i++) {
            if (apiMapForPageIndex[i].equals(type)) {
                return i;
            }
        }
        return 99999;
    }

    private TimelineFragment getTimelineFragmentForType(String type) {
        int position = getPageIndexByType(type);
        if (position >= getSupportFragmentManager().getFragments().size()) {
            return null;
        }

        TimelineFragment fragment = (TimelineFragment)getSupportFragmentManager().getFragments().get(position);
        return fragment;
    }

    @Override
    public void didLoadDataSuccess(ArrayList<Tweet> tweets, String type) {
        TimelineFragment fragment = getTimelineFragmentForType(type);
        if (fragment != null) {
            fragment.didLoadData(tweets);
        }
    }

    @Override
    public void didLoadDataFailure(JSONObject errorResponse, String type) {
        TimelineFragment fragment = getTimelineFragmentForType(type);
        if (fragment != null) {
            fragment.didLoadDataFailure(errorResponse);
        }
    }

    @Override
    public void didLoadSearchDataSuccess(ArrayList<Tweet> tweet, String type) {
        sfm.didLoadData(tweet);
    }

    @Override
    public void didLoadSearchDataFailure(JSONObject errorResponse, String type) {
        sfm.didLoadDataFailure(errorResponse);
    }


    public void willMakeRequest() {
        // Show progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void didMakeRequest() {
        // Hide progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }
}
