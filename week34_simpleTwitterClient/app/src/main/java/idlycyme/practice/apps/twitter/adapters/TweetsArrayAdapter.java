package idlycyme.practice.apps.twitter.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.templates.SearchFragment;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;

/**
 * Created by cyme on 9/2/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private TimelineFragment fragment;
    private SearchFragment searchFragment;
    private Activity activity;

    public TweetsArrayAdapter(SearchFragment fm, List<Tweet> tweets) {
        super(fm.getContext(), android.R.layout.simple_list_item_1, tweets);
        searchFragment = fm;
    }

    public TweetsArrayAdapter(TimelineFragment fm, List<Tweet> tweets) {
        super(fm.getContext(), android.R.layout.simple_list_item_1, tweets);
        fragment = fm;
    }

    public TweetsArrayAdapter(Activity activity, List<Tweet> tweets) {
        super(activity, android.R.layout.simple_list_item_1, tweets);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        // button click listener
        ImageButton ibReply = (ImageButton)convertView.findViewById(R.id.ibReply);
        if (fragment != null) {
            ibReply.setOnClickListener(fragment);
        } else if (searchFragment != null) {
            ibReply.setOnClickListener(searchFragment);
        } else {
            ibReply.setOnClickListener((View.OnClickListener) activity);
        }
        ibReply.setTag(position);

        ImageButton ibFavorite = (ImageButton)convertView.findViewById(R.id.ibFavorite);
        if (fragment != null) {
            ibFavorite.setOnClickListener(fragment);
        } else if (searchFragment != null) {
            ibFavorite.setOnClickListener(searchFragment);
        } else {
            ibFavorite.setOnClickListener((View.OnClickListener) activity);
        }
        ibFavorite.setTag(position);
        if (tweet.getFavorited()) {
            ibFavorite.setImageResource(R.drawable.ic_favorite_on);
        } else {
            ibFavorite.setImageResource(R.drawable.ic_favorite);
        }

        ImageButton ibRetweet = (ImageButton)convertView.findViewById(R.id.ibRetweet);
        if (fragment != null) {
            ibRetweet.setOnClickListener(fragment);
        } else if (searchFragment != null) {
            ibRetweet.setOnClickListener(searchFragment);
        } else {
            ibRetweet.setOnClickListener((View.OnClickListener) activity);
        }
        ibRetweet.setTag(position);

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

        ImageView ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
        TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView)convertView.findViewById(R.id.tvBody);
        TextView tvTimestamp = (TextView)convertView.findViewById(R.id.tvTimestamp);

        tvUsername.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvTimestamp.setText(tweet.getRelativeTimeAgo());
        ivProfileImage.setImageResource(android.R.color.transparent);

        if (fragment != null) {
            ivProfileImage.setOnClickListener(fragment);
        } else if (searchFragment != null) {
            ivProfileImage.setOnClickListener(searchFragment);
        } else {
            ivProfileImage.setOnClickListener((View.OnClickListener) activity);
        }
        ivProfileImage.setTag(position);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        return convertView;
    }

}
