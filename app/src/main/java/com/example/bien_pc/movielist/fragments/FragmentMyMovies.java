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
import com.example.bien_pc.movielist.activities.SignInActivity;
import com.example.bien_pc.movielist.activities.StatisticActivity;
import com.example.bien_pc.movielist.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This Fragment shows the movies that the user has seen
 */
public class FragmentMyMovies extends Fragment {

    //Variables
    private final String TAG = "FragmentMyMovies";
    private ArrayList<Movie> mySeenMovies = new ArrayList<>();
    private final ArrayList<String> idOfMovies = new ArrayList<>();
    private final ArrayList<String> favMovies = new ArrayList<>();
    private CategoryAdapter adapter;
    private MyFirebaseUser mFirebaseUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public FragmentMyMovies() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMyMovies.
     */
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
        mFirebaseUser = new MyFirebaseUser();
        if (mFirebaseUser.getAuth().getCurrentUser() == null) {
            Log.d(TAG, "onViewCreated: user is null");
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
        } else {
            updateRecyclerUI(view);
        }
    }

    /**
     * This method sets up the RecyclerView
     */
    private void setUpRecyclerView(View view) {

        // This map contains the genre as key and their movies as value
        HashMap<String, ArrayList<Movie>> mapCategories = new HashMap<>();

        // Setting up Category Favorite Movies
        if(favMovies.size() > 0){
            for(String id : favMovies){
                for(Movie movie : mySeenMovies){
                    if(id.equals(movie.getId() + "") && !mapCategories.containsKey("Favorites")){
                        ArrayList<Movie> list = new ArrayList<>();
                        list.add(movie);
                        mapCategories.put("Favorites", list);
                        break;
                    }else if (id.equals(movie.getId() + "")){
                        ArrayList<Movie> list = mapCategories.get("Favorites");
                        list.add(movie);
                        mapCategories.put("Favorites", list);
                        break;
                    }
                }
            }
        }

        // Filling the categories with movies
        for (Movie movie : mySeenMovies){
            Log.d(TAG, "setUpRecyclerView: seen movie ::: " + movie.getTitle());
            for(String genre : movie.getGenres()){
                if(!mapCategories.containsKey(genre)){
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(movie);
                    mapCategories.put(genre, list);
                }else{
                    ArrayList<Movie> list = mapCategories.get(genre);
                    list.add(movie);
                    mapCategories.put(genre, list);
                }
            }
        }

        // Adding the categoires to the list
        ArrayList<Category> categoriesWithContent = new ArrayList<>();
        categoriesWithContent.add(new Category("Favorites", sortByYear(mapCategories.get("Favorites"))));

        // Adding every genre to the RecyclerView that has more than 9 movies
        for(String genre : mapCategories.keySet()){
            if(mapCategories.get(genre).size() > 9 && !genre.equals("Favorites")){
                categoriesWithContent.add(new Category(genre, sortByYear(mapCategories.get(genre))));
            }
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

    /**
     * Getting the seen movies from the firebase
     * @param view
     */
    private void updateRecyclerUI(final View view) {
        // Getting list of seen movies from firebase
        mFirebaseUser.getAllMoviesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    idOfMovies.add(id);
                    if(snapshot.hasChild("favorite")){
                        favMovies.add(id);
                    }
                }
                fillMySeenMoviesList(idOfMovies, view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Getting the movie objects from the data base and adds them to mySeenMovies
     * @param idOfMovies
     * @param view
     */
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

        // Start setting up the menu
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
