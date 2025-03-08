//package com.example.e_commerce_app;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class Profile extends AppCompatActivity {
//
//    private CircleImageView profileImageView;
//    private TextView tvUserName, tvUserEmail, tvUserPhone;
//    private Button btnChangeImage, btnLogout;
//
//    private FirebaseAuth mAuth;
//    private FirebaseUser currentUser;
//    private FirebaseStorage storage;
//    private StorageReference storageRef;
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        // Enable the "Up" button (Back button in the ActionBar)
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        // Initialize the views
//        profileImageView = findViewById(R.id.profileImageView);
//        tvUserName = findViewById(R.id.tvUserName);
//        tvUserEmail = findViewById(R.id.tvUserEmail);
//        tvUserPhone = findViewById(R.id.tvUserPhone);
//        btnChangeImage = findViewById(R.id.btnChangeImage);
//        btnLogout = findViewById(R.id.btnLogout);
//
//        mAuth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageRef = storage.getReference();
//
//        // Get the current user
//        currentUser = mAuth.getCurrentUser();
//
//        // If the user is logged in, update the UI with their details
//        if (currentUser != null) {
//            tvUserName.setText(currentUser.getDisplayName());
//            tvUserEmail.setText(currentUser.getEmail());
//            if (currentUser.getPhoneNumber() != null) {
//                tvUserPhone.setText(currentUser.getPhoneNumber());
//            }
//
//            // Load the profile picture using Picasso (if available)
//            if (currentUser.getPhotoUrl() != null) {
//                Picasso.get().load(currentUser.getPhotoUrl()).into(profileImageView);
//            }
//        }
//
//        // Set up listeners
//        btnChangeImage.setOnClickListener(v -> openFileChooser());
//        btnLogout.setOnClickListener(v -> logout());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
//            Uri imageUri = data.getData();
//            uploadImageToFirebase(imageUri);
//        }
//    }
//
//    private void uploadImageToFirebase(Uri imageUri) {
//        if (currentUser != null) {
//            // Create a reference to store the image in Firebase Storage
//            StorageReference fileReference = storageRef.child("profile_pics/" + currentUser.getUid() + ".jpg");
//
//            // Upload the image to Firebase Storage
//            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // Get the download URL after upload
//                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
//                        // Load the updated profile image using Picasso
//                        Picasso.get().load(uri).into(profileImageView);
//                    });
//                }
//            });
//        }
//    }
//
//    private void logout() {
//        // Sign out the user and navigate to the login screen
//        mAuth.signOut();
//        Intent intent = new Intent(Profile.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    // Handle the "Up" button (back navigation) click
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // When the user clicks the up (back) button, return to the previous screen
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
package com.example.e_commerce_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView tvUserName, tvUserEmail;
    private Button btnChangeImage, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable the "Up" button (Back button in the ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize the views
        profileImageView = findViewById(R.id.profileImageView);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get the current user
        currentUser = mAuth.getCurrentUser();

        // If the user is logged in, update the UI with their details
        if (currentUser != null) {
            tvUserName.setText(currentUser.getDisplayName());
            tvUserEmail.setText(currentUser.getEmail());

            // Load the profile picture using Picasso (if available)
            loadUserProfile();
        }

        // Set up listeners
        btnChangeImage.setOnClickListener(v -> openFileChooser());
        btnLogout.setOnClickListener(v -> logout());
    }

    // Load the user's profile image from Firebase Realtime Database
    private void loadUserProfile() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the profile image URL from the database
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    if (profileImageUrl != null) {
                        // Use Picasso to load the image into the ImageView
                        Picasso.get().load(profileImageUrl).into(profileImageView);
                    }
                }
            }




            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error loading profile image: " + databaseError.getMessage());
            }
        });
    }

    // Open the file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    // Upload the selected image to Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        if (currentUser != null) {
            // Create a reference to store the image in Firebase Storage
            StorageReference fileReference = storageRef.child("profile_pics/" + currentUser.getUid() + ".jpg");

            // Upload the image to Firebase Storage
            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get the download URL after upload
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString(); // Get the image URL
                        saveProfileImageUrl(imageUrl);    // Save the URL in Firebase Realtime Database
                    });
                }
            });
        }
    }

// Save the profile image URL in Firebase Realtime Database
    private void saveProfileImageUrl(String imageUrl) {
        // Get a reference to the Realtime Database "users" node
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

        // Create a map to store user data (including the profile picture URL)
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("profileImageUrl", imageUrl);

        // Store the profile image URL under the current user's UID
        database.child(currentUser.getUid()).updateChildren(userProfile)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the profile image URL
                    Log.d("Firebase", "Profile picture updated.");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Firebase", "Failed to update profile picture: " + e.getMessage());
                });
    }

    private void logout() {
        // Sign out the user and navigate to the login screen
        mAuth.signOut();
        Intent intent = new Intent(Profile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Handle the "Up" button (back navigation) click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // When the user clicks the up (back) button, return to the previous screen
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
