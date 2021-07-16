package com.codepath.nutrifitsta.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

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
import com.codepath.nutrifitsta.classes.FitnessPost;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class FitnessComposeFragment extends Fragment {
    public static final String TAG = "FitnessCompose";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private TextView tvPicture;
    private TextView tvType;
    private EditText etLocation;
    private EditText etDuration;
    private EditText etVideo;
    private EditText etDescription;
    private ImageButton ibCamera;
    private Button btnPost;
    private Spinner spinner;
    private ImageView ivPostImage;
    private File photoFile;
    private String photoFileName= "photo.jpg";

    public FitnessComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fitness_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ComposeActivity)getContext()).getSupportActionBar().setTitle("Add Workout... ");
        ((ComposeActivity)getContext()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

        tvPicture = view.findViewById(R.id.tvPicture);
        tvType = view.findViewById(R.id.tvType);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        etDuration = view.findViewById(R.id.etDuration);
        etVideo = view.findViewById(R.id.etVideo);
        ibCamera = view.findViewById(R.id.ibCamera);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        ivPostImage.setVisibility(View.GONE);
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.workout_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
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
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.NUTRIFITSTA", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivPostImage.setVisibility(View.VISIBLE);
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost(String description, ParseUser currentUser, boolean hasPic) {

        //pb.setVisibility(ProgressBar.VISIBLE);
        FitnessPost fp = new FitnessPost();
        fp.setUser(currentUser);
        fp.setDescription(description);
        if (hasPic) {
            fp.setImage(new ParseFile(photoFile));
        }
        fp.setType(spinner.getSelectedItem().toString());
        fp.setDuration(Integer.parseInt(etDuration.getText().toString()));
        fp.setVideo(etVideo.getText().toString());
        fp.setLoc(etLocation.getText().toString());
        fp.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Inner FitnessPost save was successful!");
                Post post = new Post();
                post.setType("fitness");
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