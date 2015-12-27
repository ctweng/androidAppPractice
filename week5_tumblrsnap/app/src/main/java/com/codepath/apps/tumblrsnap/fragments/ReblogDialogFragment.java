package com.codepath.apps.tumblrsnap.fragments;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrClient;
import com.codepath.apps.tumblrsnap.models.Photo;
import com.codepath.apps.tumblrsnap.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cyme on 12/27/15.
 */
public class ReblogDialogFragment extends DialogFragment {
    Photo photo;

    static String ARG_PHOTO = "photo";
    private TumblrClient client;
    private EditText etComment;

    public static ReblogDialogFragment newInstance(Photo photo) {
        ReblogDialogFragment fm = new ReblogDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, photo);
        fm.setArguments(args);
        return fm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photo = (Photo)getArguments().getSerializable(ARG_PHOTO);
        }
        client = ((TumblrClient) TumblrClient.getInstance(
                TumblrClient.class, getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Reblog & Comment");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reblog_dialog, container, false);

        ImageView ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(android.R.color.transparent);
        ImageLoader.getInstance().displayImage(photo.getPhotoUrl(), ivPhoto);

        TextView tvUsername = (TextView)view.findViewById(R.id.tvUsername);
        tvUsername.setText(photo.getBlogName());

        etComment = (EditText)view.findViewById(R.id.etComment);

        Button btSubmit = (Button)view.findViewById(R.id.btSend);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                client.createReblogPost(User.currentUser().getBlogHostname(), photo.getId(), photo.getReblogKey(), comment, new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                        Log.i("reblog failed", jsonObject.toString());
                    }
                });
                dismiss();
                // todo: refractor hardcoded 4
                getTargetFragment().onActivityResult(4, Activity.RESULT_OK, getActivity().getIntent());
            }
        });
        return view;
    }
}
