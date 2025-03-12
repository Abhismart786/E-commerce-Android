package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity extends AppCompatActivity {

    private Button userViewButton;
    private Button addProductButton;
    private Button bb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        // Initialize buttons
        userViewButton = findViewById(R.id.userViewButton);
        addProductButton = findViewById(R.id.addProductButton);

        bb = findViewById(R.id.bb);
        // Initialize the manual back button and set its click listener

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the UserViewActivity
                Intent intent = new Intent(Activity.this, AdminView.class);
                startActivity(intent);
            }
        });
                // Set up the listener for the User View button
                userViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start the UserViewActivity
                        Intent intent = new Intent(Activity.this, AdminUserListActivity.class);
                        startActivity(intent);
                    }
                });

                // Set up the listener for the Add Product button
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start the AddProductActivity
                        Intent intent = new Intent(Activity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                    }
                });
            }

        }
