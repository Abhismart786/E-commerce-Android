package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Set up the navigation listener
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                // Navigate to ProfileActivity when profile is selected
                Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                startActivity(profileIntent);
                return true;
            }
            // Handle other navigation cases if needed
            // if (item.getItemId() == R.id.home) { ... }
            // if (item.getItemId() == R.id.search) { ... }

            return false;
        });
    }
}
