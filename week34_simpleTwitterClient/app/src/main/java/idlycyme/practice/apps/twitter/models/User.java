package idlycyme.practice.apps.twitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
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

}
