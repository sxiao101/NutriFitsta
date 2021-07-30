package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.adapters.PostsAdapter;
import com.codepath.nutrifitsta.adapters.SavedAdapter;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.classes.PostActions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {
    public static final String TAG = "SavedFragment";
    private RecyclerView rvSaved;
    private SavedAdapter adapter;
    private List<Post> savedPosts;
    private TextView message;

    public SavedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getContext()).getSupportActionBar().setTitle("Saved");
        message = view.findViewById(R.id.message);
        message.setVisibility(View.GONE);

        rvSaved = view.findViewById(R.id.rvSaved);
        savedPosts = new ArrayList<>();
        adapter = new SavedAdapter(getContext(), savedPosts);

        rvSaved.setAdapter(adapter);
        rvSaved.setLayoutManager(new GridLayoutManager(getContext(), 2));

        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<PostActions> query = ParseQuery.getQuery(PostActions.class);
        query.include(Post.KEY_USER);
        query.include("post");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("action", 2);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<PostActions>() {
            @Override
            public void done(List<PostActions> objects, ParseException e) {
                if (e == null) {
                    if (objects.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                    } else {
                        message.setVisibility(View.GONE);
                        for (PostActions pa : objects) {
                            savedPosts.add(pa.getPost());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}