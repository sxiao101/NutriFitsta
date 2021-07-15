package com.codepath.nutrifitsta.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.R;

public class FitnessComposeFragment extends Fragment {

    private TextView tvPicture;
    private TextView tvType;
    private EditText etLocation;
    private EditText etDuration;
    private EditText etVideo;
    private EditText etDescription;
    private ImageButton ibCamera;
    private Button btnPost;
    private Spinner spinner;

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
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.workout_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        btnPost = view.findViewById(R.id.btnPost);
    }
}