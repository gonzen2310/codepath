package com.gonchi.parseinstagram.fragments;

import android.util.Log;

import com.gonchi.parseinstagram.Post;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {
  private static final String TAG = "ProfileFragment";

  @Override
  protected void queryPosts() {
    // Specify which class to query
    ParseQuery<Post> query = new ParseQuery<>(Post.class);
    query.include(Post.KEY_USER);
    query.setLimit(20);
    query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
    query.addDescendingOrder(Post.KEY_CRETED_AT);
    // Specify the object id
    query.findInBackground(new FindCallback<Post>() {
      @Override
      public void done(List<Post> posts, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Issue with posts" + e);
          e.printStackTrace();
          Snackbar.make(viewContainer, "Oosp! There was an error with you query", Snackbar.LENGTH_LONG).show();
          return;
        }
        mPosts.addAll(posts);
        adapter.clear();
        adapter.addAll(posts);
        swipeContainer.setRefreshing(false);
        for (int i = 0; i < posts.size(); i++) {
          Post post = posts.get(i);
          Log.d(TAG, "POST: " + post.getDescription() + " USERNAME: " + post.getUser().getUsername());
        }
      }
    });
  }
}
