package com.example.bien_pc.movielist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.bien_pc.movielist.controller.MovieDBController;
import com.example.bien_pc.movielist.models.Movie;

public class MovieActivity extends AppCompatActivity {

    //Attributes
    private static final String TAG = "MovieActivity";
    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = this;

        // Getting the movie id and title
        int id = getIntent().getIntExtra("ID", 0);

        MovieDBController movieDBController = new MovieDBController(this);
        movieDBController.getMovieById(id);
    }

    public static void updateUI(Movie movie){
        activity.setTitle(movie.getTitle());
    }

}
