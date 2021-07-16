package com.codepath.nutrifitsta.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.R;
public class DetailsFragment extends Fragment {

    private TextView tvType;
    private TextView tvUsername;
    private ImageView ivProfile;
    private ImageView ivImage;
    private TextView tvCategory;
    private ImageView locPointer;
    private TextView tvLocation;
    private TextView tvDetails;
    private TextView tvVideo;
    private TextView tvDescription;
    private TextView tvTime;

    private ImageButton ibLike;
    private ImageButton ibComment;
    private ImageButton ibDirect;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvType = view.findViewById(R.id.tvType);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivImage = view.findViewById(R.id.ivImage);
        tvCategory = view.findViewById(R.id.tvCategory);
        locPointer = view.findViewById(R.id.locPointer);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDetails = view.findViewById(R.id.tvDetails);
        tvVideo = view.findViewById(R.id.tvVideo);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvTime = view.findViewById(R.id.tvTime);

        Bundle bundle = this.getArguments();
        String type = bundle.getString("type");
        if (type.equals("food")) {
            tvType.setText("FOOD");
            tvType.setTextColor(Color.parseColor("#8BC34A"));
        } else {
            tvType.setText("FITNESS");
            tvType.setTextColor(Color.parseColor("#3F51B5"));
        }
        tvCategory.setText(bundle.getString("category"));
        tvUsername.setText(bundle.getString("user"));
        tvDetails.setText(bundle.getString("details"));
        tvDescription.setText(bundle.getString("description"));
        tvTime.setText(bundle.getString("time"));
        tvVideo.setText(bundle.getString("video"));
        String loc = bundle.getString("loc");
        tvLocation.setText(loc);

        Glide.with(getContext())
                .load(bundle.getString("pfp"))
                .circleCrop()
                .into(ivProfile);
        Glide.with(getContext())
                .load(bundle.getString("image"))
                .into(ivImage);
        tvTime.setText(bundle.getString("time"));

    }
}