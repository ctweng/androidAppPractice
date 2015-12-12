package idlycyme.practice.apps.twitter.models;

import android.media.Image;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by cyme on 9/2/15.
 */
public class Tweet implements Serializable{
    private String body;
    private long uid;
    private String createdAt;
    private String id;
    private ArrayList<String> urls;
    private ArrayList<String> imageUrlStrings;
    private User user;

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public void setImageUrlStrings(ArrayList<String> imageUrlStrings) {
        this.imageUrlStrings = imageUrlStrings;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Log.i("tweet json = ", jsonObject.toString());
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.id = jsonObject.getString("id_str");
            tweet.urls = new ArrayList<>();
            JSONObject entities = jsonObject.getJSONObject("entities");
            if (entities.has("urls")) {
                for (int i = 0; i < entities.getJSONArray("urls").length(); i++) {
                    JSONObject urlObject = entities.getJSONArray("urls").getJSONObject(i);
                    tweet.urls.add(urlObject.getString("url"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;

    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(json);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUrls() { return  urls; }
}
