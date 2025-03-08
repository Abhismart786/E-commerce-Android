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

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Set up the adapter with the delete listener
        userAdapter = new UserAdapter(userList, new UserAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(AdminView.User user) {
                deleteUser(user);
            }
        });

        usersRecyclerView.setAdapter(userAdapter);
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
                        user.id = snapshot.getKey(); // Set the user ID
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
        // First, delete the user from the Firebase Realtime Database
        usersRef.child(user.id).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After deleting from Realtime Database, also delete the user from Firebase Authentication
                        if (user.email != null) {
                            deleteFirebaseAuthUser(user);
                        } else {
                            Toast.makeText(AdminUserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
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
                        } else {
                            Toast.makeText(AdminUserListActivity.this, "Failed to delete authenticated user", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AdminUserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
