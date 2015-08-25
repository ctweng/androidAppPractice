package practice.idlycyme.instagramclient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

        tvCaption.setText(photo.caption);
        tvLikeCount.setText(Integer.toString(photo.likesCount));
        tvUsername.setText(photo.user.username);
        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).into(ivPhoto);
        ivUser.setImageResource(0);
        Picasso.with(getContext()).load(photo.user.profilePhotoUrl).into(ivUser);
        return  convertView;
    }
}
