package com.example.bien_pc.movielist.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.adapters.MoviesAdapter;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.TMDBHelper;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.models.Actor;
import com.example.bien_pc.movielist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActorActivity extends AppCompatActivity {

    // Variables
    private final String TAG = "ActorActivity";
    private int id;

    //Views
    private ImageView imageActor;
    private TextView txtName, txtBrithday, txtCountry, txtDescription;

    // RecyclerView
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);

        // Getting the id of the actor
        id = getIntent().getIntExtra("ID", 0);

        // Init. Views
        imageActor = (ImageView) findViewById(R.id.actor_image);
        txtName = (TextView) findViewById(R.id.actor_txt_name);
        txtBrithday = (TextView) findViewById(R.id.actor_txt_birthday);
        txtCountry = (TextView) findViewById(R.id.actor_txt_country);
        txtDescription = (TextView) findViewById(R.id.actor_text_description);
        recyclerView = (RecyclerView) findViewById(R.id.actor_rv_credits);

        getActorsBasicInfo();
        getActorsMovieCredits();
    }

    /**
     * Gets the id of the actor and returns an actor object.
     */
    private void getActorsBasicInfo(){
        TMDBHelper mdbUrls = new TMDBHelper();
        final String actorUrl = mdbUrls.generateActorInfoUrl(id);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, actorUrl, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        Log.d(TAG, "onResponse: URL " + actorUrl);
                        JsonParser jsonParser = new JsonParser(result);
                        Actor actor = jsonParser.getActor();
                        setBasicViewsFromActor(actor);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void getActorsMovieCredits(){
        TMDBHelper mdbUrls = new TMDBHelper();
        final String actorUrl = mdbUrls.generateActorsCredits(id);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, actorUrl, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        Log.d(TAG, "onResponse: URL " + actorUrl);
                        JsonParser jsonParser = new JsonParser(result);
                        setCreditViewsFromActor(jsonParser.getActorCredits());
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
    private void setBasicViewsFromActor(Actor actor){

        Picasso.with(this).load(actor.getProfilePath()).into(imageActor, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: poster is set.");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: poster not set. Some error idk.");
            }
        });

        txtName.setText(actor.getName());
        if(actor.getBirth() != null && !actor.getBirth().isEmpty()){
            txtBrithday.setText(actor.getBirth());
        }else{
            txtBrithday.setVisibility(View.GONE);
        }
        if(actor.getCountry() != null && !actor.getCountry().isEmpty()){
            txtCountry.setText(actor.getCountry());
        }else{
            txtCountry.setVisibility(View.GONE);
        }
        if(!actor.getBio().equals("")){
            txtDescription.setText(actor.getBio());
        }

    }

    /** [RecyclerView]
     * Setting up the RecyclerView with the credits of the actor
     * @param credits
     */
    private void setCreditViewsFromActor(ArrayList<Movie> credits){
        int numberOfColumns = 3;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, sortByYear(credits));
        recyclerView.setAdapter(moviesAdapter);
    }


    /**
     * Sorts a list by date and returns the sorted list.
     * @param list
     * @return
     */
    private ArrayList<Movie> sortByYear(ArrayList<Movie> list){
        ArrayList<Movie> sortedList = new ArrayList<>();
        for(Movie movie : list){
            String year = movie.getYear();
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

            try {
                Date date1 = format.parse(year);
                if(sortedList.isEmpty()){
                    sortedList.add(movie);
                }else{
                    for (int i = 0; i < sortedList.size(); i++) {
                        Date date2 = format.parse(sortedList.get(i).getYear());
                        if(date2.before(date1)){
                            sortedList.add(i,movie);
                            break;
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "sortByYear: some error idk.", e );
                return list;
            }
        }
        return sortedList;
    }
}
