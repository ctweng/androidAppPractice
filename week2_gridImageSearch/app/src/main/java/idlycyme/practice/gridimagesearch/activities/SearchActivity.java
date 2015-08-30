package idlycyme.practice.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
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
import idlycyme.practice.gridimagesearch.libraries.SearchFilterDialog;
import idlycyme.practice.gridimagesearch.libraries.SearchFilterDialog.SearchFilterDialogListener;
import idlycyme.practice.gridimagesearch.models.ImageResult;

import static idlycyme.practice.gridimagesearch.R.string.limit_search_filter_param;
import static idlycyme.practice.gridimagesearch.R.string.offset_search_filter_param;
import static idlycyme.practice.gridimagesearch.R.string.query_search_filter_param;

public class SearchActivity extends AppCompatActivity implements SearchFilterDialogListener {
    private StaggeredGridView gvResults;
    private AsyncHttpClient apiClient;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private String nextSearchText;
    private String searchFilterSite = "";
    private int searchFilterType = 0;
    private int searchFilterColor = 0;
    private int searchFilterSize = 0;
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
                fetchSearchResults(nextSearchText, (page - 1) * offestIncrement);
            }
        });
;
    }

    private void setupViews() {
        gvResults = (StaggeredGridView)findViewById(R.id.gvResults);
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                if (query.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter something to search", Toast.LENGTH_SHORT).show();
                    return true;
                }
                fetchSearchResults(query, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void fetchSearchResults(String searchText, int nextSearchOffset) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network unavailable, try later!", Toast.LENGTH_LONG).show();
        }

        if (!searchText.equals(nextSearchText) || nextSearchOffset == 0) {
            resetSearchParams();
            aImageResults.clear();
            nextSearchText = searchText;
        } else if (maxSearchOffset != -1 && maxSearchOffset < nextSearchOffset) {
            Toast.makeText(this, "No more results!", Toast.LENGTH_SHORT).show();
            resetSearchParams();
            return;
        }

        apiClient.get(searchURL, buildRequestParams(nextSearchOffset), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {
                    Log.i("cursor", response.getJSONObject("responseData").getJSONObject("cursor").toString());
                    JSONArray pages = response.getJSONObject("responseData").getJSONObject("cursor").getJSONArray("pages");
                    maxSearchOffset = Integer.valueOf(pages.getJSONObject(pages.length() - 1).getString("start"));
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

    public boolean onSetting(MenuItem item) {
        Log.i("in setting", "-------------");
        showFilterSettingDialog();
        return true;
    }

    private void showFilterSettingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchFilterDialog dialog = SearchFilterDialog.newInstance(getString(R.string.search_filter_dialog_title), searchFilterSite, searchFilterSize, searchFilterType, searchFilterColor);
        dialog.show(fm, "fragment_edit_name");
    }

    private RequestParams buildRequestParams(int nextSearchOffset) {
        RequestParams params = new RequestParams();
        params.put(getString(query_search_filter_param), nextSearchText);
        params.put(getString(offset_search_filter_param), nextSearchOffset);
        params.put(getString(limit_search_filter_param), offestIncrement);
        if (!searchFilterSite.equals("")) {
            params.put("as_sitesearch", searchFilterSite);
        }
        if (searchFilterSize != 0) {
            params.put("imgsz", getResources().getStringArray(R.array.filter_size_array)[searchFilterSize]);
        }
        if (searchFilterColor != 0) {
            params.put("imgcolor", getResources().getStringArray(R.array.filter_color_array)[searchFilterColor]);
        }
        if (searchFilterType != 0) {
            params.put("imgtype", getResources().getStringArray(R.array.filter_type_array)[searchFilterType]);
        }
        Log.i("params", params.toString());
        return params;
    }

    @Override
    public void onFinishEditDialog(String site, int size, int type, int color) {
        searchFilterSite = site;
        searchFilterType = type;
        searchFilterSize = size;
        searchFilterColor = color;
        fetchSearchResults(nextSearchText, 0);
    }
}
