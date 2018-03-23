package com.example.bien_pc.movielist.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.helper.TMDBHelper;
import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    // VAriables
    final private String TAG = "SearchActivity";
    private ArrayList<Movie> listOfMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());
        String query = getIntent().getStringExtra("QUERY");
        startQuery(query);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "handleIntent: query ::: " + query);
        }
    }

    private void startQuery(String query){
        TMDBHelper tmdbHelper = new TMDBHelper();
        String url = tmdbHelper.generateMovieSearchUrl(query);
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
                        for(Movie m : listOfMovies){
                            Log.d(TAG, "startQuery: " + m.getTitle() );
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }
}
