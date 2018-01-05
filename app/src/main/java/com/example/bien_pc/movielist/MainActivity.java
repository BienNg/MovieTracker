package com.example.bien_pc.movielist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.controller.JsonParser;
import com.example.bien_pc.movielist.controller.MovieDbUrlGenerator;
import com.example.bien_pc.movielist.controller.MySingleton;
import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;
import com.example.bien_pc.movielist.models.RequestObject;
import com.example.bien_pc.movielist.recyclerview.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.test.classes.CategoriesGenerator;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private TextView mTextMessage;
    private MovieDbUrlGenerator movieDBController;
    private ArrayList<Movie> popularMovies = new ArrayList<>();
    private CategoryAdapter adapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    requestOperation(new RequestObject("Popular Movies"));
                    return true;
                case R.id.navigation_dashboard:
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

        //Testing the MovieDB API
        Log.d(TAG, "onCreate: requestOperation starts");
        requestOperation(new RequestObject("Popular Movies"));


    }


    /**
     * This method sets up the RecyclerView
     */
    private void setUpRecyclerView(){

        CategoriesGenerator cg = new CategoriesGenerator(popularMovies);
        //Categories List
        ArrayList<Category> categories = cg.generateCategories();

        RecyclerView categoriesRecyclerView = (RecyclerView) findViewById(R.id.rv_vertical_categories);
        // Setting RecyclerView
        categoriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        categoriesRecyclerView.setLayoutManager(llm);
        // nuggetsList is an ArrayList of Custom Objects, in this case  Nugget.class
        adapter = new CategoryAdapter(this, categories);
        categoriesRecyclerView.setAdapter(adapter);
    }

    /**
     * This method gets a list of movies accoording to the @param title.
     * @param title
     */
    private void requestOperation(final RequestObject requestObject){
        class RequestOperation extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... strings) {

                // Generating the HTTP URL
                //final String url = new MovieDbUrlGenerator().generateMovieSearchUrl(title);
                final String url = new MovieDbUrlGenerator(requestObject).getRequestUrl();

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
                                Log.d(TAG, "onResponse: URL: " + url);
                                Log.d(TAG, "onResponse: " + result);
                                if(requestObject.getRequest().equals("Popular Movies")){
                                    JsonParser jsonParser = new JsonParser(result);
                                    popularMovies = jsonParser.getListOfPopularMovies();
                                }
                                setUpRecyclerView();
                            }

                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsObjRequest);
                return "";
            }
        }
        new RequestOperation().execute();
    }
}
