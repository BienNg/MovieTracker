package com.example.bien_pc.movielist;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.bien_pc.movielist.Fragments.FragmentHome;
import com.example.bien_pc.movielist.Fragments.FragmentMyMovies;

public class MainActivity extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener{

    private final String TAG = "MainActivity";

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

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
