package practice.idlycyme.instagramclient;
import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class InstagramComment implements Serializable {
    public String id;
    public String text;
    public InstagramUser user;
    public long createdAtEpochSec;
    public CharSequence getCreatedAtRelativeTimeSpan () {
        return DateUtils.getRelativeTimeSpanString(createdAtEpochSec * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public InstagramComment(JSONObject commentJSON) {
        try {
            this.createdAtEpochSec = commentJSON.getLong("created_time");
            this.id = commentJSON.getString("id");
            this.text = commentJSON.getString("text");
            this.user = new InstagramUser(commentJSON.getJSONObject("from"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}