package idlycyme.practice.apps.twitter.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.adapters.TimelineFragmentPagerAdapter;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;
import idlycyme.practice.apps.twitter.templates.TweetComposeFragment;

public class TimelineActivity extends BaseTwitterActivity implements TweetComposeFragment.OnComposeDoneListener {
    private ViewPager viewPager;
    private TweetComposeFragment tcfReply;
    private String titleMapForPageIndex[] = new String[] {"Home", "Mentions", "Profile"};
    private String apiMapForPageIndex[] = new String[] {"home", "mentions", "user"};

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

    public void onComposeDone(String text, String idToReply) {
        tcfReply.dismiss();
        /*
        Tweet newTweet = new Tweet();
        newTweet.setUser(loggedInUser);
        newTweet.setBody(text);
        newTweet.setCreatedAt(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy").format(Calendar.getInstance().getTime()));
        newTweet.setFavorited(false);
        newTweet.setRetweeted(false);
        newTweet.setRetweeteable(false);
        getCurrentFragment().aTweets.insert(newTweet, 0);
        getCurrentFragment().aTweets.notifyDataSetChanged();
*/
        client.postTweet(new JsonHttpResponseHandler(), text, idToReply);
    }

    private TimelineFragment getFragment(int type) {
        TimelineFragmentPagerAdapter adapter = (TimelineFragmentPagerAdapter)viewPager.getAdapter();
        Log.i("current item", String.valueOf(viewPager.getCurrentItem()));
        TimelineFragment fragment = (TimelineFragment)adapter.getItem(viewPager.getCurrentItem());
        return fragment;
    }

    /*
    public void onLoadCacheData(TimelineFragment fragment) {
        // setup data container

        ArrayList tweets = new ArrayList<>();
        List<Tweet> cachedTweets = new Select().from(Tweet.class).orderBy("createdAt DESC").limit(20).execute();
        if (cachedTweets != null && cachedTweets.size() > 0) {
            tweets.addAll(cachedTweets);
        }

        fragment.aTweets.addAll(tweets);

    }*/

    public void onReply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        tcfReply = TweetComposeFragment.newInstance(loggedInUser, tweet);
        tcfReply.show(fm, "fragment_edit_name");
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
        if (position >= apiMapForPageIndex.length) {
            return null;
        }

        TimelineFragment fragment = (TimelineFragment)getSupportFragmentManager().getFragments().get(position);
        return fragment;
    }

    @Override
    public void didLoadDataSuccess(ArrayList<Tweet> tweet, String type) {
        TimelineFragment fragment = getTimelineFragmentForType(type);
        if (fragment != null) {
            fragment.didLoadData(tweet);
        }
    }

    @Override
    public void didLoadDataFailure(JSONObject errorResponse, String type) {
        TimelineFragment fragment = getTimelineFragmentForType(type);
        if (fragment != null) {
            fragment.didLoadDataFailure(errorResponse);
        }
    }

}
