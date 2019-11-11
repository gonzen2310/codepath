package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

  public static final String TAG = "ComposeActivity";
  public static final int MAX_TWEET_LINE = 280;

  EditText etCompose;
  Button btnTweet;
  TwitterClient twitterClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_compose);

    twitterClient = TwitterApplication.getRestClient(this);

    etCompose = findViewById(R.id.etCompose);
    btnTweet = findViewById(R.id.btnTweet);

    etCompose.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > MAX_TWEET_LINE)
          Toast.makeText(ComposeActivity.this, "Max tweet length is 280 characters", Toast.LENGTH_LONG).show();

      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    // Add a click listener on button
    btnTweet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String tweetContent = etCompose.getText().toString();
        if (tweetContent.isEmpty()) {
          Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty!", Toast.LENGTH_LONG).show();
          return;
        }
        if (tweetContent.length() > MAX_TWEET_LINE) {
          Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_LONG).show();
          return;
        }
        Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

        // Make an API call to Twitter to publish the tweet
        twitterClient.publishTweet(tweetContent, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, "onSuccess to publish tweet");
            try {
              TweetModel tweet = TweetModel.fromJson(json.jsonObject);
              Log.i(TAG, "Published tweet says: " + tweet);
              Intent intent = new Intent();
              intent.putExtra("tweet", Parcels.wrap(tweet));
              setResult(RESULT_OK, intent);
              finish();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

          @Override
          public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, "onFailure to publish tweet", throwable);
          }
        });
      }
    });
  }
}
