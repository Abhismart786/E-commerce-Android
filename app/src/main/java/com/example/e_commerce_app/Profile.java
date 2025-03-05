package com.example.e_commerce_app;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private Button btnChangeImage, btnLogout; // Declare the logout button

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnLogout = findViewById(R.id.btnLogout); // Initialize the logout button

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get the current logged-in user
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display user details
            tvUserName.setText(currentUser.getDisplayName());
            tvUserEmail.setText(currentUser.getEmail());
            // If phone number is available, display it
            if (currentUser.getPhoneNumber() != null) {
                tvUserPhone.setText(currentUser.getPhoneNumber());
            }

            // Load profile picture if available
            if (currentUser.getPhotoUrl() != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).into(profileImageView);
            }
        }

        // Handle change image button click
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Handle logout button click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    // Open file chooser to pick a new image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Upload the new profile image to Firebase Storage
            uploadImageToFirebase(imageUri);
        }
    }

    // Upload the selected image to Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        if (currentUser != null) {
            // Create a reference for the image in Firebase Storage
            StorageReference fileReference = storageRef.child("profile_pics/" + currentUser.getUid() + ".jpg");

            // Upload the image
            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get the download URL and update user's profile picture
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(profileImageView);
                    });
                }
            });
        }
    }

    // Handle logout logic
    private void logout() {
        mAuth.signOut(); // Sign the user out

        // Redirect to LoginActivity (or any activity of your choice)
        Intent intent = new Intent(Profile.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the Profile activity to prevent the user from navigating back
    }
}
