package practice.idlycyme.instagramclient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import practice.idlycyme.instagramclient.CircleTransform;

import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }
        TextView tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
        ImageView ivPhoto = (ImageView)convertView.findViewById(R.id.ivPhoto);
        TextView tvLikeCount = (TextView)convertView.findViewById(R.id.tvLikeCount);
        TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
        ImageView ivUser = (ImageView)convertView.findViewById(R.id.ivUser);
        TextView tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);
        ViewGroup vgComments = (ViewGroup)convertView.findViewById(R.id.llComments);

        tvCaption.setText(photo.caption);
        tvLikeCount.setText(Integer.toString(photo.likesCount)+" likes");
        tvUsername.setText(photo.user.username);
        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.pi_load).into(ivPhoto);
        ivUser.setImageResource(0);
        Picasso.with(getContext()).load(photo.user.profilePhotoUrl).placeholder(R.drawable.pi_profile).transform(new CircleTransform()).into(ivUser);
        tvCreatedTime.setText(photo.getCreatedAtRelativeTimeSpan());

        int nComment = photo.comments.size();
        int totalComment = nComment;
        if (nComment > 2) {
            nComment = 2;
        }
        vgComments.removeAllViews();
        for (int i=0; i<nComment; i++) {
            // Get the latest one
            InstagramComment comment = photo.comments.get(totalComment-i-1);
            View vComment = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
            TextView tvCommentUsername = (TextView)vComment.findViewById(R.id.tvCommentUsername);
            TextView tvCommentText = (TextView)vComment.findViewById(R.id.tvCommentText);
            TextView tvCommentCreatedAt = (TextView)vComment.findViewById(R.id.tvCommentCreatedAt);
            ImageView ivCommentProfile = (ImageView)vComment.findViewById(R.id.ivCommentUser);
            tvCommentUsername.setText(comment.user.username);
            tvCommentText.setText(comment.text);
            tvCommentCreatedAt.setText(comment.getCreatedAtRelativeTimeSpan());
            vgComments.addView(vComment);
            Picasso.with(getContext()).load(comment.user.profilePhotoUrl).placeholder(R.drawable.pi_profile).transform(new CircleTransform()).into(ivCommentProfile);
        }

        return  convertView;
    }
}
