package com.gonchi.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {
    private String backdropPath;
    private String posterPath;
    private String title;
    private String overview;
    private double voteAverage;

    public  Movie() {}
    public Movie(JSONObject jsonObject) throws JSONException {
        this.voteAverage = jsonObject.getDouble("vote_average");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.posterPath = jsonObject.getString("poster_path");
        this.title = jsonObject.getString("title");
        this.overview = jsonObject.getString("overview");
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath ) ;
    }

    public String getTitle() {
        return title;
    }


    public String getOverview() {
        return overview;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath  ) ;
    }

    public double getVoteAverage() { return voteAverage; }


    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

}
