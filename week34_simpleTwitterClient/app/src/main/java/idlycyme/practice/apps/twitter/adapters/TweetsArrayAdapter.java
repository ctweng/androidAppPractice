package idlycyme.practice.apps.twitter.adapters;

import android.content.Context;
import android.text.method.LinkMovementMethod;
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

/**
 * Created by cyme on 9/2/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        // button click listener
        ImageButton ibReply = (ImageButton)convertView.findViewById(R.id.ibReply);
        ibReply.setOnClickListener((View.OnClickListener) getContext());
        ibReply.setTag(position);
        ImageButton ibFavorite = (ImageButton)convertView.findViewById(R.id.ibFavorite);
        ibFavorite.setOnClickListener((View.OnClickListener) getContext());
        ibFavorite.setTag(position);
        ImageButton ibRetweet = (ImageButton)convertView.findViewById(R.id.ibRetweet);
        ibRetweet.setOnClickListener((View.OnClickListener) getContext());
        ibRetweet.setTag(position);

        ImageView ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
        TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView)convertView.findViewById(R.id.tvBody);
        TextView tvTimestamp = (TextView)convertView.findViewById(R.id.tvTimestamp);

        tvUsername.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvTimestamp.setText(tweet.getRelativeTimeAgo());
        ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        return convertView;
    }

}
