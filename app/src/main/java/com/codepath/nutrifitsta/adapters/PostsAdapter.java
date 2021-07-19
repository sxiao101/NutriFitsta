package com.codepath.nutrifitsta.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.classes.FitnessPost;
import com.codepath.nutrifitsta.classes.FoodPost;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.fragments.DetailsFragment;
import com.codepath.nutrifitsta.fragments.ProfileFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvType;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvUsername;
        private TextView tvCategory;
        private TextView tvDetails;
        private TextView tvLocation;
        private TextView tvVideo;
        private ImageView ivProfile;
        private ImageView locPointer;
        private String loc;
        private String time;
        private String imageUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvVideo = itemView.findViewById(R.id.tvVideo);
            locPointer = itemView.findViewById(R.id.locPointer);
            ivProfile = itemView.findViewById(R.id.ivProfile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    bundle.putString("type", post.getType());

                    bundle.putString("postId", post.getPostId());
                    bundle.putString("user", post.getUser().getUsername());
                    bundle.putString("pfp", post.getUser().getParseFile("pfp").getUrl());
                    bundle.putString("category", tvCategory.getText().toString());
                    bundle.putString("details", tvDetails.getText().toString());
                    bundle.putString("description", tvDescription.getText().toString());
                    bundle.putString("video", tvVideo.getText().toString());
                    bundle.putString("loc", tvLocation.getText().toString());
                    bundle.putString("time", time);
                    bundle.putString("image", imageUrl);
                    bundle.putString("userId", post.getUser().getObjectId());
                    DetailsFragment details = new DetailsFragment();
                    details.setArguments(bundle);

                    ((MainActivity)context).switchContent(R.id.flContainer, details);

                }
            });


            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    bundle.putString("user", post.getUser().getObjectId());
                    ProfileFragment profile = new ProfileFragment();
                    profile.setArguments(bundle);

                    ((MainActivity)context).switchContent(R.id.flContainer, profile);

                }
            });
        }

        public void bind(Post post) {
            String type = post.getType();
            String id = post.getPostId();
            Log.i("Adapter", ("binding" + id));
            if (type.equals("food")) {
                bindFood(post.getFood());
            } else {
                bindFitness(post.getFitness());
            }
        }

        public void bindFood(FoodPost fp) {
                    tvType.setText("FOOD");
                    tvType.setTextColor(Color.parseColor("#8BC34A"));
            try {
                tvUsername.setText(fp.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDescription.setText(fp.getDescription());
                    tvCategory.setText(fp.getCategory());
                    if (fp.getLoc() != null) {
                        locPointer.setVisibility(View.VISIBLE);
                        tvLocation.setText(fp.getLoc());
                    } else {
                        tvLocation.setText("");
                        locPointer.setVisibility(View.GONE);
                    }
                    tvDetails.setText(fp.getNutrition() + " cal");
                    if (fp.getVideo() != null) {
                        tvVideo.setText(fp.getVideo());
                    }
                    Glide.with(context)
                            .load(fp.getUser().getParseFile("pfp").getUrl())
                            .circleCrop()
                            .into(ivProfile);
                    Glide.with(context)
                            .load(fp.getImage().getUrl())
                            .into(ivImage);
                    imageUrl = fp.getImage().getUrl();
                    time = calculateTimeAgo(fp.getCreatedAt());
        }
        public void bindFitness(FitnessPost fp) {
            tvType.setText("FITNESS");
            tvType.setTextColor(Color.parseColor("#3F51B5"));
            try {
                tvUsername.setText(fp.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDescription.setText(fp.getDescription());
            tvCategory.setText(fp.getCategory());
            if (fp.getLoc() != null) {
                locPointer.setVisibility(View.VISIBLE);
                tvLocation.setText(fp.getLoc());
            } else {
                tvLocation.setText("");
                locPointer.setVisibility(View.GONE);
            }
            tvDetails.setText(fp.getDuration() + " min");
            if (fp.getVideo() != null) {
                tvVideo.setText(fp.getVideo());
            }
            Glide.with(context)
                    .load(fp.getUser().getParseFile("pfp").getUrl())
                    .circleCrop()
                    .into(ivProfile);
            Glide.with(context)
                    .load(fp.getImage().getUrl())
                    .into(ivImage);
            imageUrl = fp.getImage().getUrl();
            time = calculateTimeAgo(fp.getCreatedAt());
        }

    /*    public void bind(Post post) {
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
                    tvUsername.setText(post.getUser().getUsername());
                    tvDescription.setText(post.getDescription());
                    tvCategory.setText(post.getCategory());
                    if (post.getLoc() != null) {
                        locPointer.setVisibility(View.VISIBLE);
                        tvLocation.setText(post.getLoc());
                    } else {
                        tvLocation.setText("");
                        locPointer.setVisibility(View.GONE);
                    }
                    tvDetails.setText(post.getNutrition() + " cal");
                    if (post.getVideo() != null) {
                        tvVideo.setText(post.getVideo());
                    }
                    Glide.with(context)
                            .load(post.getUser().getParseFile("pfp").getUrl())
                            .circleCrop()
                            .into(ivProfile);
                    Glide.with(context)
                            .load(post.getImage().getUrl())
                            .into(ivImage);
                    imageUrl = post.getImage().getUrl();
                    time = calculateTimeAgo(post.getCreatedAt());
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
                    tvUsername.setText(post.getUser().getUsername());
                    tvDescription.setText(post.getDescription());
                    tvCategory.setText(post.getCategory());
                    tvLocation.setText(post.getLoc());
                    tvDetails.setText(post.getDuration() + " min");
                    tvVideo.setText(post.getVideo());
                    Glide.with(context)
                            .load(post.getUser().getParseFile("pfp").getUrl())
                            .circleCrop()
                            .into(ivProfile);
                    Glide.with(context)
                            .load(post.getImage().getUrl())
                            .into(ivImage);

                    imageUrl = post.getImage().getUrl();
                    time = calculateTimeAgo(post.getCreatedAt());
                }
            });
        }*/

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
