package com.codepath.nutrifitsta.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.adapters.PostsAdapter;
import com.codepath.nutrifitsta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostsFragment extends Fragment {

    public static String API_ENDPOINT_FOOD = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    public static String API_ENDPOINT_FIT = "https://trackapi.nutritionix.com/v2/natural/exercise";
    public static final String TAG = "PostsFragment";
    public final int REQUEST_CODE = 20;

    private FloatingActionButton compose;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> posts;
    private List<Post> allPosts;
    private Spinner mySpinner;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getContext()).getSupportActionBar().setTitle("NutriFitsta");

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        compose = view.findViewById(R.id.compose);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the compose activity
                Intent intent = new Intent(getContext(), ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mySpinner = view.findViewById(R.id.mySpinner);
        mySpinner.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.filters)));

        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<>();
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), posts, allPosts);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts(false);

        //spinner selection events
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemID) {
                String input = "";
                if (position == 1) {
                    input = "food";
                } else if (position == 2) {
                    input = "fitness";
                }
                adapter.getFilter().filter(input);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void queryPosts(boolean refresh) {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_FOOD);
        query.include(Post.KEY_FIT);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                if (refresh) {
                    adapter.clear();
                }
                posts.addAll(objects);
                allPosts.addAll(objects);
                adapter.notifyDataSetChanged();
                if (refresh) {
                    //signal refresh has finished
                    swipeContainer.setRefreshing(false);
                }
            }
        });

    }
}