package com.codepath.nutrifitsta.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.nutrifitsta.ComposeActivity;
import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.classes.FitnessPost;
import com.codepath.nutrifitsta.classes.FoodPost;
import com.codepath.nutrifitsta.classes.IPost;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.codepath.nutrifitsta.classes.PostActions;
import com.codepath.nutrifitsta.databinding.FragmentDetailsBinding;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsFragment extends Fragment {
    public static final String TAG = "DetailsCompose";
    private static View v;
    private List<String> items;
    private int totalCal;

    private String postId;
    private PostActions like;
    private PostActions save;
    private Post currPost;

    AnimatedVectorDrawable avd;
    FragmentDetailsBinding binding;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setVisibility(View.GONE);
        v = view;
        items = new ArrayList<>();

        // double tap gesture to like
        final Drawable drawable = binding.likeLogo.getDrawable();
        binding.ivImage.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    binding.likeLogo.setAlpha(0.70f);
                    avd = (AnimatedVectorDrawable) drawable;
                    avd.start();
                    if (!binding.ibLike.isSelected()) {
                        savePost();
                    }
                    return super.onDoubleTapEvent(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        Bundle bundle = this.getArguments();
        postId = bundle.getString("postId");
        getPost(postId); // sets up the view
        setButtons(); // shows correct status of like/save

        RelativeLayout likeBar = view.findViewById(R.id.likeBar);
        likeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.ibLike.isSelected()) { //like it and update Parse database
                    savePost();
                } else {
                    like.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            like = null;
                            binding.ibLike.setImageResource(R.drawable.ic_baseline_offline_bolt_24);
                            binding.tvMessage.setTypeface(null, Typeface.NORMAL);
                            binding.ibLike.setSelected(false);
                        }
                    });
                }
            }
        });

        binding.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.ibSave.isSelected()) { //save it and make post
                    PostActions p = new PostActions();
                    p.setUser(ParseUser.getCurrentUser());
                    p.setPostId(postId);
                    p.setAction(2);
                    p.setPost(currPost);
                    save = p;
                    p.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            binding.ibSave.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                            binding.ibSave.setSelected(true);
                        }
                    });
                } else {
                    save.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            save = null;
                            binding.ibSave.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                            binding.ibSave.setSelected(false);
                        }
                    });
                }
            }
        });

        binding.btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty()) {
                    openDialog(items);
                } else {
                    Toast.makeText(getContext(), "No List Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDialog(List<String> items) {
        ViewDetailsDialog dialog = ViewDetailsDialog.newInstance(items, totalCal);
        dialog.setTargetFragment(DetailsFragment.this, 1);
        dialog.show(getFragmentManager(), "ViewDetailsDialog");
    }

    private void savePost() {
        PostActions p = new PostActions();
        p.setUser(ParseUser.getCurrentUser());
        p.setPostId(postId);
        p.setAction(1);
        p.setPost(currPost);
        like = p;
        p.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                binding.ibLike.setImageResource(R.drawable.liked_bolt);
                binding.tvMessage.setTypeface(Typeface.DEFAULT_BOLD);
                binding.ibLike.setSelected(true);
            }
        });
    }

    private void getPost(String postId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_FOOD);
        query.include(Post.KEY_FIT);
        query.whereEqualTo("objectId", postId);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    currPost = objects.get(0);
                    loadPost(currPost.getType());
                }
            }
        });
    }
    private void loadPost(String type) {
        if (type.equals("food")) {
            try {
                loadPostDetails(currPost.getFood().fetchIfNeeded());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                loadPostDetails(currPost.getFitness().fetchIfNeeded());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPostDetails(IPost fp) {
        if (fp instanceof FoodPost) {
            binding.tvType.setText("FOOD");
            binding.tvType.setTextColor(Color.parseColor("#8BC34A"));
            binding.btnList.setText("Get Ingredients");
            binding.tvDetails.setText(((FoodPost)fp).getNutrition() + " cal");
            totalCal = ((FoodPost)fp).getNutrition();
        }
        if (fp instanceof FitnessPost) {
            binding.tvType.setText("FITNESS");
            binding.tvType.setTextColor(Color.parseColor("#3F51B5"));
            binding.btnList.setText("Get Workout");
            binding.tvDetails.setText(((FitnessPost)fp).getDuration() + " min");
            totalCal = ((FitnessPost)fp).getCal();
        }
        try {
            binding.tvUsername.setText(fp.getUser().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if(fp.getList() != null) {
                items.addAll(fp.getList());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.tvDescription.setText(fp.getDescription());
        binding.tvCategory.setText(fp.getCategory());
        binding.tvTime.setText(Methods.calculateTimeAgo(fp.getCreatedAt()));
        if (fp.getLoc() != null) {
            binding.locPointer.setVisibility(View.VISIBLE);
            binding.tvLocation.setText(fp.getLoc());
        } else {
            binding.tvLocation.setText("");
            binding.locPointer.setVisibility(View.GONE);
        }
        if (fp.getVideo() != null) {
            binding.tvVideo.setText(fp.getVideo());
        }
        Glide.with(getContext())
                .load(fp.getUser().getParseFile("pfp").getUrl())
                .circleCrop()
                .into(binding.ivProfile);
        Glide.with(getContext())
                .load(fp.getImage().getUrl())
                .fitCenter()
                .transform(new RoundedCornersTransformation(30, 10))
                .into(binding.ivImage);


        v.setVisibility(View.VISIBLE);
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
                    binding.ibSave.setImageResource(R.drawable.baseline_bookmark_border_black_24dp);
                    binding.ibSave.setSelected(false);
                    binding.ibLike.setImageResource(R.drawable.ic_baseline_offline_bolt_24);
                    binding.tvMessage.setTypeface(null, Typeface.NORMAL);
                    binding.ibLike.setSelected(false);
                } else {
                    for (PostActions pa: objects) {
                        if (pa.getAction() == 1) {
                            like = pa;
                            binding.ibLike.setImageResource(R.drawable.liked_bolt);
                            binding.tvMessage.setTypeface(Typeface.DEFAULT_BOLD);
                            binding.ibLike.setSelected(true);
                        } else if (pa.getAction() == 2) {
                            save = pa;
                            binding.ibSave.setImageResource(R.drawable.baseline_bookmark_black_24dp);
                            binding.ibSave.setSelected(true);
                        }
                    }
                }
            }
        });
    }

}