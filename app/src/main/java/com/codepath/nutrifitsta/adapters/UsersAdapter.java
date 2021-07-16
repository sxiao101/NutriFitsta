package com.codepath.nutrifitsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.R;
import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    private Context context;
    private List<ParseUser> users;

    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfile = itemView.findViewById(R.id.ivProfile);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    bundle.putString("user", post.getUser().getObjectId());
                    ProfileFragment profile = new ProfileFragment();
                    profile.setArguments(bundle);

                    ((MainActivity) context).switchContent(R.id.flContainer, profile);
                }
            });*/

        }

        public void bind(ParseUser user) {
            Glide.with(context)
                    .load(user.getParseFile("pfp").getUrl())
                    .circleCrop()
                    .into(ivProfile);
            tvUsername.setText(user.getUsername());
        }

    }

}
