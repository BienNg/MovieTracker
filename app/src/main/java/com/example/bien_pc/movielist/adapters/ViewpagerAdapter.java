package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.MovieActivity;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is an adapter for the viewpager in the MovieActivity which displays multiple images.
 * - Gets the id of the movie and gets the images for the viewpager from the database
 * Created by Bien-PC on 06.01.2018.
 */

public class ViewpagerAdapter extends PagerAdapter {

    //Attributes
    private final String TAG = "ViewpagerAdapter";
    private static ArrayList<String> movieImageUrls = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    /**
     * Constructor that fills the movieImageUrls list with the id.
     * @param context
     * @param movieId
     */
    public ViewpagerAdapter(final Context context, int movieId){
        this.context = context;

        // Reset the Viewpager
        movieImageUrls.clear();
        notifyDataSetChanged();

        final String movieImagesUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/images" + "?api_key=c9fa182d1bdc69a05cdaf873e0216d82";final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, movieImagesUrl, null, new Response.Listener<JSONObject>() {

                    /**
                     * This is the main part of the method.
                     * Getting the Json String and passing it on to the JsonParser to get
                     * the image URLs.
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        JsonParser jsonParser = new JsonParser(result);
                        ArrayList<String> imageUrls = jsonParser.getImageUrls();
                        updateImageURLs(imageUrls);
                        MovieActivity.updateVisibility();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);

    }

    public void updateImageURLs(ArrayList<String> list){
        movieImageUrls = list;
        notifyDataSetChanged();
    }

    /**
     * Returns the size of the movies list.
     * @return
     */
    @Override
    public int getCount() {
        return movieImageUrls.size();
    }

    // Not used
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout)object;
    }

    /**
     * This method fills the viewpager with the images.
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Set ups
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout_movie_images, container, false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.image_viewpager_movie);

        //Setting the image from URL
        Picasso.with(context).load(movieImageUrls.get(position)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");

            }
        });

        container.addView(item_view);
        return item_view;
    }

    // Not used
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
