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
import com.codepath.nutrifitsta.classes.IPost;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.fragments.DetailsFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
                    try {
                        bundle.putString("user", post.getUser().fetchIfNeeded().getUsername());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    bundle.putString("category", tvCategory.getText().toString());
                    bundle.putString("details", tvDetails.getText().toString());
                    bundle.putString("time", tvTime.getText().toString());
                    bundle.putString("image", imageUrl);
                    bundle.putString("video", video);
                    bundle.putString("description", description);
                    bundle.putString("loc", loc);
                    bundle.putString("pfp", post.getUser().getParseFile("pfp").getUrl());
                    bundle.putString("userId", post.getUser().getObjectId());

                    DetailsFragment details = new DetailsFragment();
                    details.setArguments(bundle);

                    ((MainActivity)context).switchContent(R.id.flContainer, details);

                }
            });
        }

        public void bind(Post post) {
            String type = post.getType();
            if (type.equals("food")) {
                bindPost(post.getFood());
            } else {
                bindPost(post.getFitness());
            }

        }

        public void bindPost(IPost fp) {
            if (fp instanceof FoodPost) {
                tvType.setText("FOOD");
                tvType.setTextColor(Color.parseColor("#F4B18C"));
                tvDetails.setText(((FoodPost)fp).getNutrition() + " cal");
            }
            if (fp instanceof FitnessPost) {
                tvType.setText("FITNESS");
                tvType.setTextColor(Color.parseColor("#3B9778"));
                tvDetails.setText(((FitnessPost)fp).getDuration() + " min");
            }
            tvCategory.setText(fp.getCategory());
            tvTime.setText(Methods.calculateTimeAgo(fp.getCreatedAt()));
            if (fp.getImage() != null) {
                ivImage.setVisibility(View.VISIBLE);
                imageUrl = fp.getImage().getUrl();
                Glide.with(context)
                        .load(imageUrl)
                        .fitCenter() // scale image to fill the entire ImageView
                        .transform(new RoundedCornersTransformation(30, 10))
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }
            video = fp.getVideo();
            description = fp.getDescription();
            loc = fp.getLoc();
        }
    }
}
