package idlycyme.practice.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import idlycyme.practice.gridimagesearch.R;
import idlycyme.practice.gridimagesearch.adapters.ImageResultsAdapter;
import idlycyme.practice.gridimagesearch.libraries.EndlessScrollListener;
import idlycyme.practice.gridimagesearch.models.ImageResult;

public class SearchActivity extends AppCompatActivity {
    private EditText etQuery;
    private GridView gvResults;
    private AsyncHttpClient apiClient;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private String nextSearchText;
    private int offestIncrement = 8;
    private int maxSearchOffset;
    private static final String searchURL = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        apiClient = new AsyncHttpClient();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, this.imageResults);
        gvResults.setAdapter(this.aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchSearchResults(nextSearchText, (page-1)*offestIncrement);
            }
        });
;
    }

    private void setupViews() {
        etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getBaseContext(), "Network unavailable, try later!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent result2detail = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                result2detail.putExtra("result", result);
                startActivity(result2detail);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    private void fetchSearchResults(String searchText, int nextSearchOffset) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network unavailable, try later!", Toast.LENGTH_LONG).show();
        }

        if (!searchText.equals(nextSearchText)) {
            resetSearchParams();
            aImageResults.clear();
            nextSearchText = searchText;
        } else if (maxSearchOffset != -1 && maxSearchOffset < nextSearchOffset) {
            Toast.makeText(this, "No more results!", Toast.LENGTH_SHORT).show();
            resetSearchParams();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("q", nextSearchText);
        params.put("rsz", offestIncrement);
        params.put("start", nextSearchOffset);
        apiClient.get(searchURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {
                    Log.i("cursor", response.getJSONObject("responseData").getJSONObject("cursor").toString());
                    JSONArray pages = response.getJSONObject("responseData").getJSONObject("cursor").getJSONArray("pages");
                    maxSearchOffset = Integer.valueOf(pages.getJSONObject(pages.length()-1).getString("start"));
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    aImageResults.notifyDataSetChanged();

                    //aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                Log.d("client failure", responseString + "           " + String.valueOf(statusCode) + "          " + headers.toString());
            }
        });
    }

    public void onImageSearch(View v) {
        String query = this.etQuery.getText().toString();
        if (query.equals("")) {
            Toast.makeText(this, "Please enter something to search", Toast.LENGTH_SHORT).show();
            return;
        }
        fetchSearchResults(query, 0);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void resetSearchParams() {
        nextSearchText = "";
        maxSearchOffset = -1;
    }
}
