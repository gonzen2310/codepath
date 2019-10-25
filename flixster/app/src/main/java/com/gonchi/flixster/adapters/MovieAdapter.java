package com.gonchi.flixster.adapters;

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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.moviesFull = new ArrayList<>(movies);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl = (context.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) ? movie.getBackdropPath() :
                    movie.getPosterPath();

            // 1. Register click listener on the whole row
            // 2. Navigate on the activity on tap
            Log.d("MainActivity", Double.toString(movie.getVoteAverage()));
            Glide.with(context).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder_img)).into(ivPoster);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(intent);
                    Toast.makeText(context, movie.getTitle(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
