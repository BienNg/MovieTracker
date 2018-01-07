package com.example.bien_pc.movielist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bien_pc.movielist.adapters.ViewpagerAdapter;
import com.example.bien_pc.movielist.controller.MovieDBController;
import com.example.bien_pc.movielist.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieActivity extends AppCompatActivity {

    //Attributes
    private static final String TAG = "MovieActivity";
    private static Activity activity;
    private static Context context;
    private FirebaseAuth mAuth;

    //Views
    private ViewPager viewPager;
    private ViewpagerAdapter viewpagerAdapter;
    private static TextView textReleaseYear, textGenres, textDescription, textRating;
    private static ImageView imageRating, imagePoster;
    ImageButton bttnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up the Add Button
        bttnAdd = (ImageButton) findViewById(R.id.mv_bttn_add);
        setUpAddButton();

        activity = this;
        context = this;

        mAuth = FirebaseAuth.getInstance();

        // Getting the movie id and title
        int id = getIntent().getIntExtra("ID", 0);

        // Setting up ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager_movie_images);
        viewpagerAdapter = new ViewpagerAdapter(this, id);
        viewPager.setAdapter(viewpagerAdapter);

        //Init. Views
        textReleaseYear = (TextView) findViewById(R.id.mv_text_release_year);
        textGenres = (TextView) findViewById(R.id.mv_text_genres);
        textDescription = (TextView) findViewById(R.id.mv_text_description);
        textRating = (TextView) findViewById(R.id.mv_text_rating);
        imageRating = (ImageView) findViewById(R.id.mv_image_rating);
        imagePoster= (ImageView) findViewById(R.id.mv_image_poster);

        // MovieDBController gets the movie object via its id and updates the ui
        MovieDBController movieDBController = new MovieDBController(this);
        movieDBController.getMovieById(id);
    }

    /**
     * Updates the views of the activity.
     * @param movie
     */
    public static void updateUI(final Movie movie){
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

        // Setting poster image
        Picasso.with(context).load(movie.getPosterPath()).into(imagePoster, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: poster is set.");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: image url ::: " + movie.getPosterPath());
                Log.d(TAG, "onError: poster not set. Some error idk.");
            }
        });

        // Setting rating
        textRating.setText(movie.getRating());

        // Setting the descripton
        textDescription.setText(movie.getDescription());
    }

    /**
     * Sets up the behaviour and appearance of the add button in the Toolbar:
     * - Clicking on Add should add the movie id to the firebase database.
     * - Clicking on Seen should remove the movie id from the firebase database.
     */
    private void setUpAddButton(){
        bttnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + bttnAdd.getTag());
                if(bttnAdd.getTag().equals("add")){
                    // Check if user is logged in
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser != null){
                        bttnAdd.setImageResource(R.drawable.ic_visibility);
                        bttnAdd.setTag("seen");
                    }else{
                        Intent intent = new Intent(MovieActivity.context, SignIn.class);
                        startActivity(intent);
                    }
                }else{
                    bttnAdd.setImageResource(R.drawable.ic_add);
                    bttnAdd.setTag("add");
                }
            }
        });
    }
}
