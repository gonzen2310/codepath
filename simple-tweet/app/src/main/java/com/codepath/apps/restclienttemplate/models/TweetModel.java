package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class TweetModel {
  public String body;
  public String createdAt;
  public UserModel user;
  public long id;
  public boolean verified;


  public TweetModel() {}

  public static TweetModel fromJson(JSONObject jsonObject) throws JSONException {
    TweetModel tweet = new TweetModel();
    tweet.body = jsonObject.getString("text");
    tweet.createdAt = jsonObject.getString("created_at");
    tweet.id = jsonObject.getLong("id");
    tweet.user = UserModel.fromJson(jsonObject.getJSONObject("user"));
    return tweet;
  }

  public static List<TweetModel> fromJsonArray(JSONArray jsonArray) throws JSONException {
    List<TweetModel> tweets = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      tweets.add(fromJson(jsonArray.getJSONObject(i)));
    }
    return tweets;
  }
}
