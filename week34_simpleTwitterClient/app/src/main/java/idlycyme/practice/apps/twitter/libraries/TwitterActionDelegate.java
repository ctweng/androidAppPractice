package idlycyme.practice.apps.twitter.libraries;

import org.json.JSONObject;

import java.util.ArrayList;
import idlycyme.practice.apps.twitter.models.Tweet;

/**
 * Created by cyme on 12/19/15.
 */
public interface TwitterActionDelegate {
    void didLoadData(ArrayList<Tweet> tweets);
    void didLoadDataFailure(JSONObject errorResponse);
    void didPostTweet(Tweet tweet);
}
