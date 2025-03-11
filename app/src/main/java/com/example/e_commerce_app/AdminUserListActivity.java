package com.example.e_commerce_app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<AdminView.User> userList;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        // Set up Action Bar (back navigation)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User List");  // Set a title for the activity
        }

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Set up RecyclerView
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, new UserAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(AdminView.User user) {
                deleteUser(user);
            }
        });

        usersRecyclerView.setAdapter(userAdapter);

        // Fetch users from the Firebase Realtime Database
        fetchUsers();
    }

    private void fetchUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AdminView.User user = snapshot.getValue(AdminView.User.class);
                    if (user != null) {
                        user.id = snapshot.getKey();  // Set the user ID from Firebase
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminUserListActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(AdminView.User user) {
        // Show a progress indicator
        Toast.makeText(this, "Deleting user...", Toast.LENGTH_SHORT).show();

        // Delete the user from Firebase Realtime Database
        usersRef.child(user.id).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If the deletion from Realtime Database is successful, proceed with Firebase Authentication deletion if needed
                        if (user.email != null) {
                            deleteFirebaseAuthUser(user);
                        } else {
                            // User was deleted only from the Realtime Database
                            Toast.makeText(AdminUserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            refreshUserList();
                        }
                    } else {
                        // Handle failure in deleting from Realtime Database
                        Toast.makeText(AdminUserListActivity.this, "Failed to delete user from database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFirebaseAuthUser(AdminView.User user) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null && user.email.equals(firebaseUser.getEmail())) {
            // If the user is authenticated, delete the Firebase Authentication user
            firebaseUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminUserListActivity.this, "Authenticated user deleted", Toast.LENGTH_SHORT).show();
                            refreshUserList();  // Refresh user list after successful deletion
                        } else {
                            Toast.makeText(AdminUserListActivity.this, "Failed to delete authenticated user", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // The user was deleted from the Realtime Database but not Firebase Authentication
            Toast.makeText(AdminUserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            refreshUserList();
        }
    }

    // Method to refresh the user list after deletion
    private void refreshUserList() {
        // Fetch the users again to reflect the updated list
        fetchUsers();
    }

    // Handle back navigation in action bar


}
