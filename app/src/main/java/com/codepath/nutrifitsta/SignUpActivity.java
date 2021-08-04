package com.codepath.nutrifitsta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nutrifitsta.databinding.ActivityLoginBinding;
import com.codepath.nutrifitsta.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    private static ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = (Toolbar) binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                Log.i(TAG, "onCLick signup button");
                binding.btnRegister.startAnimation();
                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = true;
                        // Choose a stop animation if your call was succesful or not
                        if (isSuccessful) {
                            String username = binding.etUsername.getText().toString();
                            String password = binding.etPassword.getText().toString();
                            String password2 = binding.etPassword2.getText().toString();
                            if (!(password.equals(password2))) {
                                binding.btnRegister.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                            } else {
                                signUpUser(username, password);
                            }
                        } else {
                            binding.btnRegister.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }


    private void signUpUser(String username, String password) {
        Log.i(TAG, "Attempting to sign up user" + username);
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    binding.btnRegister.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    Log.e(TAG, "Issue with sign up", e);
                    Toast.makeText(SignUpActivity.this, "Issue with Sign Up!", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.btnRegister.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                    @Override
                    public void onAnimationStopEnd() {
                        startMainActivity();
                    }
                });
            }
        });
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, LoginActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}