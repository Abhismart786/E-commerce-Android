
package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Cart extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalPriceTextView;
    private Button payButton; // Button for initiating the payment
    private Stripe stripe;
    private PaymentSheet paymentSheet;
    private String EphericalKey;
    private String Amount = "20000";  // Amount in cents
    private String Currency = "usd";
    private String CustomersURL = "https://api.stripe.com/v1/customers";
    private String EphericalKeyURL = "https://api.stripe.com/v1/ephemeral_keys";
    private String ClientSecretURL = "https://api.stripe.com/v1/payment_intents";

    private String CustomerId = null; // Customer ID from backend
    private String ephemeralKey; // Ephemeral Key from backend
    private String clientSecret; // Client Secret from backend

    private String publishableKey = "pk_test_51QZiSj026qNc6iTxNcz5qYc4ixnZIDwfbXXdS5Xgeq2LEVdJhq1i4jWIj7nzPnv6WdicHkdwgIFDhCDSpv9HkAjP00GlltXFm1";  // Replace with your Publishable Key
    private String secretKey = "sk_test_51QZiSj026qNc6iTx7wy3YLmHjBP5NH3oZKsTDT7yMUbDYIeqRlie0B4rAI02pqDtPnm9UvVL0X9uqZJKJHJR7z4r00MNHSvf6J";  // Replace with your Secret Key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Stripe SDK with your Publishable Key (in test mode)
        PaymentConfiguration.init(getApplicationContext(), publishableKey);
        stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // Set up the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
            getSupportActionBar().setTitle("Your Cart");
        }

        // Initialize RecyclerView and total price TextView
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        payButton = findViewById(R.id.paymentButton);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Initialize cart item list and adapter
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        // Load cart items from SharedPreferences
        loadCartItems();

        // Handle Pay button click
        payButton.setOnClickListener(view -> {
            if (!cartItemList.isEmpty()) {
                createCustomerAndPaymentIntent(); // Create customer and proceed with payment
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load cart items from SharedPreferences
    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        String cartItemsString = sharedPreferences.getString("cart_items", "[]");

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
                    int quantity = productObject.optInt("quantity", 1);

                    CartItem cartItem = new CartItem(productId, productName, productImageUrl, productPrice, quantity);
                    cartItemList.add(cartItem);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }


    public void onErrorResponse(VolleyError error) {
        if (error.networkResponse != null) {
            String response = new String(error.networkResponse.data);
            Log.e("StripeError", "Error: " + error.getMessage());
            Log.e("StripeError", "Response: " + response);  // Log the full response body
        } else {
            Log.e("StripeError", "Error: " + error.getMessage());
        }
        Toast.makeText(Cart.this, "Error creating payment intent: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // Method to update the total price
    void updateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItemList) {
            totalPrice += cartItem.getTotalPrice();
        }
        totalPriceTextView.setText("Total: $" + totalPrice);
    }

    // Method to create a new customer, fetch ephemeral key, and create payment intent
    private void createCustomerAndPaymentIntent() {
        createCustomer();
    }

    // Method to create the customer on your backend
    private void createCustomer() {
        StringRequest request = new StringRequest(Request.Method.POST, CustomersURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    CustomerId = object.getString("id");
                    Log.d("Stripe", "Customer created: " + CustomerId);
                    Toast.makeText(Cart.this, "Customer ID: " + CustomerId, Toast.LENGTH_SHORT).show();

                    // Now that CustomerId is available, fetch the Ephemeral Key
                    if (CustomerId != null && !CustomerId.isEmpty()) {
                        getEphericalKey();
                    } else {
                        Toast.makeText(Cart.this, "Failed to create customer", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Cart.this, "Error creating customer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cart.this, "Error creating customer: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    // Method to fetch the ephemeral key from your backend
    private void getEphericalKey() {
        StringRequest request = new StringRequest(Request.Method.POST, EphericalKeyURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    EphericalKey = object.getString("id");
                    Log.d("Stripe", "Ephemeral Key created: " + EphericalKey);

                    // Now get the Client Secret after the Ephemeral Key is fetched
                    if (EphericalKey != null && !EphericalKey.isEmpty()) {
                        getClientSecret(CustomerId, EphericalKey);
                    } else {
                        Toast.makeText(Cart.this, "Failed to fetch ephemeral key", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Cart.this, "Error fetching ephemeral key: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cart.this, "Error fetching ephemeral key: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                headers.put("Stripe-Version", "2022-11-15");
                return headers;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    // Method to create the payment intent on your backend
    private void getClientSecret(String customerId, String ephemeralKey) {
        StringRequest request = new StringRequest(Request.Method.POST, ClientSecretURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    clientSecret = object.getString("client_secret");
                    Log.d("Stripe", "Client Secret created: " + clientSecret);
                    Toast.makeText(Cart.this, "Client Secret: " + clientSecret, Toast.LENGTH_SHORT).show();

                    // After receiving the client secret, call the payment flow
                    paymentFlow();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Cart.this, "Error fetching client secret: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cart.this, "Error fetching client secret: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                return headers;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", Amount);
                params.put("currency", Currency);
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    public static void clearCartData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clears all the data in SharedPreferences
        editor.apply();  // Save the changes
    }
    // Method to present the payment sheet
    private void paymentFlow() {
        if (clientSecret != null && !clientSecret.isEmpty()) {
            paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("Stripe", new PaymentSheet.CustomerConfiguration(
                    CustomerId, EphericalKey // Ensure both are non-null and valid
            )));
        } else {
            Toast.makeText(Cart.this, "Client Secret not available", Toast.LENGTH_SHORT).show();
        }
    }


    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
