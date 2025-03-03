package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton;
    private Button loginRedirectButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);

        // Handle password reset
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgetPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Inform the user that the email was sent successfully
                                Toast.makeText(ForgetPassword.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle error (e.g. user not found)
                                Toast.makeText(ForgetPassword.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Redirect to LoginActivity when the "Back to Login" button is clicked
        loginRedirectButton.setOnClickListener(v -> {
            // Redirect to Login Activity
            Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
