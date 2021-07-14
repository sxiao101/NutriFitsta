package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.R;

public class ComposeOptionsFragment extends Fragment {

    Button btnFood;
    Button btnFitness;

    public ComposeOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFood = view.findViewById(R.id.btnFood);
        btnFitness = view.findViewById(R.id.btnFitness);

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ComposeActivity)getContext()).switchContent(R.id.flCompose, new FoodComposeFragment());
            }
        });

        btnFitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ComposeActivity)getContext()).switchContent(R.id.flCompose, new FitnessComposeFragment());
            }
        });
    }
}