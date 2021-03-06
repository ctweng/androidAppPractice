package idlycyme.practice.apps.twitter.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by cyme on 9/2/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable{
    @Column(name = "body")
    private String body;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "idString")
    private String idString;

    @Column(name = "urls")
    private ArrayList<String> urls;

    private ArrayList<String> imageUrlStrings;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "favorited")
    private Boolean favorited;

    @Column(name = "retweeted")
    private Boolean retweeted;

    @Column(name = "retweeteable")
    private Boolean retweeteable;

    @Column(name = "retweetCount")
    private long retweetCount;

    @Column(name = "favorite_count")
    private long favoriteCount;

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setRetweeteable(Boolean retweeteable) {
        this.retweeteable = retweeteable;
    }

    public Boolean getRetweeteable() {
        return retweeteable;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setIdString(String idString) {
        this.idString = idString;
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

    public Tweet() {
        super();
    };

    public static Tweet fromJSON(JSONObject jsonObject) {
        Log.i("tweet json = ", jsonObject.toString());
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.idString = jsonObject.getString("id_str");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.favoriteCount = jsonObject.getLong("favorite_count");
            tweet.retweetCount = jsonObject.getLong("retweet_count");
            tweet.urls = new ArrayList<>();
            tweet.retweeteable = true;
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

    public static ArrayList<Tweet> fromJSONArrayAddRetweeteable(JSONArray jsonArray, long uid) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(json);
                if (tweet != null) {
                    if (tweet.getUser().getUid() == uid) {
                        tweet.retweeteable = false;
                    }
                    //Log.i("Compare uid", String.valueOf(tweet.retweeteable) + " " + String.valueOf(uid) + " " + String.valueOf(tweet.getUser().getUid()));
                    tweet.saveAllProperties();
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(json);
                if (tweet != null) {
                    tweet.saveAllProperties();
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public Long saveAllProperties() {
        getUser().save();
        return super.save();
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

    public String getIdString() {
        return idString;
    }

    public ArrayList<String> getUrls() { return  urls; }
}
