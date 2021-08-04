package com.codepath.nutrifitsta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nutrifitsta.databinding.ActivityLoginBinding;
import com.codepath.nutrifitsta.databinding.ActivitySignUpBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.royrodriguez.transitionbutton.TransitionButton;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.tvSignUp.setPaintFlags(binding.tvSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                binding.btnLogin.startAnimation();

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
                            loginUser(username, password);
                        } else {
                            binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    Log.e(TAG, "Issue with login: "+ e.getMessage()); // change to a pop up
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
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
}