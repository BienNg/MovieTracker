package com.example.bien_pc.movielist.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.adapters.RvAdapterSearch;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.helper.TMDBHelper;
import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    // VAriables
    final private String TAG = "TAG:SearchActivity";
    private ArrayList<Movie> listOfMovies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getStringExtra("QUERY");

        Log.d(TAG, "onCreate: query ::: " + query);
        startQuery(query);
    }


    private void startQuery(String query){
        TMDBHelper tmdbHelper = new TMDBHelper();
        String url = tmdbHelper.generateMovieSearchUrl(query);

        // Getting the list from the movie database
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and pass it on to the JsonParser.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        JsonParser jsonParser = new JsonParser(result);
                        listOfMovies = jsonParser.getList();
                        updateUi(listOfMovies);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void updateUi(ArrayList<Movie> movies){
        for(Movie m : movies){
            Log.d(TAG, "updateUi: movies ::: " + m.getTitle());
        }

        // Setting up RecyclerView
        RecyclerView recyclerViewResults = (RecyclerView) findViewById(R.id.act_search_recycler_view);
        recyclerViewResults.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewResults.setLayoutManager(llm);
        // nuggetsList is an ArrayList of Custom Objects, in this case  Nugget.class
        RvAdapterSearch adapter = new RvAdapterSearch(this, listOfMovies);
        recyclerViewResults.setAdapter(adapter);
    }
}
