package com.example.bien_pc.movielist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.recyclerview.adapters.CategoryAdapter;
import com.example.bien_pc.movielist.test.classes.CategoriesGenerator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
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


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Setting up the RecyclerView
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){

        CategoriesGenerator cg = new CategoriesGenerator();
        //Categories List
        ArrayList<Category> categories = cg.generateCategories();

        RecyclerView categoriesRecyclerView = (RecyclerView) findViewById(R.id.rv_vertical_categories);
        // Setting RecyclerView
        categoriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        categoriesRecyclerView.setLayoutManager(llm);
        // nuggetsList is an ArrayList of Custom Objects, in this case  Nugget.class
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        categoriesRecyclerView.setAdapter(adapter);
    }

}
