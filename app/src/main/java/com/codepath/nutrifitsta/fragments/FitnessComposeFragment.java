package com.codepath.nutrifitsta.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.codepath.nutrifitsta.classes.FitnessPost;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.databinding.FragmentDetailsBinding;
import com.codepath.nutrifitsta.databinding.FragmentFitnessComposeBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FitnessComposeFragment extends Fragment implements ComposeListDialog.ComposeDialogListener {
    public static final String TAG = "FitnessCompose";
    private File photoFile;
    private String photoFileName= "photo.jpg";
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    FragmentFitnessComposeBinding binding;
    private List<String> workout;
    private List<Integer> workoutCal;
    private int cal;

    public FitnessComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFitnessComposeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ComposeActivity)getContext()).getSupportActionBar().setTitle("Add Workout... ");
        ((ComposeActivity)getContext()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B9778")));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.workout_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(myAdapter);

        workout = new ArrayList<>();
        workoutCal = new ArrayList<>();
        cal = 0;
        binding.ivPostImage.setVisibility(View.GONE);
        binding.ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFile = Methods.getPhotoFileUri(getContext(), photoFileName, TAG);
                Methods.launchCamera(photoFile, getContext(), TAG, someActivityResultLauncher);
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = binding.etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show(); // doesn't work?
                    Log.i(TAG, "no description");
                    return;
                }
                boolean hasPic = true;
                if(photoFile == null || binding.ivPostImage.getDrawable() == null) {
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
                            binding.ivPostImage.setVisibility(View.VISIBLE);
                            binding.ivPostImage.setImageBitmap(takenImage);
                        } else { // Result was a failure
                            Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    // launch the edit dialog
    private void openDialog() {
        ComposeListDialog dialog = ComposeListDialog.newInstance("fitness", workout, workoutCal, cal);
        dialog.setTargetFragment(FitnessComposeFragment.this, 1);
        dialog.show(getFragmentManager(), "ComposeListDialog");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void sendInput(List<String> items, List<Integer> itemsCal, int totalCal) {
        cal = totalCal;
        workout.clear();
        workout.addAll(items);
        workoutCal.clear();
        workoutCal.addAll(itemsCal);
        Toast.makeText(getContext(), "Exercises added!", Toast.LENGTH_SHORT).show();
    }

    private void savePost(String description, ParseUser currentUser, boolean hasPic) {
        //pb.setVisibility(ProgressBar.VISIBLE);
        FitnessPost fp = new FitnessPost();
        fp.setUser(currentUser);
        fp.setDescription(description);
        if (hasPic) {
            fp.setImage(new ParseFile(photoFile));
        }
        fp.setCategory(binding.spinner.getSelectedItem().toString());
        if (!binding.etVideo.getText().toString().isEmpty()) {
            fp.setVideo(binding.etVideo.getText().toString());
        }
        if (!binding.etLocation.getText().toString().isEmpty()) {
            fp.setLoc(binding.etLocation.getText().toString());
        }
        if (!workout.isEmpty()) {
            fp.setList(new JSONArray(workout));
        }
        fp.setCal(cal);
        fp.setDuration(Integer.parseInt(binding.etDuration.getText().toString()));
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
                post.setFitness(fp);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //pb.setVisibility(ProgressBar.GONE);
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Outer Post save was successful!");
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