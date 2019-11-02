package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.TweetModel;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
  Context context;
  List<TweetModel> tweets;

  // Pass in the context and list of tweets
  public  TweetsAdapter(Context context, List<TweetModel> tweets) {
    this.context= context;
    this.tweets = tweets;
  }

  // For each row, inflate a layout
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
    return new ViewHolder(view);
  }

  // Bind values based on the position of the element
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Get the data at position
    TweetModel tweet = tweets.get(position);
    // Bind the tweet at position
    holder.bind(tweet);
  }

  public void clear() {
    tweets.clear();
    notifyDataSetChanged();
  }

  public void addAll(List<TweetModel> tweetList) {
    tweets.addAll(tweetList);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return tweets.size();
  }

  // Define view holder
  public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
      tvBody = itemView.findViewById(R.id.tvBody);
      tvScreenName = itemView.findViewById(R.id.tvScreenName);

    }

    public void bind(TweetModel tweet) {
      tvBody.setText(tweet.body);
      tvScreenName.setText(tweet.user.screenName);
      Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
    }
  }
}
