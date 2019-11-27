package com.gonchi.parseinstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gonchi.parseinstagram.Post;
import com.gonchi.parseinstagram.PostsAdapter;
import com.gonchi.parseinstagram.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

  private static final String TAG = "PostsFragment";
  RecyclerView recyclerView;
  ConstraintLayout viewContainer;
  SwipeRefreshLayout swipeContainer;

  protected PostsAdapter adapter;
  protected List<Post> mPosts;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_posts, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    recyclerView = view.findViewById(R.id.rvPosts);
    swipeContainer = view.findViewById(R.id.swipeContainer);
    viewContainer = view.findViewById(R.id.mainContainer);
    mPosts = new ArrayList<>();
    adapter = new PostsAdapter(getContext(), mPosts);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    queryPosts();
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        queryPosts();
      }
    });
    // Configure the refreshing colors
    swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
  }

  protected void queryPosts() {
    // Specify which class to query
    ParseQuery<Post> query = new ParseQuery<>(Post.class);
    query.include(Post.KEY_USER);
    query.setLimit(20);
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
