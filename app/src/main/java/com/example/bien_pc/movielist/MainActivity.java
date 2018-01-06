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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.controller.JsonParser;
import com.example.bien_pc.movielist.controller.MovieDBController;
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
    ArrayList<ArrayList<Movie>> listOfMovies;
    private ArrayList<Movie> popularMovies, comedyMovies, dramaMovies, horrorMovies;
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
        requestOperation(new RequestObject("Comedy Movies"));
        requestOperation(new RequestObject("Drama Movies"));
        requestOperation(new RequestObject("Horror Movies"));
    }


    /**
     * This method sets up the RecyclerView
     */
    private void setUpRecyclerView(){

        listOfMovies = new ArrayList<>();
        listOfMovies.add(popularMovies);
        listOfMovies.add(comedyMovies);
        listOfMovies.add(dramaMovies);
        listOfMovies.add(horrorMovies);

        CategoriesGenerator cg = new CategoriesGenerator(listOfMovies);
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
     * This method gets a requestObject which contains the information what request is queued
     * e.g. List of popular Movies, search for movie title etc.
     */
    private void requestOperation(final RequestObject requestObject){
        class RequestOperation extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... strings) {

                // Generating the HTTP URL
                final String url = new MovieDBController(requestObject).getUrl();

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
                                if(requestObject.getRequest().equals("Popular Movies")){
                                    popularMovies = jsonParser.getList();
                                }else if (requestObject.getRequest().equals("Comedy Movies")){
                                    comedyMovies = jsonParser.getList();
                                }else if (requestObject.getRequest().equals("Drama Movies")){
                                    dramaMovies = jsonParser.getList();
                                }else if (requestObject.getRequest().equals("Horror Movies")){
                                    horrorMovies = jsonParser.getList();
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
