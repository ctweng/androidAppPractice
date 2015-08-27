package practice.idlycyme.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyme on 8/27/15.
 */
public class InstagramDetailAdapter extends ArrayAdapter<InstagramComment> {
    private InstagramPhoto photo;
    public InstagramDetailAdapter(Context context, List objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
    public void setPhoto(InstagramPhoto photo) {
        this.photo = photo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            // Show photo detail as PhotoActivity
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            }
            TextView tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
            ImageView ivPhoto = (ImageView)convertView.findViewById(R.id.ivPhoto);
            TextView tvLikeCount = (TextView)convertView.findViewById(R.id.tvLikeCount);
            TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
            ImageView ivUser = (ImageView)convertView.findViewById(R.id.ivUser);
            TextView tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);

            tvCaption.setText(photo.caption);
            tvLikeCount.setText(Integer.toString(photo.likesCount));
            tvUsername.setText(photo.user.username);
            ivPhoto.setImageResource(0);
            Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.pi_image).into(ivPhoto);
            ivUser.setImageResource(0);
            Picasso.with(getContext()).load(photo.user.profilePhotoUrl).placeholder(R.drawable.pi_profile).transform(new CircleTransform()).into(ivUser);
            tvCreatedTime.setText(photo.getCreatedAtRelativeTimeSpan());
        } else {
            // Show all comments
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
            }
            InstagramComment comment = getItem(position);
            TextView tvCommentUsername = (TextView)convertView.findViewById(R.id.tvCommentUsername);
            TextView tvCommentText = (TextView)convertView.findViewById(R.id.tvCommentText);
            ImageView ivCommentUser = (ImageView)convertView.findViewById(R.id.ivCommentUser);
            tvCommentUsername.setText(comment.user.username);
            tvCommentText.setText(comment.text);
            Picasso.with(getContext()).load(comment.user.profilePhotoUrl).placeholder(R.drawable.pi_profile).transform(new CircleTransform()).into(ivCommentUser);
        }
        return  convertView;
    }


}
