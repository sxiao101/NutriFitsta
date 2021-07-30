package com.codepath.nutrifitsta.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.adapters.ProfileAdapter;
import com.codepath.nutrifitsta.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private RecyclerView rvPics;
    private ProfileAdapter adapter;
    private List<Post> allPosts;
    private ImageView ivProfile;
    private TextView tvUser;
    private Button btnEditProfile, btnChart;
    private File photoFile;
    private String photoFileName= "photo.jpg";
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getContext()).getSupportActionBar().setTitle("Profile");
        view.setVisibility(View.GONE);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvUser = view.findViewById(R.id.tvUser);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChart = view.findViewById(R.id.btnChart);
        rvPics = view.findViewById(R.id.rvPics);
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);

        rvPics.setAdapter(adapter);
        rvPics.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
           // ((MainActivity)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); //not working
            btnEditProfile.setVisibility(View.GONE);

            String userId = bundle.getString("user");
            Log.i("ProfileFragment", userId);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", userId);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    load(objects.get(0));
                }
            });
        } else {
            load(ParseUser.getCurrentUser());
            btnEditProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoFile = Methods.getPhotoFileUri(getContext(), photoFileName, TAG);
                    Methods.launchCamera(photoFile, getContext(), TAG, someActivityResultLauncher) ;
                }
            });
            btnChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getContext()).switchContent(R.id.flContainer, new UserActivityFragment());
                }
            });

        }

        getView().setVisibility(View.VISIBLE);
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            ParseUser.getCurrentUser().put("pfp", new ParseFile(photoFile));
                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Glide.with(getContext())
                                            .load(ParseUser.getCurrentUser().getParseFile("pfp").getUrl())
                                            .circleCrop()
                                            .into(ivProfile);
                                }
                            });
                        } else { // Result was a failure
                            Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void load(ParseUser user) {
        tvUser.setText(user.getUsername());
        ParseFile pf = user.getParseFile("pfp");
        Glide.with(getContext())
                .load(pf.getUrl())
                .circleCrop()
                .into(ivProfile);

       queryPosts(user);
    }

    private void queryPosts(ParseUser user) {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_FOOD);
        query.include(Post.KEY_FIT);
        // limit query to latest 20 items
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, user);
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
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity)getContext()).switchContent(R.id.flContainer, new SearchFragment());
        }
        return super.onOptionsItemSelected(item);
    }
}