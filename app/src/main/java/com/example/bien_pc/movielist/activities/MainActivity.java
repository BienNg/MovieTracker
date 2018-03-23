package com.example.bien_pc.movielist.activities;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.adapters.ActorsAdapter;
import com.example.bien_pc.movielist.fragments.FragmentHome;
import com.example.bien_pc.movielist.fragments.FragmentMyMovies;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.helper.OnSwipeTouchListener;
import com.example.bien_pc.movielist.models.Actor;
import com.example.bien_pc.movielist.models.Movie;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;
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
    private MyFirebaseUser myFirebasUser;

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
                case R.id.navigation_my_movies:
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

        // Init. Variables
        dialogUserPopup = new Dialog(this);
        myFirebasUser = new MyFirebaseUser();

        // Set watch request listener
        if (myFirebasUser.getAuth().getCurrentUser() != null) {
            setWatchRequestListener();
        }
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
            // Shows the user information
            case R.id.menuitem_user:
                // Start signin activity if user is not logged in
                if (myFirebasUser.getAuth().getCurrentUser() == null) {
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                } else {
                    showPopup();
                }
                return true;

            // Starts the lightning feature
            case R.id.menuitem_lightning:
                if (myFirebasUser.getAuth() != null) {
                    startLightningFeature();
                    getMarkedMovies();
                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Shows the pop up window of the user
    private void showPopup() {
        dialogUserPopup.setContentView(R.layout.dialog_user);
        Button bttnLogout = (Button) dialogUserPopup.findViewById(R.id.dialog_main_user);
        TextView txtvEmail = (TextView) dialogUserPopup.findViewById(R.id.dialog_txt_email);
        txtvEmail.setText(myFirebasUser.getUsername());
        bttnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFirebasUser.getAuth().signOut();
                myFirebasUser = null;
                dialogUserPopup.dismiss();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
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

    /**[LightningFeature]
     * 1. Gets every movie that is stored in the firebase database of the user.
     * 2. getLightningMovies()
     */
    private void getMarkedMovies() {
        final ArrayList<Integer> firebaseCounter = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(myFirebasUser.getUsername())){
                    if(dataSnapshot.child(myFirebasUser.getUsername()).hasChild("movies")){
                        // -1- Getting seen movies from current user.
                        // Getting his movies.
                        myFirebasUser.getAllMoviesReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "Firebase: movies read.");
                                    String id = d.getKey();
                                    markedMovies.add(id);
                                    firebaseCounter.add(1);
                                    if (firebaseCounter.size() == 3) {
                                        getLightningMovies();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }else{
                        firebaseCounter.add(1);
                        if (firebaseCounter.size() == 3) {
                            getLightningMovies();
                        }
                    }
                    if(dataSnapshot.child(myFirebasUser.getUsername()).hasChild("swiped")){
                        // -2- Getting swiped movies from current user
                        // Getting his movies.
                        myFirebasUser.getSwipedMoviesReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    String id = d.getKey();
                                    markedMovies.add(id);
                                    firebaseCounter.add(1);
                                    if (firebaseCounter.size() == 3) {
                                        getLightningMovies();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }else{
                        firebaseCounter.add(1);
                        if (firebaseCounter.size() == 3) {
                            getLightningMovies();
                        }
                    }
                    if(dataSnapshot.child(myFirebasUser.getUsername()).hasChild("watchlist")){
                        // Getting watchlist movies from current user
                        // Getting his movies.
                        myFirebasUser.getWatchedReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "Firebase: watchlist read.");
                                    String id = d.getKey();
                                    markedMovies.add(id);
                                    firebaseCounter.add(1);
                                    if (firebaseCounter.size() == 3) {
                                        getLightningMovies();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }else{
                        firebaseCounter.add(1);
                        if (firebaseCounter.size() == 3) {
                            getLightningMovies();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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


                                // -2- Removing Marked Movies from random movie list
                                for (String seenMovie : markedMovies) {
                                    for (int i = 0; i < randomMovies.size(); i++) {
                                        if ((randomMovies.get(i).getId() + "").equals(seenMovie)) {
                                            randomMovies.remove(i);
                                        }
                                    }
                                }

                                for (Movie movie : randomMovies) {
                                    Log.d(TAG, "full list of random movies ::: " + movie.getTitle());
                                }

                                // If more than 10 Random movies are generated the UI should be uptdated with the first movie
                                if (randomMovies.size() > 5) {
                                    // -3- Update Dialog UI
                                    updateDialogUI();
                                } else {
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
     * @param actors
     */
    private void updateRecyclerViewCast(ArrayList<Actor> actors) {
        Log.d(TAG, "updateRecyclerViewCast: reached");
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
     * - If swiped left: Add movie to Firebase swiped list
     * - If swiped top: Add movie to Firebase watch list
     * - If swiped bottom: Add movie to Firebase seen list and add fav.
     * 2. Remove the swiped movie from the list
     * 3. Check if random movies list has less than 5 movies.
     * - Load more movies if it's less.
     */
    private void updateRandomMovies(String direction) {
        // Getting Firebase Database from current user.
        if (direction.equals("right")) {
            myFirebasUser.getAllMoviesReference()
                    .child(randomMovies.get(0).getId() + "")
                    .child("title")
                    .setValue(randomMovies.get(0).getTitle());
        } else if (direction.equals("top")) {
            // Adding the movie to the watchlist of the user
            myFirebasUser.getWatchedReference()
                    .child(randomMovies.get(0).getId() + "")
                    .child("title")
                    .setValue(randomMovies.get(0).getTitle());

            // Putting the user on the watch_request list with the movie
            DatabaseReference databaseWatchrequestsRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("watch_requests")
                    .child(randomMovies.get(0).getId()+"")
                    .child(myFirebasUser.getUsername());
            // TODO Change to the gender of the user
            databaseWatchrequestsRef.setValue("male");
        } else if (direction.equals("left")) {
            myFirebasUser.getSwipedMoviesReference()
                    .child(randomMovies.get(0).getId() + "")
                    .child("title")
                    .setValue(randomMovies.get(0).getTitle());
        }
        if (direction.equals("bottom")) {
            myFirebasUser.getAllMoviesReference().child(randomMovies.get(0).getId() + "").child("title").setValue(randomMovies.get(0).getTitle());
            myFirebasUser.getAllMoviesReference().child(randomMovies.get(0).getId() + "").child("favorite").setValue("true");
        }
        randomMovies.remove(0);
        Log.d(TAG, "updateRandomMovies: swiped ::: " + direction);
        if (randomMovies.size() < 5) {
            getMarkedMovies();
        }
    }

    /**
     * Sends a notification if another user sends a watch request.
     */
    private void setWatchRequestListener(){
        DatabaseReference databaseReferenceWatchRequest = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("user")
                .child(myFirebasUser.getUsername())
                .child("watch_request");
        databaseReferenceWatchRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
