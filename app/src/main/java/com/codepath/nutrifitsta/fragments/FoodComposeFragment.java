package com.codepath.nutrifitsta.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.classes.FoodPost;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class FoodComposeFragment extends Fragment {

    public static final String TAG = "FoodCompose";
    private TextView tvPicture;
    private TextView tvCategory;
    private EditText etLocation;
    private EditText etNutrition;
    private EditText etVideo;
    private EditText etDescription;
    private ImageButton ibCamera;
    private Spinner spinner;
    private Button btnPost;
    private ImageView ivPostImage;
    private File photoFile;
    private String photoFileName= "photo.jpg";
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    public FoodComposeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ComposeActivity)getContext()).getSupportActionBar().setTitle("Add Food... ");
        ((ComposeActivity)getContext()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8BC34A")));

        tvPicture = view.findViewById(R.id.tvPicture);
        tvCategory = view.findViewById(R.id.tvCategory);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        etNutrition = view.findViewById(R.id.etNutrition);
        etVideo = view.findViewById(R.id.etVideo);
        ibCamera = view.findViewById(R.id.ibCamera);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        ivPostImage.setVisibility(View.GONE);
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFile = Methods.getPhotoFileUri(getContext(), photoFileName, TAG);
                Methods.launchCamera(photoFile, getContext(), TAG, someActivityResultLauncher) ;
            }
        });

        btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show(); // doesn't work?
                    Log.i(TAG, "no description");
                    return;
                }
                boolean hasPic = true;
                if(photoFile == null || ivPostImage.getDrawable() == null) {
//                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
//                    return;
                    hasPic = false;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, hasPic);
            }
        });
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            ivPostImage.setVisibility(View.VISIBLE);
                            ivPostImage.setImageBitmap(takenImage);
                        } else { // Result was a failure
                            Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savePost(String description, ParseUser currentUser, boolean hasPic) {
        //pb.setVisibility(ProgressBar.VISIBLE);
        FoodPost fp = new FoodPost();
        fp.setUser(currentUser);
        fp.setDescription(description);
        if (hasPic) {
            fp.setImage(new ParseFile(photoFile));
        }
        fp.setCategory(spinner.getSelectedItem().toString());
        fp.setNutrition(Integer.parseInt(etNutrition.getText().toString()));
        fp.setVideo(etVideo.getText().toString());
        fp.setLoc(etLocation.getText().toString());
        fp.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Inner FoodPost save was successful!");
                Post post = new Post();
                post.setType("food");
                post.setPostId(fp.getObjectId());
                post.setUser(currentUser);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //pb.setVisibility(ProgressBar.GONE);
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Outer Post save was successful!");
                        //etDescription.setText("");
                        //ivPostImage.setImageResource(0);
                        goMainActivity();
                    }
                });

            }
        });

    }

    private void goMainActivity() {
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }

}