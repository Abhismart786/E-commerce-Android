package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
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
    private TextView totalPriceTextView;  // To show the total price

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Set up the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
            getSupportActionBar().setTitle("Your Cart");  // Optional: Set the title for the action bar
        }

        // Initialize RecyclerView and total price TextView
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTextView = findViewById(R.id.totalPriceTextView);  // Add this to your layout

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

        Log.d("Cart", "Cart Items String fetched: " + cartItemsString);

        if (!cartItemsString.isEmpty() && !cartItemsString.equals("[]")) {
            try {
                JSONArray cartItemsArray = new JSONArray(cartItemsString);
                cartItemList.clear();
                for (int i = 0; i < cartItemsArray.length(); i++) {
                    JSONObject productObject = cartItemsArray.getJSONObject(i);
                    String productId = productObject.getString("productId");
                    String productName = productObject.getString("productName");
                    String productImageUrl = productObject.getString("productImageUrl");
                    double productPrice = productObject.optDouble("productPrice", 0.0);
                    int quantity = productObject.optInt("quantity", 1);  // Get the quantity, default is 1

                    // Log the product and its price
                    Log.d("Cart", "Loaded product: " + productName + " | Price: " + productPrice + " | Quantity: " + quantity);

                    CartItem cartItem = new CartItem(productId, productName, productImageUrl, productPrice, quantity);
                    cartItemList.add(cartItem);
                }

                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();  // Update the total price after loading the items
                Log.d("Cart", "Loaded Cart Items: " + cartItemList.size());
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the total price
    public void updateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItemList) {
            totalPrice += cartItem.getTotalPrice();  // Add each item's total price
        }

        totalPriceTextView.setText("Total: $" + totalPrice);  // Display the total price
    }

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

    // Method to clear cart data when user signs in
    public static void clearCartData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear the cart items
        editor.clear();
        editor.apply();

        Log.d("Cart", "Cart has been cleared for the new user.");
    }
}
