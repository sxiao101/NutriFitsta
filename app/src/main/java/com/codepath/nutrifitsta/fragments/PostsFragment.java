package com.codepath.nutrifitsta.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import okhttp3.Headers;

public class PostsFragment extends Fragment {

    public static String API_ENDPOINT_FOOD = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    public static String API_ENDPOINT_FIT = "https://trackapi.nutritionix.com/v2/natural/exercise";
    public static final String TAG = "PostsFragment";
    public final int REQUEST_CODE = 20;

    private FloatingActionButton compose;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;

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

        compose = view.findViewById(R.id.compose);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the compose activity
                Intent intent = new Intent(getContext(), ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // testing api request
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        headers.put("Content-Type", "application/json");
        headers.put("x-app-id", getString(R.string.app_id));
        headers.put("x-app-key", getString(R.string.app_key));
        headers.put("x-remote-user-id", "0");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("query", "1 cup of celery");
        Gson gson = new Gson();
        client.post(API_ENDPOINT_FOOD, headers, new RequestParams(), gson.toJson(body), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("foods");
                    Integer cal = results.getJSONObject(0).getInt("nf_calories");
                    Log.i(TAG, "calories: " + cal);
                    Log.i(TAG, "results: " + results.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure" + response, throwable);
            }
        });
        body = new HashMap<String, String>();
        body.put("query", "ran 3 miles");
        body.put("gender", "female");
        body.put("weight_kg", "72.5");
        body.put("height_cm", "167.64");
        body.put("age", "30");
        client.post(API_ENDPOINT_FIT, headers, new RequestParams(), gson.toJson(body), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                try {
                    JSONArray results = json.jsonObject.getJSONArray("exercises");
                    Log.i(TAG, "results: " + results.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure" + response, throwable);
            }
        });


        queryPosts();
    }

    private void queryPosts() {
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
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts){
                    Log.i(TAG, "Post: " + post.getPostId());
                }
//                if (refresh) {
//                    adapter.clear();
//                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
//                if (refresh) {
//                    // Now we call setRefreshing(false) to signal refresh has finished
//                    swipeContainer.setRefreshing(false);
//                }
            }
        });

    }
}