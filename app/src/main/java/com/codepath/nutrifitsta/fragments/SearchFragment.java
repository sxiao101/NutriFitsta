package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.adapters.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private RecyclerView rvUsers;
    private UsersAdapter adapter;
    private List<ParseUser> allUsers;
    private List<ParseUser> users;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_items, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getContext()).getSupportActionBar().setTitle("Users");

        rvUsers = view.findViewById(R.id.rvUser);
        allUsers = new ArrayList<>();
        users = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), users, allUsers);

        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        queryUsers();
    }

    private void queryUsers() {

        // Specify which class to query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for (ParseUser user : objects){
                    Log.i(TAG, "User: " + user.getUsername());
                }

                allUsers.addAll(objects);
                users.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

}