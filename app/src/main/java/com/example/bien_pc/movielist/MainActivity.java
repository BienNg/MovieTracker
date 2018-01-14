package com.example.bien_pc.movielist;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.adapters.ActorsAdapter;
import com.example.bien_pc.movielist.fragments.FragmentHome;
import com.example.bien_pc.movielist.fragments.FragmentMyMovies;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.helper.OnSwipeTouchListener;
import com.example.bien_pc.movielist.models.Actor;
import com.example.bien_pc.movielist.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener {

    private final String TAG = "MainActivity";
    // Popup Dialog of the user
    private Dialog dialogUserPopup;
    // Firebase user
    private FirebaseAuth mAuth;
    private String userEmail;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentHome fragmentHome = new FragmentHome();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flContent, fragmentHome, "FragmentName");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentMyMovies fragmentMyMovies = new FragmentMyMovies();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.flContent, fragmentMyMovies, "FragmentName");
                    fragmentTransaction2.commit();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the Navigation View
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Start Home Fragment when starting the app
        FragmentHome fragmentHome = new FragmentHome();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragmentHome, "FragmentName");
        fragmentTransaction.commit();

        // Init. Dialog Popup of the user
        dialogUserPopup = new Dialog(this);
    }


    /**
     * Inflates the menu xml
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    /**
     * Click Listener for the menu items.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_user:
                mAuth = FirebaseAuth.getInstance();
                if (mAuth == null) {
                    Intent intent = new Intent(this, SignIn.class);
                    startActivity(intent);
                } else {
                    showPopup();
                }
                return true;
            case R.id.menuitem_lightning:
                mAuth = FirebaseAuth.getInstance();
                userEmail = mAuth.getCurrentUser().getEmail().replace(".", "(dot)");
                startLightningFeature();
                getMarkedMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Shows the pop up window of the user
    private void showPopup() {
        dialogUserPopup.setContentView(R.layout.popup_user);
        dialogUserPopup.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }



    // [Lightning Feature] Variables.
    private ArrayList<Movie> randomMovies = new ArrayList<>();
    private ArrayList<String> markedMovies = new ArrayList<>();
    private View view;
    private int pageCounter = 1;

    /**
     * [LightningFeature]
     * Opens the dialog to swipe through movies.
     */
    private void startLightningFeature() {
        if (randomMovies.size() < 5) {
            // TODO ADD NEW MOVIES
        }
        final Dialog dialog = new Dialog(MainActivity.this);
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_movie, null);
        view.setVisibility(View.INVISIBLE);
        setUpView(dialog);

        // Start the dialog window
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * 1. Gets every movie that is stored in the firebase database of the user.
     * 2. getLightningMovies()
     */
    private void getMarkedMovies(){
        final ArrayList<Integer> firebaseCounter = new ArrayList<>();

        // -1- Getting seen movies from current user.
        DatabaseReference databaseReferenceMovies = FirebaseDatabase.getInstance().getReference(userEmail).child("movies");
        // Getting his movies.
        databaseReferenceMovies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String id = d.getKey();
                    markedMovies.add(id);
                    firebaseCounter.add(1);
                    if(firebaseCounter.size() == 3){
                        getLightningMovies();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // -2- Getting swiped movies from current user
        DatabaseReference databaseReferenceSwipedMovies = FirebaseDatabase.getInstance().getReference(userEmail).child("swiped");
        // Getting his movies.
        databaseReferenceSwipedMovies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String id = d.getKey();
                    markedMovies.add(id);
                    firebaseCounter.add(1);
                    if(firebaseCounter.size() == 3){
                        getLightningMovies();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // Getting watchlist movies from current user
        DatabaseReference databaseReferenceWatchlist = FirebaseDatabase.getInstance().getReference(userEmail).child("watchlist");
        // Getting his movies.
        databaseReferenceWatchlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String id = d.getKey();
                    markedMovies.add(id);
                    firebaseCounter.add(1);
                    if(firebaseCounter.size() == 3){
                        getLightningMovies();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * [LightningFeature]
     * Getting the movies that are shown in the Lightning Dialog.
     * Only show movies that are not in the users list yet.
     * 1. Get Random movies from the database.
     * 2. Remove seen movies from those random movies.
     * 3. Updated dialog UI
     */
    private void getLightningMovies() {

        // -1- Generating the URL for the database.
        final MDBUrls mdbUrls = new MDBUrls();
        String urlPopularMovies = mdbUrls.generatePopularMoviesUrlWithPage(pageCounter);
        Log.d(TAG, "getLightningMovies: urlPopularMovies ::: " + urlPopularMovies);

        // Getting Popular Movies
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlPopularMovies, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resultPopularMovies = response.toString();
                        JsonParser jsonParserPopularMovies = new JsonParser(resultPopularMovies);
                        randomMovies = jsonParserPopularMovies.getList();

                        // Getting Top rated movies
                        String urlTopRatedMovies = mdbUrls.generateTopRatedMoviesUrlWithPage(pageCounter);
                        Log.d(TAG, "getLightningMovies: urlTopRatedMovies ::: " + urlTopRatedMovies);
                        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlTopRatedMovies, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String resultTopRatedMovies = response.toString();
                                JsonParser jsonParserTopRatedMovies = new JsonParser(resultTopRatedMovies);
                                ArrayList<Movie> list = jsonParserTopRatedMovies.getList();
                                randomMovies.addAll(list);
                                Log.d(TAG, "getLightningMovies: getting top rated list done.");


                                // -2- Removing Seen Movies from random movie list
                                for (String seenMovie : markedMovies) {
                                    for (int i = 0; i < randomMovies.size(); i++) {
                                        if ((randomMovies.get(i).getId() + "").equals(seenMovie)) {
                                            randomMovies.remove(i);
                                        }
                                    }
                                }

                                for(Movie movie : randomMovies){
                                    Log.d(TAG, "full list of random movies ::: " + movie.getTitle());
                                }

                                // If more than 10 Random movies are generated the UI should be uptdated with the first movie
                                if(randomMovies.size() > 15){
                                    // -3- Update Dialog UI
                                    updateDialogUI();
                                }else{
                                    // Repeat getting movies with next page
                                    pageCounter++;
                                    getMarkedMovies();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        // Access the RequestQueue through your singleton class.
                        MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsObjRequest);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsObjRequest);
    }

    /**
     * Update the lightning ui
     */
    private void updateDialogUI() {
        final Movie randomMovie = randomMovies.get(0);
        // Init. Views
        ImageView imageMovie = (ImageView) view.findViewById(R.id.dialog_lightning_movie_image);
        TextView txtRating = (TextView) view.findViewById(R.id.dialog_lightning_rating);
        TextView txtRelease = (TextView) view.findViewById(R.id.dialog_lightning_txt_release);

        // Setting up views
        txtRating.setText(randomMovie.getRating());
        txtRelease.setText(randomMovie.getYear());

        // Setting Movie Image
        Picasso.with(MainActivity.this).load(randomMovie.getPosterPath()).into(imageMovie, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: poster is set.");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: image url ::: " + randomMovie.getPosterPath());
                Log.d(TAG, "onError: poster not set. Some error idk.");
            }
        });
        imageMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                intent.putExtra("ID", randomMovie.getId());
                startActivity(intent);
            }
        });

        MDBUrls mdbUrls = new MDBUrls();
        String urlCast = mdbUrls.generateCastListUrl(randomMovie.getId());
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlCast, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        JsonParser jsonParser = new JsonParser(result);
                        ArrayList<Actor> actors = jsonParser.getActors();
                        updateRecyclerViewCast(actors);
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
     * Updates the RecyclerView
     *
     * @param actors
     */
    private void updateRecyclerViewCast(ArrayList<Actor> actors) {
        RecyclerView rvCast = (RecyclerView) view.findViewById(R.id.dialog_lightning_rv_cast);
        rvCast.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(linearLayoutManager);
        ActorsAdapter adapterActors = new ActorsAdapter(MainActivity.this, actors);
        rvCast.setAdapter(adapterActors);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Sets Up the feedback after swiping the dialog away.
     * @param dialog
     */
    private void setUpView(final Dialog dialog) {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.animate().scaleY(0).scaleX(0);
        view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

            public void onSwipeTop() {
                view.animate().translationY(-5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                fab.setImageResource(R.drawable.ic_add);
                fab.setVisibility(View.VISIBLE);
                fab.animate()
                        .scaleYBy(1.2f)
                        .scaleXBy(1.2f)
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRandomMovies("top");
                                        startLightningFeature();
                                        updateDialogUI();
                                    }
                                });
                            }
                        });
            }

            public void onSwipeRight() {
                view.animate().translationX(5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });

                fab.setImageResource(R.drawable.ic_seen);
                fab.setVisibility(View.VISIBLE);
                fab.animate()
                        .scaleYBy(1.2f)
                        .scaleXBy(1.2f)
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRandomMovies("right");
                                        startLightningFeature();
                                        updateDialogUI();
                                    }
                                });
                            }
                        });
            }

            public void onSwipeLeft() {
                view.animate().translationX(-5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });
                fab.setImageResource(R.drawable.ic_block);
                fab.setVisibility(View.VISIBLE);
                fab.animate()
                        .scaleYBy(1.2f)
                        .scaleXBy(1.2f)
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRandomMovies("left");
                                        startLightningFeature();
                                        updateDialogUI();
                                    }
                                });
                            }
                        });
            }

            public void onSwipeBottom() {
                view.animate().translationY(5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });
                fab.setImageResource(R.drawable.ic_favorite_full);
                fab.setVisibility(View.VISIBLE);
                fab.animate()
                        .scaleYBy(1.2f)
                        .scaleXBy(1.2f)
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRandomMovies("bottom");
                                        startLightningFeature();
                                        updateDialogUI();
                                    }
                                });
                            }
                        });
            }
        });
    }

    /**
     * After Movie is swiped the random Movies list is updated.
     * 1.   - If swiped right: Add movie to Firebase seen list
     *      - If swiped left: Add movie to Firebase swiped list
     *      - If swiped top: Add movie to Firebase watch list
     *      - If swiped bottom: Add movie to Firebase seen list and add fav.
     * 2. Remove the swiped movie from the list
     * 3. Check if random movies list has less than 5 movies.
     * - Load more movies if it's less.
     */
    private void updateRandomMovies(String direction) {
        // Getting Firebase Database from current user.
        if(direction.equals("right")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userEmail).child("movies");
            databaseReference.child(randomMovies.get(0).getId()+"").setValue(randomMovies.get(0).getTitle());
        }else if(direction.equals("top")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userEmail).child("watchlist");
            databaseReference.child(randomMovies.get(0).getId()+"").setValue(randomMovies.get(0).getTitle());
        }else if(direction.equals("left")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userEmail).child("swiped");
            databaseReference.child(randomMovies.get(0).getId()+"").setValue(randomMovies.get(0).getTitle());
        }if(direction.equals("bottom")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userEmail).child("movies");
            databaseReference.child(randomMovies.get(0).getId()+"").setValue(randomMovies.get(0).getTitle());
            databaseReference.child(randomMovies.get(0).getId()+"").child("favorite").setValue("true");
        }
        randomMovies.remove(0);
        Log.d(TAG, "updateRandomMovies: swiped ::: " + direction);
        if(randomMovies.size() < 5 ){
            getMarkedMovies();
        }
    }
}
