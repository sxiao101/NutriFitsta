package com.codepath.nutrifitsta.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.FitnessPost;
import com.codepath.nutrifitsta.FoodPost;
import com.codepath.nutrifitsta.Post;
import com.codepath.nutrifitsta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    public final int REQUEST_CODE = 20;

    private FloatingActionButton compose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compose = view.findViewById(R.id.compose);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the compose activity
                Intent intent = new Intent(getContext(), ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
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
                    String type = post.getType();
                    String id = post.getPostId();
                    if (type.equals("food")) {
                        getFoodPost(id);
                    } else {
                        getFitnessPost(id);
                    }
                }
//                if (refresh) {
//                    adapter.clear();
//                }
//                allPosts.addAll(posts);
//                adapter.notifyDataSetChanged();
//                if (refresh) {
//                    // Now we call setRefreshing(false) to signal refresh has finished
//                    swipeContainer.setRefreshing(false);
//                }
            }
        });

    }

    private void getFoodPost(String postId) {
        ParseQuery<FoodPost> query = ParseQuery.getQuery(FoodPost.class);
        query.whereEqualTo("objectId", postId);
        query.include(FoodPost.KEY_USER);
        query.getFirstInBackground(new GetCallback<FoodPost>() {
            @Override
            public void done(FoodPost object, ParseException e) {
                Log.i(TAG, "FoodPost: " + object.getDescription() + ", username: " + object.getUser().getUsername());
            }
        });
    }
    
    private void getFitnessPost(String postId) {
        ParseQuery<FitnessPost> query = ParseQuery.getQuery(FitnessPost.class);
        query.whereEqualTo("objectId", postId);
        query.include(FitnessPost.KEY_USER);
        query.getFirstInBackground(new GetCallback<FitnessPost>() {
            @Override
            public void done(FitnessPost object, ParseException e) {
                Log.i(TAG, "FitnessPost: " + object.getDescription() + ", username: " + object.getUser().getUsername());

            }
        });
    }
}