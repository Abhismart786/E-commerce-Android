package com.example.e_commerce_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPassword extends AppCompatActivity {

    private Button loginRedirectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        loginRedirectButton = findViewById(R.id.loginRedirectButton);

        // Redirect to LoginActivity when the "Back to Login" button is clicked
        loginRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Login Activity
                Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
