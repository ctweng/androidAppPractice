package idlycyme.practice.apps.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.models.User;

/**
 * Created by cyme on 12/25/15.
 */
public class UsersArrayAdapter extends ArrayAdapter<User> {
    public UsersArrayAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        ImageView ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfile);
        TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
        TextView tvScreenname = (TextView)convertView.findViewById(R.id.tvScreenname);

        tvUsername.setText(user.getName());
        tvScreenname.setText(user.getScreenname());
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivProfileImage);
        return convertView;
    }
}
