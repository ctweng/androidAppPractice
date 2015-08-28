package practice.idlycyme.instagramclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;

    public static final String CLIENT_ID = "5e4bb8b442144e2cad975512543ecdb8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotosAdapter(this, photos);
        ListView lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                InstagramPhoto photo = photos.get(position);
                Intent photo2detail = new Intent();
                photo2detail.setClass(getBaseContext(), PhotoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", photo);
                photo2detail.putExtras(bundle);
                startActivity(photo2detail);
            }
        });
        // first load

    }

    public void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONArray photoJSONs = null;
                photos.clear();
                try {
                    photoJSONs = response.getJSONArray("data");
                    Log.i("DEBUG_JSON", photoJSONs.toString());
                    for (int i = 0; i < photoJSONs.length(); i++) {
                        JSONObject photoJSON = photoJSONs.getJSONObject(i);
                        Log.i("DEBUG_JSON2", photoJSON.toString());
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        if (photoJSON.has("caption") && !photoJSON.isNull("caption")) {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        }
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.user = new InstagramUser(photoJSON.getJSONObject("user"));
                        photo.createdAtEpochSec = photoJSON.getLong("created_time");
                        photo.setComments(photoJSON.getJSONObject("comments").getJSONArray("data"));
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    Log.i("DEBUG", response.toString());
                    e.printStackTrace();
                }
                aPhotos.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
