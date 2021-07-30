package com.codepath.nutrifitsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved, parent, false);
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
        private ImageView ivImage;
        private TextView tvUsername;
        private TextView tvCategory;
        private TextView tvDetails;
        private ImageView ivProfile;
        private RelativeLayout border;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            border = itemView.findViewById(R.id.border);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    bundle.putString("postId", post.getObjectId());
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
                border.setBackground(ContextCompat.getDrawable(context, R.drawable.card_edge));
                tvDetails.setText(((FoodPost)fp).getNutrition() + " cal");
            }
            if (fp instanceof FitnessPost) {
                border.setBackground(ContextCompat.getDrawable(context, R.drawable.card_edge2));
                tvDetails.setText(((FitnessPost)fp).getDuration() + " min");
            }
            try {
                tvUsername.setText(fp.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvCategory.setText(fp.getCategory());
            Glide.with(context)
                    .load(fp.getUser().getParseFile("pfp").getUrl())
                    .circleCrop()
                    .into(ivProfile);
            Glide.with(context)
                    .load(fp.getImage().getUrl())
                    .fitCenter() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(ivImage);
        }
    }
}
