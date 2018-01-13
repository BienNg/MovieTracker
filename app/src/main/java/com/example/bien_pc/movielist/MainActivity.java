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
    Dialog dialogUserPopup;

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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth == null) {
                    Intent intent = new Intent(this, SignIn.class);
                    startActivity(intent);
                } else {
                    showPopup();
                }
                return true;
            case R.id.menuitem_lightning:
                startLightningFeature();
                getLightningMovies();
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
    private View view;
    private int counter = -1;

    /**
     * [LightningFeature]
     * Opens the dialog to swipe through movies.
     */
    private void startLightningFeature() {
        counter++;
        final Dialog dialog = new Dialog(MainActivity.this);
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_movie, null);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                view.animate().translationY(-5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.ic_add);
                        fab.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        fab.animate()
                                .scaleYBy(2)
                                .scaleXBy(2)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                startLightningFeature();
                                                updateDialogUI();
                                            }
                                        });
                                    }
                                });
                    }
                });
            }

            public void onSwipeRight() {
                view.animate().translationX(5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.ic_seen);
                        fab.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        fab.animate()
                                .scaleYBy(2)
                                .scaleXBy(2)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        startLightningFeature();
                                        updateDialogUI();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            public void onSwipeLeft() {
                view.animate().translationX(-5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.ic_block);
                        fab.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        fab.animate()
                                .scaleYBy(2)
                                .scaleXBy(2)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                startLightningFeature();
                                                updateDialogUI();
                                            }
                                        });
                                    }
                                });
                    }
                });
            }

            public void onSwipeBottom() {
                view.animate().translationY(5000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.ic_favorite_full);
                        fab.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        fab.animate()
                                .scaleYBy(2)
                                .scaleXBy(2)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        fab.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                startLightningFeature();
                                                updateDialogUI();
                                            }
                                        });
                                    }
                                });
                    }
                });
            }
        });

        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * [LightningFeature]
     * Getting the movies that are shown in the Lightning Dialog.
     * Only show movies that are not in the users list yet.
     * 1. Look for movies of the movies.
     * 2. Get Random movies from the database.
     * 3. Remove seen movies from those random movies.
     * 4. Updated dialog UI
     */
    private void getLightningMovies() {
        Log.d(TAG, "getLightningMovies: reached.");


        // -1- Getting id from current user.
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "(dot)");
        Log.d(TAG, "getLightningMovies: user mail ::: " + email);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(email).child("movies");
        final ArrayList<String> seenMovies = new ArrayList<>();
        // Getting his movies.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String id = d.getKey();
                    seenMovies.add(id);

                }

                // -2- Generating the URL for the database.
                MDBUrls mdbUrls = new MDBUrls();
                String urlPopularMovies = mdbUrls.generatePopularMoviesUrl();
                Log.d(TAG, "getLightningMovies: urlPopularMovies ::: " + urlPopularMovies);

                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, urlPopularMovies, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String result = response.toString();
                                JsonParser jsonParser = new JsonParser(result);
                                randomMovies = jsonParser.getList();


                                // -3- Removing Seen Movies from random movie list
                                for (String seenMovie : seenMovies) {
                                    for (int i = 0; i < randomMovies.size(); i++) {
                                        if ((randomMovies.get(i).getId() + "").equals(seenMovie)) {
                                            randomMovies.remove(i);
                                        }
                                    }
                                }

                                // Update Dialog UI
                                updateDialogUI();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsObjRequest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Update the lightning ui
     */
    private void updateDialogUI(){
        final Movie randomMovie = randomMovies.get(counter);
        ImageView imageMovie = (ImageView) view.findViewById(R.id.dialog_lightning_movie_image);
        TextView txtRating = (TextView) view.findViewById(R.id.dialog_lightning_rating);
        txtRating.setText(randomMovie.getRating());

        // Setting Image
        Picasso.with(MainActivity.this).load(randomMovies.get(counter).getPosterPath()).into(imageMovie, new Callback() {
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

    private void updateRecyclerViewCast(ArrayList<Actor> actors){
        RecyclerView rvCast = (RecyclerView) view.findViewById(R.id.dialog_lightning_rv_cast);
        rvCast.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(linearLayoutManager);
        ActorsAdapter adapterActors = new ActorsAdapter(MainActivity.this, actors);
        rvCast.setAdapter(adapterActors);
    }
}
