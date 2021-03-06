package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class UserModel {

  public String name;
  public String screenName;
  public String profileImageUrl;
  public boolean verified;

  public UserModel() {}

  public static UserModel fromJson(JSONObject jsonObject) throws JSONException {
    UserModel user = new UserModel();
    user.name = jsonObject.getString("name");
    user.screenName = jsonObject.getString("screen_name");
    user.profileImageUrl = jsonObject.getString("profile_image_url_https");
    user.verified = jsonObject.getBoolean("verified");


    return user;
  }
}
