package idlycyme.practice.apps.twitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cyme on 9/2/15.
 */
@Table(name = "Users")
public class User extends Model implements Serializable {
    @Column(name = "isLoggedIn")
    private Boolean isLoogedIn;

    @Column(name = "name")
    private String name;

    @Column(name = "uid")
    private long uid;

    @Column(name = "screenname")
    private String screenname;

    @Column(name = "profileImageUrl")
    private String profileImageUrl;

    @Column(name = "profileBannerUrl")
    private String profileBannerUrl;

    @Column(name = "profileBackgroundUrl")
    private String profileBackgroundUrl;

    @Column(name = "followersCount")
    private int followersCount;

    @Column(name = "friendsCount")
    private int friendsCount;

    @Column(name = "listsCount")
    private int listsCount;

    @Column(name = "description")
    private String description;

    public User() {
        super();
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.uid = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenname = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.isLoogedIn = false;
            if (jsonObject.has("profile_banner_url")) {
                user.profileBannerUrl = jsonObject.getString("profile_banner_url");
            }
            user.profileBackgroundUrl = jsonObject.getString("profile_background_image_url");
            user.followersCount = jsonObject.getInt("followers_count");
            user.friendsCount = jsonObject.getInt("friends_count");
            //user.listsCount = jsonObject.getInt("lists_count");
            user.description = jsonObject.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static ArrayList<User> fromJSONArray(JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                User user = User.fromJSON(jsonArray.getJSONObject(i));
                users.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return users;
    }
    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenname() {
        return screenname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }


    public Boolean getIsLoogedIn() {
        return isLoogedIn;
    }

    public void setIsLoogedIn(Boolean isLoogedIn) {
        this.isLoogedIn = isLoogedIn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public void setProfileBannerUrl(String profileBannerUrl) {
        this.profileBannerUrl = profileBannerUrl;
    }

    public String getProfileBackgroundUrl() {
        return profileBackgroundUrl;
    }

    public void setProfileBackgroundUrl(String profileBackgroundUrl) {
        this.profileBackgroundUrl = profileBackgroundUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getListsCount() {
        return listsCount;
    }

    public void setListsCount(int listsCount) {
        this.listsCount = listsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
