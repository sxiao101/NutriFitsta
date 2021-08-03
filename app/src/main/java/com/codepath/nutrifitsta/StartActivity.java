package com.codepath.nutrifitsta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import at.markushi.ui.CircleButton;

public class StartActivity extends AppCompatActivity {
    private ImageView ivLogo;
    private CircleButton btEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        ivLogo = findViewById(R.id.ivLogo);
        btEnter = findViewById(R.id.btEnter);
        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}