package com.codepath.simpleinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.codepath.simpleinstagram.fragments.HomeFragment;
import com.codepath.simpleinstagram.fragments.ComposeFragment;
import com.codepath.simpleinstagram.fragments.ProfileFragment;
import com.codepath.simpleinstagram.fragments.ProfileGridFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static BottomNavigationView bottomNavigationView;
    public static MenuItem miActionProgressItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.placeholder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);

        frameLayout.setPadding(0,0,0,0);

        // define your fragments here
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new ComposeFragment();
        final Fragment fragment3 = new ProfileGridFragment();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_favorites:
                        //item.setIcon(R.drawable.ic_instagram_home_filled_24);
                        fragment = fragment1;
                        break;
                    case R.id.action_schedules:
                        //item.setIcon(R.drawable.ic_instagram_new_post_filled_24);
                        fragment = fragment2;
                        break;
                    case R.id.action_music:
                        //item.setIcon(R.drawable.ic_instagram_user_filled_24);
                        fragment = fragment3;
                        break;
                    default: return true;
                }
                fragmentManager.beginTransaction().replace(R.id.placeholder, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_favorites);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            // Compose icon has been selected
            //Navigate to compose activity
            ParseUser.logOut();
            goLaunchActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    private void goLaunchActivity(){
        Intent i = new Intent(this, LaunchActivity.class);
        startActivity(i);
        finish();
    }

    public static void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public static void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
}