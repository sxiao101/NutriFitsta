package com.codepath.nutrifitsta.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.classes.FitnessPost;
import com.codepath.nutrifitsta.classes.FoodPost;
import com.codepath.nutrifitsta.classes.IPost;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.fragments.DetailsFragment;
import com.codepath.nutrifitsta.fragments.ProfileFragment;
import com.parse.ParseException;

import java.util.List;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder>{
    private Context context;
    private List<Post> posts;

    public SavedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public SavedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        return new SavedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAdapter.ViewHolder holder, int position) {
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
                    bundle.putString("postId", post.getObjectId());
                    try {
                        bundle.putString("user", post.getUser().fetchIfNeeded().getUsername());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
            if (type.equals("food")) {
                try {
                    bindPost(post.getFood().fetchIfNeeded());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    bindPost(post.getFitness().fetchIfNeeded());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        public void bindPost(IPost fp) {
            if (fp instanceof FoodPost) {
                tvType.setText("FOOD");
                tvType.setTextColor(Color.parseColor("#8BC34A"));
                tvDetails.setText(((FoodPost)fp).getNutrition() + " cal");
            }
            if (fp instanceof FitnessPost) {
                tvType.setText("FITNESS");
                tvType.setTextColor(Color.parseColor("#3F51B5"));
                tvDetails.setText(((FitnessPost)fp).getDuration() + " min");
            }
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
            time = Methods.calculateTimeAgo(fp.getCreatedAt());
        }
    }
}
