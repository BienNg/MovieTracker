package com.example.bien_pc.movielist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.bien_pc.movielist.adapters.ViewpagerAdapter;
import com.example.bien_pc.movielist.controller.MovieDBController;
import com.example.bien_pc.movielist.models.Movie;

public class MovieActivity extends AppCompatActivity {

    //Attributes
    private static final String TAG = "MovieActivity";
    private static Activity activity;

    //Views
    private ViewPager viewPager;
    private ViewpagerAdapter viewpagerAdapter;
    private static TextView textReleaseYear, textGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = this;

        // Getting the movie id and title
        int id = getIntent().getIntExtra("ID", 0);

        // Setting up ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager_movie_images);
        viewpagerAdapter = new ViewpagerAdapter(this, id);
        viewPager.setAdapter(viewpagerAdapter);

        //Init. Views
        textReleaseYear = (TextView) findViewById(R.id.mv_text_release_year);
        textGenres = (TextView) findViewById(R.id.mv_text_genres);

        // MovieDBController gets the movie object via its id and updates the ui
        MovieDBController movieDBController = new MovieDBController(this);
        movieDBController.getMovieById(id);
    }

    public static void updateUI(Movie movie){
        // Set the title of the activity
        activity.setTitle(movie.getTitle());
        // Set the release date of the movie
        textReleaseYear.setText(movie.getYear());

        //Set the genres of the movie
        String genres = "";
        for(String genre : movie.getGenres()){
            if(genres.equals("")){
                genres = genre;
            }else{
                genres = genres + ", " + genre;
            }
        }
        textGenres.setText(genres);
    }
}
