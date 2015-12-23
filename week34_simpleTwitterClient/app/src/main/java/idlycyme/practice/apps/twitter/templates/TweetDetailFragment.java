package idlycyme.practice.apps.twitter.templates;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.activities.TimelineActivity;
import idlycyme.practice.apps.twitter.libraries.TwitterActionDelegate;
import idlycyme.practice.apps.twitter.models.Tweet;

/**
 * Created by cyme on 12/19/15.
 */
public class TweetDetailFragment extends DialogFragment implements View.OnClickListener {
    private static final String ARG_TWEET = "param_tweet";

    // TODO: Rename and change types of parameters
    private Tweet tweet;

    public static TweetDetailFragment newInstance(Tweet tweet) {
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET, tweet);
        fragment.setArguments(args);
        return fragment;
    }

    public TweetDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tweet = (Tweet)getArguments().getSerializable(ARG_TWEET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweet_detail, container, false);

        // button click listener
        ImageButton ibReply = (ImageButton)view.findViewById(R.id.ibDetailReply);
        ibReply.setOnClickListener(this);

        ImageButton ibFavorite = (ImageButton)view.findViewById(R.id.ibDetailFavorite);
        ibFavorite.setOnClickListener(this);

        if (tweet.getFavorited()) {
            ibFavorite.setImageResource(R.drawable.ic_favorite_on);
        } else {
            ibFavorite.setImageResource(R.drawable.ic_favorite);
        }

        ImageButton ibRetweet = (ImageButton)view.findViewById(R.id.ibDetailRetweet);
        ibRetweet.setOnClickListener(this);

        if (tweet.getRetweeteable() == false) {
            ibRetweet.setEnabled(false);
        } else {
            ibRetweet.setEnabled(true);
        }
        if (tweet.getRetweeted()) {
            ibRetweet.setImageResource(R.drawable.ic_retweet_on);
        } else {
            ibRetweet.setImageResource(R.drawable.ic_retweet);
        }


        ImageView ivProfileImage = (ImageView)view.findViewById(R.id.ivDetailProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        TextView tvUsername = (TextView)view.findViewById(R.id.tvDetailUsername);
        TextView tvBody = (TextView)view.findViewById(R.id.tvDetailBody);
        TextView tvTimestamp = (TextView)view.findViewById(R.id.tvDetailTime);
        TextView tvScreenname = (TextView)view.findViewById(R.id.tvDetailScreenname);
        TextView tvLikeNumber = (TextView)view.findViewById(R.id.tvNumber1);
        TextView tvRetweetNumber = (TextView)view.findViewById(R.id.tvNumber2);
        TextView tvLikeTitle = (TextView)view.findViewById(R.id.tvTitle1);
        TextView tvRetweetTitle = (TextView)view.findViewById(R.id.tvTitle2);

        tvUsername.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvTimestamp.setText(tweet.getCreatedAt());
        tvScreenname.setText("@" + tweet.getUser().getScreenname());
        tvLikeNumber.setText(String.valueOf(tweet.getFavoriteCount()));
        tvLikeTitle.setText("favorited");
        tvRetweetNumber.setText(String.valueOf(tweet.getRetweetCount()));
        tvRetweetTitle.setText("retweeted");

        return view;
    }

    @Override
    public void onClick(View view) {
        String id = tweet.getIdString();
        ImageButton button = (ImageButton)view;
        switch (view.getId()) {
            case  R.id.ibDetailReply:
                ((TimelineActivity)getActivity()).onReply(tweet);

                break;
            case R.id.ibDetailFavorite:
                ((TimelineActivity)getActivity()).onFavorite(id, tweet.getFavorited());
                tweet.setFavorited(!tweet.getFavorited());
                if (tweet.getFavorited()) {
                    button.setImageResource(R.drawable.ic_favorite_on);
                } else {
                    button.setImageResource(R.drawable.ic_favorite);
                }

                break;
            case R.id.ibDetailRetweet:
                ((TimelineActivity)getActivity()).onFavorite(id, tweet.getRetweeted());
                tweet.setRetweeted(!tweet.getRetweeted());
                if (tweet.getRetweeted()) {
                    button.setImageResource(R.drawable.ic_retweet_on);
                } else {
                    button.setImageResource(R.drawable.ic_retweet);
                }

                break;
            default:
                break;
        }
    }

}
