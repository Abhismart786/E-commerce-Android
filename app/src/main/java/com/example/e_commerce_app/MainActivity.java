
package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList; // List to store filtered products
    private FirebaseDatabase database;
    private DatabaseReference productRef;
    private SearchView searchView;
    private TextView cartItemCount; // To display cart item count

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("Products"); // Assuming "products" is the node in the Realtime Database

        // Initialize product list and adapter
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>(); // Initialize filtered list
        productAdapter = new ProductAdapter(this, filteredProductList); // Set filtered list for adapter
        categoriesRecyclerView.setAdapter(productAdapter);

        // Initialize SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Optionally, handle what happens when the user submits a search (not needed here)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter products based on the search query
                filterProducts(newText);
                return true;
            }
        });

        // Initialize Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        cartItemCount = findViewById(R.id.cartItemCount); // To show cart item count

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                // Navigate to ProfileActivity when profile is selected
                Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                startActivity(profileIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_purse) {
                Intent profileIntent = new Intent(MainActivity.this, Favourite.class);
                startActivity(profileIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_cart) {
                // Navigate to CartActivity when cart is selected
                Intent profileIntent = new Intent(MainActivity.this, Cart.class);
                startActivity(profileIntent);
                return true;
            }
            return false;
        });

        // Fetch products from Firebase
        fetchProductsFromFirebase();
    }


    private void fetchProductsFromFirebase() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear(); // Clear existing list to avoid duplicates

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product); // Add product to the list
                    }
                }

                // Initially display all products
                filteredProductList.clear();
                filteredProductList.addAll(productList);
                productAdapter.notifyDataSetChanged();

                // Update cart icon with the current cart count
                updateCartIcon();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter products based on the search query
    private void filterProducts(String query) {
        filteredProductList.clear(); // Clear the filtered list

        if (query.isEmpty()) {
            // If query is empty, show all products
            filteredProductList.addAll(productList);
        } else {
            // Filter products by name (or any other field you want to filter by)
            for (Product product : productList) {
                if (product.getPname().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product); // Add matching products to filtered list
                }
            }
        }

        // Notify the adapter that data has changed
        productAdapter.notifyDataSetChanged();
    }

    // Update cart icon count
    public void updateCartIcon() {
        int cartItemCount = getCartItemCount();
        Log.d("Cart", "Updated Cart Item Count: " + cartItemCount);

        // Update cart item count TextView in the BottomNavigationView
        TextView cartItemCountTextView = findViewById(R.id.cartItemCount);
        cartItemCountTextView.setText(String.valueOf(cartItemCount));
    }

    public int getCartItemCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        String cartItemsString = sharedPreferences.getString("cart_items", "[]");

        Log.d("Cart", "Cart Items String: " + cartItemsString);  // Log the string from SharedPreferences

        try {
            // Parse the cart items string into a JSONArray
            JSONArray cartItemsArray = new JSONArray(cartItemsString);
            return cartItemsArray.length();  // Return the number of items in the cart
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // Return 0 if there's an error or no items in cart
        }
    }


}
