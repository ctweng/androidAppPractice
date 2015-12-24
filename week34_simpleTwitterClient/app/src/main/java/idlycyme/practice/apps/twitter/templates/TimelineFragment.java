package idlycyme.practice.apps.twitter.templates;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.activities.BaseTwitterActivity;
import idlycyme.practice.apps.twitter.activities.TimelineActivity;
import idlycyme.practice.apps.twitter.adapters.TweetsArrayAdapter;
import idlycyme.practice.apps.twitter.libraries.EndlessScrollListener;
import idlycyme.practice.apps.twitter.libraries.TwitterActionDelegate;
import idlycyme.practice.apps.twitter.models.Tweet;

/**
 * Created by cyme on 12/17/15.
 */
public class TimelineFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, TwitterActionDelegate {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_TYPE = "ARG_TYPE";

    private int mPage;
    private String mType;

    public TweetsArrayAdapter aTweets;
    public ListView lvTweets;
    public EndlessScrollListener esListener;
    public SwipeRefreshLayout swipeContainer;
    public String lastTweetId = "";

    public static TimelineFragment newInstance(int position, String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putInt(ARG_PAGE, position);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mType = getArguments().getString(ARG_TYPE);
        Log.i("--------------mpage---", String.valueOf(mPage));

        // setup load-more
        esListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                Log.i("on load more ", "in scroll listener");
                ((TimelineActivity)getActivity()).onLoadData(lastTweetId, mType);
            }
        };

        // setup ui content
        aTweets = new TweetsArrayAdapter(this, new ArrayList());

        if (mType.equals("home")) {
            ((TimelineActivity)getActivity()).onLoadCacheData(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // setup pull-to-refresh
        setupPullToRefresh(view);

        lvTweets = (ListView)view.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnItemClickListener(TimelineFragment.this);
        lvTweets.setOnScrollListener(esListener);

        return view;
    }

    private void setupPullToRefresh(View view) {
        swipeContainer = (SwipeRefreshLayout)view;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                if (!((BaseTwitterActivity)getActivity()).isNetworkAvailable(true)) {
                    swipeContainer.setRefreshing(false);
                    return;
                }
                lastTweetId = "";
                ((TimelineActivity)getActivity()).onLoadData(lastTweetId, mType);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        TweetDetailFragment tcfReply = TweetDetailFragment.newInstance(aTweets.getItem(i));
        tcfReply.show(fm, "fragment_tweet_detail");
    }

    @Override
    public void onClick(View view) {
        if (!((BaseTwitterActivity)getActivity()).isNetworkAvailable(true)) {
            return;
        }
        int position = (Integer)view.getTag();
        Tweet tweet = aTweets.getItem(position);
        String id = tweet.getIdString();
        ImageButton button;
        switch (view.getId()) {
            case  R.id.ibReply:
                ((TimelineActivity)getActivity()).onReply(tweet);

                break;
            case R.id.ibFavorite:
                button = (ImageButton)view;
                ((TimelineActivity)getActivity()).onFavorite(id, tweet.getFavorited());
                tweet.setFavorited(!tweet.getFavorited());
                if (tweet.getFavorited()) {
                    button.setImageResource(R.drawable.ic_favorite_on);
                } else {
                    button.setImageResource(R.drawable.ic_favorite);
                }

                break;
            case R.id.ibRetweet:
                button = (ImageButton)view;
                ((TimelineActivity)getActivity()).onRetweet(id, tweet.getRetweeted());
                tweet.setRetweeted(!tweet.getRetweeted());
                if (tweet.getRetweeted()) {
                    button.setImageResource(R.drawable.ic_retweet_on);
                } else {
                    button.setImageResource(R.drawable.ic_retweet);
                }

                break;
            case R.id.ivProfileImage:
                Log.i("ewwf", "gggg");
                ((TimelineActivity)getActivity()).onProfileImage(tweet.getUser());

                break;
            default:
                break;
        }
    }

    @Override
    public void didLoadData(ArrayList<Tweet> tweets) {
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
    public void didLoadDataFailure(JSONObject errorResponse) {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void didPostTweet(Tweet tweet) {
        aTweets.insert(tweet, 0);
        aTweets.notifyDataSetChanged();
    }


}
