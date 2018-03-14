package com.example.bien_pc.movielist.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.features.WatchNow;
import com.example.bien_pc.movielist.adapters.ActorsAdapter;
import com.example.bien_pc.movielist.adapters.MoviesAdapter;
import com.example.bien_pc.movielist.adapters.ViewpagerAdapter;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.models.Actor;
import com.example.bien_pc.movielist.models.Movie;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    //Variables
    private static final String TAG = "MovieActivity";
    private static Activity activity;
    private static Context context;
    private int id;
    private static String title;
    private MyFirebaseUser myFirebaseUser;

    //Views
    private static ViewPager viewPager;
    private static ViewpagerAdapter viewpagerAdapter;
    private TextView textReleaseYear, textGenres, textDescription, textRating;
    private ImageView imagePoster, bttnFavorite, bttnWatch;
    private static ImageButton bttnAdd;
    private static LinearLayout layoutRating;
    private CardView cardViewRelatedMovies;

    // Variables for the collection RecyclerViews
    private static MoviesAdapter adapterRelatedMovies;
    private static ActorsAdapter adapterActors;
    private static RecyclerView rvRelatedMovies, rvCast;
    private ArrayList<Actor> listActors = new ArrayList<>();


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Getting Firabase Instance
        myFirebaseUser = new MyFirebaseUser();

        // Getting the movie
        id = getIntent().getIntExtra("ID", 0);

        activity = this;
        context = this;

        //Init. Views
        textReleaseYear = (TextView) findViewById(R.id.mv_text_release_year);
        textGenres = (TextView) findViewById(R.id.mv_text_genres);
        textDescription = (TextView) findViewById(R.id.mv_text_description);
        textRating = (TextView) findViewById(R.id.mv_text_rating);
        imagePoster = (ImageView) findViewById(R.id.mv_image_poster);
        rvRelatedMovies = (RecyclerView) findViewById(R.id.mv_rv_related_movies);
        rvCast = (RecyclerView) findViewById(R.id.mv_rv_cast);
        layoutRating = (LinearLayout) findViewById(R.id.mv_layout_rating);
        cardViewRelatedMovies = (CardView) findViewById(R.id.mv_cardview_related_movies);
        bttnFavorite = (ImageView) findViewById(R.id.mv_bttn_fav);
        bttnWatch = (ImageView) findViewById(R.id.mv_bttn_watch);
        bttnAdd = (ImageButton) findViewById(R.id.mv_bttn_add);

        // Setting up Buttons
        setUpAddButton();
        setUpFavButton();
        setUpWatchButton();

        // Setting up ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager_movie_images);
        viewpagerAdapter = new ViewpagerAdapter(this, id);
        viewPager.setAdapter(viewpagerAdapter);

        getMovieInformationForUi();
    }

    /**
     * Gets the movie information to update the ui
     */
    private void getMovieInformationForUi() {
        MDBUrls mdbUrls = new MDBUrls();
        String movieUrl = mdbUrls.getURL() + "/movie/" + id + mdbUrls.getAPI_KEY();
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        JsonParser jsonParser = new JsonParser(result);
                        Movie movie = jsonParser.getMovie();
                        updateUI(movie);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    /**
     * Updates the views of the activity.
     *
     * @param movie
     */
    private void updateUI(final Movie movie) {
        // Set the title of the activity
        title = movie.getTitle();
        activity.setTitle(movie.getTitle());

        // Set the release date of the movie
        textReleaseYear.setText(movie.getYear());

        //Set the genres of the movie
        String genres = "";
        for (String genre : movie.getGenres()) {
            if (genres.equals("")) {
                genres = genre;
            } else {
                genres = genres + ", " + genre;
            }
        }
        textGenres.setText(genres);

        // Setting poster image if there is a poster url
        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
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
        }

        // Setting rating
        textRating.setText(movie.getRating());

        // Setting the descripton
        textDescription.setText(movie.getDescription());

        // Setting up the related movies recycler view
        if (movie.getCollectionId() != 0) {
            cardViewRelatedMovies.setVisibility(View.VISIBLE);
            MDBUrls controller = new MDBUrls();
            controller.getCollection(movie.getId(), movie.getCollectionId());
        } else {
            cardViewRelatedMovies.setVisibility(View.GONE);
        }

        // Setting up the actors recycler view
        setupRecyclerViewActors();
    }

    /**
     * Sets up the Fav button
     */
    private void setUpFavButton() {
        if (myFirebaseUser.getFirebaseUser() != null) {
            // Updating UI
            myFirebaseUser.getAllMoviesReference()
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(id + "")
                            && snapshot.child(id+"").hasChild("favorite")
                            && snapshot.child(id+"").child("favorite").getValue().equals("true")) {
                        bttnFavorite.setImageResource(R.drawable.ic_favorite_full);
                        bttnFavorite.setTag("is_fav");
                    }else{
                        bttnFavorite.setImageResource(R.drawable.ic_favorite_empty);
                        bttnFavorite.setTag("not_fav");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // Setup listener for the favButton
            bttnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bttnFavorite.getTag().equals("not_seen")){
                        // Update UI
                        bttnFavorite.setImageResource(R.drawable.ic_favorite_full);
                        bttnFavorite.setTag("seen");
                        bttnAdd.setImageResource(R.drawable.ic_seen);
                        bttnAdd.setTag("seen");

                        myFirebaseUser.favMovie(id, title);

                    }else{
                        bttnFavorite.setImageResource(R.drawable.ic_favorite_empty);
                        bttnFavorite.setTag("not_seen");
                        // Delete fav
                        myFirebaseUser.unfavMovie(id);
                    }
                }
            });
        }else{
            bttnFavorite.setImageResource(R.drawable.ic_favorite_empty);
            bttnFavorite.setTag("not_fav");
        }
    }

    /**
     * Sets up the behaviour and appearance of the add button in the Toolbar:
     * - Clicking on Add should add the movie id to the firebase database.
     * - Clicking on Seen should remove the movie id from the firebase database.
     */
    private void setUpAddButton() {
        // Check if user is logged in
        if(myFirebaseUser.getFirebaseUser() != null) {

                // Update UI
                myFirebaseUser.getAllMoviesReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(id + "")) {
                            bttnAdd.setImageResource(R.drawable.ic_seen);
                            bttnAdd.setTag("seen");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            // Bttn adds movie or removes movie from database when clicked
            bttnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Add Button is clicked.");

                    if (bttnAdd.getTag().equals("add")) {
                        if (myFirebaseUser.getFirebaseUser() != null) {

                            // Update UI
                            bttnAdd.setImageResource(R.drawable.ic_seen);
                            bttnAdd.setTag("seen");

                            // Add a id to the database
                            myFirebaseUser.addMovieToSeen(id, title);

                        }else {
                            // If usre is not logged in, SignIn Activity is called.
                            Intent intent = new Intent(MovieActivity.context, SignInActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        bttnAdd.setImageResource(R.drawable.ic_add);
                        bttnAdd.setTag("add");
                        bttnFavorite.setImageResource(R.drawable.ic_favorite_empty);
                        bttnFavorite.setTag("not_fav");

                        // Delete id from database
                        myFirebaseUser.removeMovieFromSeen(id);

                    }
                }
            });
        }else{
            Intent intent = new Intent(MovieActivity.this, SignInActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Gets list of related movies and updates the related movies recycler view
     *
     * @param list
     */
    public static void updateRelatedMoviesRV(ArrayList<Movie> list) {

        // Setting up the Recycler View of the related Movies
        rvRelatedMovies.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvRelatedMovies.setLayoutManager(llm);
        adapterRelatedMovies = new MoviesAdapter(context, list);
        rvRelatedMovies.setAdapter(adapterRelatedMovies);
    }


    /**
     * Sets up the Actors Recycler View.
     * First: gets all the actors.
     * Second: updates the recyclerview.
     */
    private void setupRecyclerViewActors() {
        // Getting URL of the actors
        MDBUrls mdbUrls = new MDBUrls();
        String actorsUrl = mdbUrls.generateCastListUrl(id);
        Log.d(TAG, "setupRecyclerViewActors: actorsUrl ::: " + actorsUrl);
        // Getting list of actors
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, actorsUrl, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        JsonParser jsonParser = new JsonParser(result);
                        listActors = jsonParser.getActors();
                        initRecyclerViewActors();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    private void initRecyclerViewActors() {
        rvCast.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(linearLayoutManager);
        adapterActors = new ActorsAdapter(context, listActors);
        rvCast.setAdapter(adapterActors);
    }

    public static void updateVisibility() {

        // Set Visibility of the views if the information is not given
        if (viewpagerAdapter.getCount() == 0) {
            viewPager.setVisibility(View.GONE);
            bttnAdd.setVisibility(View.GONE);
            layoutRating.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            bttnAdd.setVisibility(View.VISIBLE);
            layoutRating.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up watch now button
     */
    private void setUpWatchButton(){
        bttnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: watch now button clicked.");
                new WatchNow(id).execute();
            }
        });
    }
}
