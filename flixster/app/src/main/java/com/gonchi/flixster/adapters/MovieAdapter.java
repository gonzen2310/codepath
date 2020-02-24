package com.gonchi.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gonchi.flixster.DetailActivity;
import com.gonchi.flixster.R;
import com.gonchi.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>  {

    Context context;
    List<Movie> movies;
    List<Movie> moviesFull;
    Activity activity;

    public MovieAdapter(Context context, List<Movie> movies, Activity activity) {
        this.context = context;
        this.movies = movies;
        this.moviesFull = new ArrayList<>(movies);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
       holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;
        ImageView ivPlay;
        ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
            ivPlay = itemView.findViewById(R.id.ivPlay);
        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl = (context.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) ? movie.getBackdropPath() :
                    movie.getPosterPath();
            if(movie.getVoteAverage() < 5) ivPlay.setVisibility(View.GONE);

            // 1. Register click listener on the whole row
            // 2. Navigate on the activity on tap
            Log.d("MainActivity", Double.toString(movie.getVoteAverage()));
            Glide.with(context).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder_img).transforms(new CenterCrop(), new RoundedCorners(24))).into(ivPoster);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("movie", Parcels.wrap(movie));
                    ActivityOptionsCompat title = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, tvTitle, "title");
                    context.startActivity(intent, title.toBundle());
                    Toast.makeText(context, movie.getTitle(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}
