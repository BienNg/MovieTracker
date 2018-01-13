package com.example.bien_pc.movielist.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;
import com.example.bien_pc.movielist.test.classes.CategoriesGenerator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Fragment for the home screen.
 */
public class FragmentHome extends Fragment {

    //Variables
    private final String TAG = "FragmentHome";
    private ArrayList<Movie> popularMovies, comedyMovies, dramaMovies, horrorMovies;
    private CategoryAdapter adapter;
    private HashMap<String, ArrayList<Movie>> listOfMovies;
    private ArrayList<Category> categoriesWithContent;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentHome() {
        // Required empty public constructor
    }

    /*NOT USED*/
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*NOT USED*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Setting the title
        getActivity().setTitle("Home");

        //Setting up the Recycler View
        setUpRecyclerView(view);

        //Requesting Movies that fill the RecyclerViews
        requestOperation(view, "Popular Movies");
        requestOperation(view, "Comedy Movies");
        requestOperation(view, "Drama Movies");
        requestOperation(view, "Horror Movies");
    }

    /**
     * This method sets up the RecyclerView
     */
    private void setUpRecyclerView(View view){
        Log.d(TAG, "setUpRecyclerView: reached.");
        listOfMovies = new HashMap<>();
        listOfMovies.put("Popular Movies", popularMovies);
        listOfMovies.put("Comedy Movies", comedyMovies);
        listOfMovies.put("Drama Movies", dramaMovies);
        listOfMovies.put("Horror Movies", horrorMovies);

        CategoriesGenerator cg = new CategoriesGenerator(listOfMovies);
        //Categories List
        categoriesWithContent = cg.generateCategories();

        RecyclerView categoriesRecyclerView = (RecyclerView) view.findViewById(R.id.fm_home_rv_categories);
        // Setting RecyclerView
        categoriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        categoriesRecyclerView.setLayoutManager(llm);
        // nuggetsList is an ArrayList of Custom Objects, in this case  Nugget.class
        adapter = new CategoryAdapter(getContext(), categoriesWithContent);
        categoriesRecyclerView.setAdapter(adapter);
    }

    /**
     * This method gets a requestObject which contains the information what request is queued
     * e.g. List of popular Movies, search for movie title etc.
     */
    private void requestOperation(final View view, final String requestObject){
        Log.d(TAG, "requestOperation: reached.");
        class RequestOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                // Generating the HTTP URL
                final String url = new MDBUrls(requestObject).getUrl();

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
                                if(requestObject.equals("Popular Movies")){
                                    popularMovies = jsonParser.getList();
                                    listOfMovies.put("Popular Movies", popularMovies);
                                }else if (requestObject.equals("Comedy Movies")){
                                    comedyMovies = jsonParser.getList();
                                    listOfMovies.put("Comedy Movies", comedyMovies);
                                }else if (requestObject.equals("Drama Movies")){
                                    dramaMovies = jsonParser.getList();
                                    listOfMovies.put("Drama Movies", dramaMovies);
                                }else if (requestObject.equals("Horror Movies")){
                                    horrorMovies = jsonParser.getList();
                                    listOfMovies.put("Horror Movies", horrorMovies);
                                }
                                setUpRecyclerView(view);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
                return "";
            }
        }
        new RequestOperation().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_home, container, false);
    }

    // IDK
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
