package idlycyme.practice.apps.twitter.templates;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.models.Tweet;
import idlycyme.practice.apps.twitter.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TweetComposeFragment.OnComposeDoneListener} interface
 * to handle interaction events.
 * Use the {@link TweetComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetComposeFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TWEET = "param_tweet";
    private static final String ARG_AUTHOR = "param_author";
    private static final int DEFAULT_WORD_LIMIT = 140;
    private static final String DEFAULT_WORD_UNIT = "Words";

    // TODO: Rename and change types of parameters
    private Tweet tweet;
    private User author;
    private TextView tvScreenname;
    private TextView tvUsername;
    private TextView tvWordCount;
    private ImageView ivProfile;
    private EditText etText;
    private Button btnConfirm;

    private OnComposeDoneListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tweet Parameter 1.
     * @return A new instance of fragment TweetComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TweetComposeFragment newInstance(User author, Tweet tweet) {
        TweetComposeFragment fragment = new TweetComposeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET, tweet);
        args.putSerializable(ARG_AUTHOR, author);
        fragment.setArguments(args);
        return fragment;
    }

    public TweetComposeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tweet = (Tweet)getArguments().getSerializable(ARG_TWEET);
            author = (User)getArguments().getSerializable(ARG_AUTHOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweet_compose, container, false);
        ivProfile = (ImageView)view.findViewById(R.id.ivComposeAuthor);
        tvScreenname = (TextView)view.findViewById(R.id.tvComposeScreenname);
        tvUsername = (TextView)view.findViewById(R.id.tvComposeUsername);
        tvWordCount = (TextView)view.findViewById(R.id.tvComposeWorcCount);
        etText = (EditText)view.findViewById(R.id.etComposeText);
        btnConfirm = (Button)view.findViewById(R.id.btnComposeConfirm);

        tvScreenname.setText("@" + author.getScreenname());
        tvUsername.setText(author.getName());
        Picasso.with(getActivity()).load(author.getProfileImageUrl()).into(ivProfile);

        tvWordCount.setText(String.valueOf(DEFAULT_WORD_LIMIT) + " " + DEFAULT_WORD_UNIT);
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int countLeft = DEFAULT_WORD_LIMIT - etText.getText().toString().length();
                if (countLeft == 0) {
                    CharSequence newText = etText.getText().subSequence(0, DEFAULT_WORD_LIMIT - 1);
                    etText.setText(newText);
                    etText.setSelection(DEFAULT_WORD_LIMIT - 1);
                }
                tvWordCount.setText(String.valueOf(countLeft) + " " + DEFAULT_WORD_UNIT);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onComposeDone(etText.getText().toString(), "");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnComposeDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnComposeDoneListener {
        public void onComposeDone(String text, String idToReply);
    }

}
