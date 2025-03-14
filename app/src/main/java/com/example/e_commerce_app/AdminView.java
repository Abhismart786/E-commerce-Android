package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminView extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signupRedirect;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAdminsRef;

    private static final String ADMIN_EMAIL = "abhi@gmail.com"; // Admin email
    private static final String ADMIN_PASSWORD = "abhi6239$"; // Fixed password for admin

    // Firebase Authentication and Database references
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAdminsRef = mDatabase.getReference("admins");  // Reference to "admins" node

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);

        // Login button logic
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AdminView.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email is the fixed admin email
                if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                    // If the email and password match the fixed admin credentials
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Directly save admin details to the database without Firebase Auth sign-in
                        saveAdminDetails(user);
                    } else {
                        // If no user is logged in, we don't have to do Firebase Auth here
                        Toast.makeText(AdminView.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminView.this, Activity.class));
                        finish();
                    }
                } else {
                    // Use Firebase Authentication to validate user credentials (if not using a fixed password)
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AdminView.this, task -> {
                                if (task.isSuccessful()) {
                                    // If login is successful with Firebase, save details to database
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        saveUserDetails(user);  // Save user details to Realtime Database
                                    }
                                } else {
                                    // If authentication fails, show an error message
                                    Toast.makeText(AdminView.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Redirect to SignUpActivity
        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to save admin details to the database
    private void saveAdminDetails(FirebaseUser user) {
        String adminId = user.getUid();  // Get the admin's UID
        String email = user.getEmail();
        String name = "Admin";  // You can customize this if needed
        String role = "admin";

        // Create an admin object
        Admin adminDetails = new Admin(name, email, role);

        // Save the admin details in Firebase Realtime Database under the "admins" node
        mAdminsRef.child(adminId).setValue(adminDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminView.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminView.this, Activity.class)); // Redirect to Admin Category Activity
                        finish();
                    } else {
                        Toast.makeText(AdminView.this, "Failed to save admin details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to save user details to the database
    private void saveUserDetails(FirebaseUser user) {
        String userId = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName() != null ? user.getDisplayName() : "Unknown User";
        String role = "user";

        // Create a user object
        User userDetails = new User(name, email, role);

        // Save the user details in Firebase Realtime Database
        mDatabase.getReference("users").child(userId).setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminView.this, "User details saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminView.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Admin class to represent the admin object that will be stored in Firebase
    public static class Admin {
        public String name;
        public String email;
        public String role;

        // No-argument constructor required for Firebase
        public Admin() {
            // Firebase needs this no-argument constructor
        }

        public Admin(String name, String email, String role) {
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }

    // User class to represent the user object that will be stored in Firebase
    public static class User {
        public String name;
        public String email;
        public String role;
        public String id;

        // No-argument constructor required for Firebase
        public User() {
            // Firebase needs this no-argument constructor
        }

        public User(String name, String email, String role) {
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }
}
