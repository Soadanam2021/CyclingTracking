package com.example.cyclingtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds splash screen duration
    private ProgressBar progressBar; // Declare ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  // Ensure correct layout

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Show the ProgressBar while the splash screen is visible
        progressBar.setVisibility(View.VISIBLE);

        // Handler to delay the transition to the main activity
        new Handler().postDelayed(() -> {
            // Hide the ProgressBar after delay
            progressBar.setVisibility(View.GONE);

            // Start the MainActivity after the splash screen
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish(); // Close the splash activity
        }, SPLASH_TIME_OUT); // Delay duration (3 seconds)
    }
}
