package practice.idlycyme.instagramclient;
import android.text.format.DateUtils;
public class InstagramPhoto {
    public String username;
    public String caption;
    public String imageUrl;
    public int imageHeight;
    public int likesCount;
    public InstagramUser user;
    public long createdAtEpochSec;
    public CharSequence getCreatedAtRelativeTimeSpan () {
        return DateUtils.getRelativeTimeSpanString(createdAtEpochSec*1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
}
