package com.codepath.nutrifitsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.nutrifitsta.fragments.ComposeOptionsFragment;
import com.codepath.nutrifitsta.fragments.PostsFragment;
import com.codepath.nutrifitsta.fragments.ProfileFragment;
import com.codepath.nutrifitsta.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Compose");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment = new ComposeOptionsFragment();
        fragmentManager.beginTransaction().replace(R.id.flCompose, fragment).commit();

    }

    public void switchContent(int id, Fragment fragment) {
        fragmentManager.beginTransaction().replace(id, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}