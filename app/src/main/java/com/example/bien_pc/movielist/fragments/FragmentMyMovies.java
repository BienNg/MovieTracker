package com.example.bien_pc.movielist.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.SignIn;
import com.example.bien_pc.movielist.StatisticActivity;
import com.example.bien_pc.movielist.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.controller.JsonParser;
import com.example.bien_pc.movielist.controller.MDBUrls;
import com.example.bien_pc.movielist.controller.MySingleton;
import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This Fragment shows the movies that the user has seen
 */
public class FragmentMyMovies extends Fragment {

    //Variables
    private final String TAG = "FragmentMyMovies";
    private ArrayList<Movie> mySeenMovies = new ArrayList<>();
    private final ArrayList<String> idOfMovies = new ArrayList<>();
    private CategoryAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public FragmentMyMovies() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMyMovies.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMyMovies newInstance(String param1, String param2) {
        FragmentMyMovies fragment = new FragmentMyMovies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Setting the views of the Fragment.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the title
        getActivity().setTitle("My Movies");

        // Check if user exists
        // Start sign activity if not
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "onViewCreated: user is null");
            Intent intent = new Intent(getContext(), SignIn.class);
            startActivity(intent);
        } else {

            //TEST TODO delete
            updateRecyclerUI(view);
        }
    }

    /**
     * This method sets up the RecyclerView
     */
    private void setUpRecyclerView(View view) {

        // Init. lists for the categories
        ArrayList<Movie> comedyMovies = new ArrayList<>();
        ArrayList<Movie> dramaMovies = new ArrayList<>();
        ArrayList<Movie> horrorMovies = new ArrayList<>();

        // Seperating seen movies into their categories
        for (Movie movie : mySeenMovies) {
            for (String genre : movie.getGenres()) {
                if (genre.equals("Drama")) {
                    dramaMovies.add(movie);
                }
                if (genre.equals("Horror")) {
                    horrorMovies.add(movie);
                }
                if (genre.equals("Comedy")) {
                    comedyMovies.add(movie);
                }
            }
        }

        // Adding the categoires to a list
        ArrayList<Category> categoriesWithContent = new ArrayList<>();
        if (comedyMovies.size() > 0) {
            categoriesWithContent.add(new Category("Comedy", comedyMovies));
        }
        if (dramaMovies.size() > 0) {
            categoriesWithContent.add(new Category("Drama", dramaMovies));
        }
        if (categoriesWithContent.size() > 0) {
            categoriesWithContent.add(new Category("Horror", horrorMovies));
        }

        RecyclerView categoriesRecyclerView = (RecyclerView) view.findViewById(R.id.fm_mymovies_rv_categories);
        // Setting RecyclerView
        categoriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        categoriesRecyclerView.setLayoutManager(llm);
        // nuggetsList is an ArrayList of Custom Objects, in this case  Nugget.class
        adapter = new CategoryAdapter(getContext(), categoriesWithContent);
        categoriesRecyclerView.setAdapter(adapter);
    }

    private void updateRecyclerUI(final View view) {
        // Init. movies of the user
        ArrayList<Movie> moviesOfUser = new ArrayList<>();

        // Init. Firebase Database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail().replace(".", "(dot)");
        DatabaseReference databaseReference = database.getReference(email).child("movies");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    idOfMovies.add(id);
                }
                fillMySeenMoviesList(idOfMovies, view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fillMySeenMoviesList(final ArrayList<String> idOfMovies, final View view) {

        for (String id : idOfMovies) {
            MDBUrls mdbUrls = new MDBUrls();
            final String movieUrl = mdbUrls.getURL() + "/movie/" + id + mdbUrls.getAPI_KEY();
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                        /**
                         * This is the main part of the method.
                         * Getting the Json String and pass it on to the JsonParser.
                         *
                         * @param response
                         */
                        @Override
                        public void onResponse(JSONObject response) {
                            String result = response.toString();
                            JsonParser jsonParser = new JsonParser(result);
                            Movie movie = jsonParser.getMovie();
                            mySeenMovies.add(movie);
                            Log.d(TAG, "onResponse: mySeenMovies size ::: " + mySeenMovies.size());
                            if (mySeenMovies.size() == idOfMovies.size()) {
                                setUpRecyclerView(view);
                            }
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_my_movies, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_mymovies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_statistics:
                if(idOfMovies.size() != 0){
                    Intent intent = new Intent(getActivity(), StatisticActivity.class);
                    intent.putStringArrayListExtra("ID_OF_MOVIES", idOfMovies);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Please wait until movies have been loaded.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
