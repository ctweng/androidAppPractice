package practice.idlycyme.instagramclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by cyme on 8/25/15.
 */
public class InstagramUser implements Serializable {
    public String username;
    public String profilePhotoUrl;
    public String id;
    public String fullName;

    public InstagramUser(JSONObject userJSON) {
        try {
            username = userJSON.getString("username");
            id = userJSON.getString("id");
            fullName = userJSON.getString("full_name");
            profilePhotoUrl = userJSON.getString("profile_picture");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
