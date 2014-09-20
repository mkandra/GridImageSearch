package com.mbkandra16.apps.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mbkandra16.apps.fragments.SearchFiltersDialog;
import com.mbkandra16.apps.gridimagesearch.R;
import com.mbkandra16.apps.gridimagesearch.adapters.EndlessScrollListener;
import com.mbkandra16.apps.gridimagesearch.adapters.ImageResultsAdapter;
import com.mbkandra16.apps.gridimagesearch.models.ImageResult;
import com.mbkandra16.apps.gridimagesearch.models.SearchFilters;

public class SearchActivity extends FragmentActivity implements
		SearchFiltersDialog.SearchFiltersDialogListener {
	public static final int SEARCH_FILTERS_ACTIVITY_REQUEST_CODE = 50;
	public static final int RSZ_PER_PAGE = 8;

	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	private SearchFilters filters;
	private SearchView searchView;
	private String queryString;
	private int currPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();

		filters = null;
		queryString = null;
		// Creates the data source
		imageResults = new ArrayList<ImageResult>();
		// Attaches the data source to an adapter
		aImageResults = new ImageResultsAdapter(this, imageResults);
		// Link the adapter to the adaterView (gridView)
		gvResults.setAdapter(aImageResults);

	}

	// Append more data into the adapter
	public void customLoadMoreDataFromApi(int offset) {
		// This method probably sends out a network request and appends new data
		// items to your adapter.
		// Use the offset value and add it as a parameter to your API request to
		// retrieve paginated data.
		// Deserialize API response and then construct new objects to append to
		// the adapter
		doImageSearch(offset);
	}

	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Launch the image display activity
				// Create an intent
				Intent i = new Intent(SearchActivity.this,
						ImageDisplayActivity.class);
				// Get the image result to display
				ImageResult result = imageResults.get(arg2);
				// Pass image result into the intent
				i.putExtra("result", result); // need to either be serializable
												// or parcelable
				// launch the new activity
				startActivity(i);
			}

		});
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				customLoadMoreDataFromApi(totalItemsCount);
				// or customLoadMoreDataFromApi(totalItemsCount);
			}
		});
	}

	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	private void doImageSearch(int page) {

		if (!isNetworkAvailable()) {
			Toast.makeText(SearchActivity.this, "No internet connection",
					Toast.LENGTH_SHORT).show();
			return;
		}

		currPage = page;

		// Log.d("DEBUG", "query string is " + queryString);
		if (queryString == null || queryString.isEmpty())
			return;

		// Toast.makeText(this, "Search for: " + query,
		// Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
		String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
				+ queryString + "&rsz=" + RSZ_PER_PAGE + "&start=" + page;
		if (filters != null) {
			searchUrl += "&imgcolor=" + filters.colorFilter + "&imgsz="
					+ filters.imageSize + "&imgtype=" + filters.imageType
					+ "&as_sitesearch=" + filters.siteFilter;

			// Toast.makeText(this, searchUrl, Toast.LENGTH_SHORT).show();
		}
		// Log.d("INFO", "searchUrl = " + searchUrl);
		client.get(searchUrl, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.d("DEBUG", response.toString());
				JSONArray imageResultsJson = null;
				try {
					imageResultsJson = response.getJSONObject("responseData")
							.getJSONArray("results");
					if (currPage == 0)
						aImageResults.clear(); // clear the existing images from
												// the
												// array (in cases where its a
												// new
												// search)

					// imageResults.addAll(ImageResult
					// .fromJSONArray(imageResultsJson));
					// aImageResults.notifyDataSetChanged();

					// When you make to the adapter, it does modify the
					// underlying data

					aImageResults.addAll(ImageResult
							.fromJSONArray(imageResultsJson));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		MenuItem actionSearch = menu.findItem(R.id.action_search);
		searchView = (SearchView) actionSearch.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// InputMethodManager imm = (InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				// perform query here
				queryString = query;
				searchView.clearFocus();
				doImageSearch(0);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return true;
	}

	public void onSearchFiltersAction(MenuItem mi) {
		// // Create the intent
		// Intent i = new Intent(this, SearchFiltersActivity.class);
		//
		// if (filters != null)
		// i.putExtra("currentFilters", filters);
		// else {
		// SearchFilters defaultFilters = new SearchFilters();
		// defaultFilters.colorFilter = "black";
		// defaultFilters.imageSize = "small";
		// defaultFilters.imageType = "face";
		// defaultFilters.siteFilter = "";
		// i.putExtra("currentFilters", defaultFilters);
		// }
		//
		// // Execute the intent
		// startActivityForResult(i, SEARCH_FILTERS_ACTIVITY_REQUEST_CODE);

		if (filters == null) {
			filters = new SearchFilters();
			filters.colorFilter = "black";
			filters.imageSize = "small";
			filters.imageType = "face";
			filters.siteFilter = "";
		}
		
		FragmentManager fm = getSupportFragmentManager();
		SearchFiltersDialog editNameDialog = SearchFiltersDialog.newInstance(
				"Search Filters", filters);
		editNameDialog.show(fm, "Search Filters Dialog");

	}

	@Override
	public void onFinishFiltersDialog(SearchFilters filter) {
		filters = filter;
		doImageSearch(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEARCH_FILTERS_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Success
				filters = (SearchFilters) data.getSerializableExtra("filters");
				// Toast.makeText(this, filters.colorFilter, Toast.LENGTH_SHORT)
				// .show();
				doImageSearch(0);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
