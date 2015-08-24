package practice.idlycyme.instagramclient;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by cyme on 8/24/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
}
