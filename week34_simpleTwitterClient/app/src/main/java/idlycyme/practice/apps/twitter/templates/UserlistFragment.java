package idlycyme.practice.apps.twitter.templates;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.activities.ProfileActivity;
import idlycyme.practice.apps.twitter.adapters.UsersArrayAdapter;
import idlycyme.practice.apps.twitter.libraries.EndlessScrollListener;
import idlycyme.practice.apps.twitter.models.User;

/**
 * Created by cyme on 12/25/15.
 */
public class UserlistFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private EndlessScrollListener esListener;
    public ListAdapter aUsers;
    private ListView lvUser;
    public String nextCursor = "";
    private User targetUser;
    private String type;
    private static String ARG_USER = "user";
    private static String ARG_TYPE = "type";

    public static UserlistFragment newInstance(User user, String type) {
        UserlistFragment fragment = new UserlistFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetUser = (User)getArguments().getSerializable(ARG_USER);
        type = getArguments().getString(ARG_TYPE);
        aUsers = new UsersArrayAdapter(getContext(), android.R.layout.simple_list_item_1, new ArrayList());
        // setup load-more
        ((ProfileActivity) getActivity()).onLoadUserLists(type, targetUser, nextCursor);
        esListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                Log.i("on load more ", "in scroll listener");
                if (type.equals("friends")) {
                    //((ProfileActivity) getActivity()).onLoadFriends(targetUser, nextCursor);
                } else {

                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlist, container, false);
        lvUser = (ListView)view.findViewById(R.id.lvUsers);
        lvUser.setAdapter(aUsers);
        lvUser.setOnItemClickListener(this);
        lvUser.setOnScrollListener(esListener);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
