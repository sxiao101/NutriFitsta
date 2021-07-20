package com.codepath.nutrifitsta.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.classes.PostActions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class DetailsFragment extends Fragment {
    public static final String TAG = "DetailsCompose";

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
    private ImageButton ibSave;
    private String postId;
    private PostActions like;
    private PostActions save;

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
        ibLike = view.findViewById(R.id.ibLike);
        ibSave = view.findViewById(R.id.ibSave);

        Bundle bundle = this.getArguments();
        postId = bundle.getString("postId");
        setButtons();

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


        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ibLike.isSelected()) { //save it and make post
                    PostActions p = new PostActions();
                    p.setUser(ParseUser.getCurrentUser());
                    p.setPostId(postId);
                    p.setAction(1);
                    like = p;
                    p.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ibLike.setImageResource(R.drawable.liked_bolt);
                            ibLike.setSelected(true);
                        }
                    });
                } else {
                    like.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            like = null;
                            ibLike.setImageResource(R.drawable.ic_baseline_offline_bolt_24);
                            ibLike.setSelected(false);
                        }
                    });
                }
            }
        });

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ibSave.isSelected()) { //save it and make post
                    PostActions p = new PostActions();
                    p.setUser(ParseUser.getCurrentUser());
                    p.setPostId(postId);
                    p.setAction(2);
                    save = p;
                    p.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ibSave.setImageResource(R.drawable.ic_baseline_favorite_24);
                            ibSave.setSelected(true);
                        }
                    });
                } else {
                    save.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            save = null;
                            ibSave.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            ibSave.setSelected(false);
                        }
                    });
                }
            }
        });
    }

    private void setButtons() {
        ParseQuery<PostActions> query = ParseQuery.getQuery(PostActions.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("postId", postId);
        query.findInBackground(new FindCallback<PostActions>() {
            @Override
            public void done(List<PostActions> objects, ParseException e) {
                if (e != null) { // no actions on post
                    ibSave.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    ibSave.setSelected(false);
                    ibLike.setImageResource(R.drawable.ic_baseline_offline_bolt_24);
                    ibLike.setSelected(false);
                } else {
                    for (PostActions pa: objects) {
                        if (pa.getAction() == 1) {
                            like = pa;
                            ibLike.setImageResource(R.drawable.liked_bolt);
                            ibLike.setSelected(true);
                        } else if (pa.getAction() == 2) {
                            save = pa;
                            ibSave.setImageResource(R.drawable.ic_baseline_favorite_24);
                            ibSave.setSelected(true);
                        }
                    }
                }
            }
        });
    }

}