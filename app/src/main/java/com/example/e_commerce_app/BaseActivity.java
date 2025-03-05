package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Initialize Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Set up the navigation listener
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                // Navigate to ProfileActivity when profile is selected
                Intent profileIntent = new Intent(BaseActivity.this, Profile.class);
                startActivity(profileIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_home) {
                // Navigate to Home Activity (MainActivity)
                Intent homeIntent = new Intent(BaseActivity.this, MainActivity.class);
                startActivity(homeIntent);
                return true;
            }
            // Handle other navigation cases if needed

            return false;
        });
    }
}
