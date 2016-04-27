package com.example.moamen.moviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;



public class MainActivity extends AppCompatActivity{


    Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (findViewById(R.id.fragment_container) != null){
//            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DetailsFragment()).commit();
//            }
//        }
        //else {
//            mTwoPane = false;
//            MainFragment mFragment = new MainFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).commit();
        //}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
