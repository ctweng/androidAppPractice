package practice.idlycyme.instagramclient;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class PhotoDetailActivity extends AppCompatActivity {
    private InstagramDetailAdapter aDetail;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Bundle extras = getIntent().getExtras();
        InstagramPhoto photo = (InstagramPhoto)extras.getSerializable("data");
        // ugly hack for first cell to be replaced by photo detail
        photo.comments.add(0, null);
        aDetail = new InstagramDetailAdapter(this, photo.comments);
        aDetail.setPhoto(photo);
        ListView lvDetail = (ListView)findViewById(R.id.lvPhotos);
        lvDetail.setAdapter(aDetail);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // No pull-to-update here
        swipeContainer.setEnabled(false);
    }
}
