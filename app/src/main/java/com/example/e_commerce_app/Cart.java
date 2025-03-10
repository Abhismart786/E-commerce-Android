package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart); // Ensure you have a layout for the cart

        // Initialize RecyclerView
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cart item list and adapter
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        // Load cart items from SharedPreferences
        loadCartItems();
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        String cartItemsString = sharedPreferences.getString("cart_items", "[]");

        Log.d("Cart", "Cart Items String fetched: " + cartItemsString);  // Log the fetched cart items

        if (!cartItemsString.isEmpty() && !cartItemsString.equals("[]")) {
            try {
                // Parsing the cart items into a JSONArray
                JSONArray cartItemsArray = new JSONArray(cartItemsString);

                // Clear any existing items to avoid duplication
                cartItemList.clear();

                // Process each item in the cart
                for (int i = 0; i < cartItemsArray.length(); i++) {
                    JSONObject productObject = cartItemsArray.getJSONObject(i);
                    String productId = productObject.getString("productId");
                    String productName = productObject.getString("productName");
                    String productImageUrl = productObject.getString("productImageUrl");

                    // Create CartItem and add it to the list
                    CartItem cartItem = new CartItem(productId, productName, productImageUrl);
                    cartItemList.add(cartItem);
                }

                // Notify the adapter that the data has changed
                cartAdapter.notifyDataSetChanged();

                // Log the loaded cart items to verify
                Log.d("Cart", "Loaded Cart Items: " + cartItemList.size());
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

}


