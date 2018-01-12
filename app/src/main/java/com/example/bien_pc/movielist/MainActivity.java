package com.example.bien_pc.movielist;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bien_pc.movielist.fragments.FragmentHome;
import com.example.bien_pc.movielist.fragments.FragmentMyMovies;
import com.example.bien_pc.movielist.helper.OnSwipeTouchListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener{

    private final String TAG = "MainActivity";
    // Popup Dialog of the user
    Dialog dialogUserPopup;

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

        // Init. Dialog Popup of the user
        dialogUserPopup = new Dialog(this);


    }


    /**
     * Inflates the menu xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    /**
     * Click Listener for the menu items.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_user:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth == null) {
                    Intent intent = new Intent(this, SignIn.class);
                    startActivity(intent);
                }else{
                    showPopup();
                }
                return true;
            case R.id.menuitem_lightning:
                final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_movie, null);
                view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                    public void onSwipeTop() {
                        Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                    }
                    public void onSwipeRight() {
                        Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                    }
                    public void onSwipeLeft() {
                        Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                    }
                    public void onSwipeBottom() {
                        Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                        view.animate().translationY(5000);
                    }
                });
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(view);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    // Shows the pop up window of the user
    private void showPopup(){
        dialogUserPopup.setContentView(R.layout.popup_user);
        dialogUserPopup.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
