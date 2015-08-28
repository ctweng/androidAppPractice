package practice.idlycyme.instagramclient;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class InstagramPhoto implements Serializable {
    public String username;
    public String caption;
    public String imageUrl;
    public int imageHeight;
    public int likesCount;
    public InstagramUser user;
    public long createdAtEpochSec;
    public ArrayList<InstagramComment> comments;

    public CharSequence getCreatedAtRelativeTimeSpan () {
        return DateUtils.getRelativeTimeSpanString(createdAtEpochSec*1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
    public void setComments(JSONArray commentsJSON) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        } else  {
            this.comments.clear();
        }
        try {
            for (int i=0; i<commentsJSON.length(); i++) {
                JSONObject commentJSON = commentsJSON.getJSONObject(i);
                InstagramComment comment = new InstagramComment(commentJSON);
                comments.add(0, comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
