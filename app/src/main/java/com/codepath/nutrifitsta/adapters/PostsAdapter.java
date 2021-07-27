package com.codepath.nutrifitsta.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.codepath.nutrifitsta.fragments.ProfileFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Post> posts;
    private List<Post> postsAll;
    private static final String TAG = "PostsAdapter";

    public PostsAdapter(Context context, List<Post> posts, List<Post> postsAll) {
        this.context = context;
        this.posts = posts;
        this.postsAll = postsAll;
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        // runs on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filt = constraint.toString();
            List<Post> filteredPosts = new ArrayList<>();
            if (filt.isEmpty()) {
                filteredPosts.addAll(postsAll);
            } else {
                for (Post post : postsAll) {
                    if (post.getType().equals(filt)) {
                        filteredPosts.add(post);
                    }
                }
                Log.i("Posts", ("" + filteredPosts.size()));
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredPosts;
            return filterResults;
        }
        // runs on a ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            posts.clear();
            posts.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvType, tvDescription, tvUsername, tvCategory, tvDetails, tvLocation, tvVideo, tvTime;
        private ImageView ivImage, ivProfile, locPointer;

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
            tvTime = itemView.findViewById(R.id.tvTime);
            locPointer = itemView.findViewById(R.id.locPointer);
            ivProfile = itemView.findViewById(R.id.ivProfile);

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
                tvType.setText("FOOD");
                tvType.setTextColor(Color.parseColor("#F4B18C"));
                tvDetails.setText(((FoodPost)fp).getNutrition() + " cal");
            }
            if (fp instanceof FitnessPost) {
                tvType.setText("FITNESS");
                tvType.setTextColor(Color.parseColor("#3B9778"));
                tvDetails.setText(((FitnessPost)fp).getDuration() + " min");
            }
            try {
                tvUsername.setText(fp.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDescription.setText(fp.getDescription());
            tvCategory.setText(fp.getCategory());
            tvTime.setText(Methods.calculateTimeAgo(fp.getCreatedAt()));
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
            // for rounded corners
            int radius = 30;
            int margin = 10;
            Glide.with(context)
                    .load(fp.getImage().getUrl())
                    .fitCenter() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivImage);
        }
    }

}
