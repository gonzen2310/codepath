package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

  public static final String TAG = "TimelineActivity";
  public final int REQUEST_CODE = 20;

  TwitterClient client;
  RecyclerView rvTweets;
  List<TweetModel> tweets;
  TweetsAdapter tweetsAdapter;
  SwipeRefreshLayout swipeRefreshLayout;
  EndlessRecyclerViewScrollListener scrollListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);

    client = TwitterApplication.getRestClient(this);

    swipeRefreshLayout = findViewById(R.id.swipeContainer);
    // Configure the refreshing colors
    swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        Log.i(TAG, "fetching new data");
        populateTimeLine();
      }
    });
    // Find the recycler view
    rvTweets = findViewById(R.id.rvTweets);
    // Initialize the list of tweets and the adapter
    tweets = new ArrayList<>();
    tweetsAdapter = new TweetsAdapter(this, tweets);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    // Recycler view setup: layout manager and the adapter
    rvTweets.setLayoutManager(layoutManager);
    rvTweets.setAdapter(tweetsAdapter);

    scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        Log.i(TAG, "onLoadMore: " + page);
        loadMoreData();
      }
    };
    // Adds the scroll listener to RecyclerView
    rvTweets.addOnScrollListener(scrollListener);

    populateTimeLine();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle presses on the action bar items
    switch (item.getItemId()) {
      case R.id.compose:
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
//        composeMessage();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      // Get data from intent
      TweetModel tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
      // Update recycle view
      // Modify data source
      tweets.add(0, tweet);
      // Update adapter
      tweetsAdapter.notifyItemInserted(0);
      rvTweets.smoothScrollToPosition(0);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void loadMoreData() {
    // 1. Send an API request to retrieve appropriate paginated data
    client.getNextPageOfTweets(new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.i(TAG, "onSuccess for loadMoreData! " + json.toString());
        // 2. Deserialize and construct new model objects from the API response
        JSONArray jsonArray = json.jsonArray;
        try {

          List<TweetModel> tweets = TweetModel.fromJsonArray(jsonArray);
          // 3. Append the new data objects to the existing set of items inside the array of items
          tweetsAdapter.addAll(tweets);
          // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      @Override
      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.e(TAG, "onFailure for loadMoreData! " + throwable);
      }
    }, tweets.get(tweets.size()-1).id);

  }

  private void populateTimeLine() {
      client.getHomeTimeline(new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
          Log.i(TAG, "onSuccess: "+ json.toString());
          JSONArray jsonArray = json.jsonArray;
          try {
            tweetsAdapter.clear();
            tweetsAdapter.addAll(TweetModel.fromJsonArray(jsonArray));
            swipeRefreshLayout.setRefreshing(false);
          } catch (JSONException e) {
            Log.e(TAG,  "Json Exception", e);
          }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
          Log.i(TAG, "onFailure: " + response, throwable);
        }
      });
  }
}
