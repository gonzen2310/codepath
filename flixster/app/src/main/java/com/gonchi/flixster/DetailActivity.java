package com.gonchi.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gonchi.flixster.models.Movie;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

  TextView tvTitle;
  TextView tvOverview;
  RatingBar ratingBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    tvTitle = findViewById(R.id.tvTitle);
    tvOverview = findViewById(R.id.tvOverview);
    ratingBar  = findViewById(R.id.ratingBar);
    Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
    tvTitle.setText(movie.getTitle());
    ratingBar.setRating((float)movie.getVoteAverage());
    tvOverview.setText(movie.getOverview());
  }
}
