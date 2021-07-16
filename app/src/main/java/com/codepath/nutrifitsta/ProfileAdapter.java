package com.codepath.nutrifitsta;


import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.fragments.DetailsFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvType;
        private TextView tvCategory;
        private TextView tvDetails;
        private TextView tvTime;
        private ImageView ivImage;
        private String imageUrl;
        private String video;
        private String description;
        private String loc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    bundle.putString("type", post.getType());
                    bundle.putString("postId", post.getPostId());
                    bundle.putString("user", post.getUser().getUsername());
                    bundle.putString("category", tvCategory.getText().toString());
                    bundle.putString("details", tvDetails.getText().toString());
                    bundle.putString("time", tvTime.getText().toString());
                    bundle.putString("image", imageUrl);
                    bundle.putString("video", video);
                    bundle.putString("description", description);
                    bundle.putString("loc", loc);
                    bundle.putString("pfp", post.getUser().getParseFile("pfp").getUrl());

                    DetailsFragment details = new DetailsFragment();
                    details.setArguments(bundle);

                    ((MainActivity)context).switchContent(R.id.flContainer, details);

                }
            });
        }

        public void bind(Post post) {
            String type = post.getType();
            String id = post.getPostId();
            Log.i("Adapter", ("binding" + id));
            if (type.equals("food")) {
                bindFood(id);
            } else {
                bindFitness(id);
            }

        }

        public void bindFood(String postId) {
            ParseQuery<FoodPost> query = ParseQuery.getQuery(FoodPost.class);
            query.whereEqualTo("objectId", postId);
            query.include(FoodPost.KEY_USER);
            query.getFirstInBackground(new GetCallback<FoodPost>() {
                @Override
                public void done(FoodPost post, ParseException e) {
                    if (e != null) {
                        Log.e("PostsAdapter", "error", e);
                        return;
                    }
                    tvType.setText("FOOD");
                    tvType.setTextColor(Color.parseColor("#8BC34A"));
                    tvCategory.setText(post.getCategory());
                    tvDetails.setText(post.getNutrition() + " cal");
                    tvTime.setText(calculateTimeAgo(post.getCreatedAt()));

                    if (post.getImage() != null) {
                        ivImage.setVisibility(View.VISIBLE);
                        imageUrl = post.getImage().getUrl();
                        Glide.with(context)
                                .load(imageUrl)
                                .into(ivImage);
                    } else {
                        ivImage.setVisibility(View.GONE);
                    }
                    video = post.getVideo();
                    description = post.getDescription();
                    loc = post.getLoc();
                }
            });
        }
        public void bindFitness(String postId) {
            ParseQuery<FitnessPost> query = ParseQuery.getQuery(FitnessPost.class);
            query.whereEqualTo("objectId", postId);
            query.include(FitnessPost.KEY_USER);
            query.getFirstInBackground(new GetCallback<FitnessPost>() {
                @Override
                public void done(FitnessPost post, ParseException e) {
                    tvType.setText("FITNESS");
                    tvType.setTextColor(Color.parseColor("#3F51B5"));
                    tvCategory.setText(post.getType());
                    tvDetails.setText(post.getDuration() + " min");
                    tvTime.setText(calculateTimeAgo(post.getCreatedAt()));
                    if (post.getImage() != null) {
                        ivImage.setVisibility(View.VISIBLE);
                        imageUrl = post.getImage().getUrl();
                        Glide.with(context)
                                .load(post.getImage().getUrl())
                                .into(ivImage);
                    } else {
                        ivImage.setVisibility(View.GONE);
                    }
                    video = post.getVideo();
                    description = post.getDescription();
                    loc = post.getLoc();
                }
            });
        }
    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
