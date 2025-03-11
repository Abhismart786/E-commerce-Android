//package com.example.e_commerce_app;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.squareup.picasso.Picasso;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
//
//    private Context context;
//    private List<CartItem> cartItems; // List to hold cart items
//
//    public CartAdapter(Context context, List<CartItem> cartItems) {
//        this.context = context;
//        this.cartItems = cartItems;
//    }
//
//    @Override
//    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
//        return new CartViewHolder(view);
//    }
//
//
//
//
//    @Override
//    public void onBindViewHolder(CartViewHolder holder, int position) {
//        CartItem cartItem = cartItems.get(position);
//        holder.productName.setText(cartItem.getProductName());
//        Picasso.get().load(cartItem.getProductImageUrl()).into(holder.productImage);
//
//        // Get the product price
//        double productPrice = cartItem.getProductPrice();
//
//        // Log the price
//        Log.d("CartAdapter", "Product: " + cartItem.getProductName() + " | Price: " + productPrice);
//
//        // Check if productPrice is valid
//        if (productPrice > 0) {
//            // Valid price, format it correctly
//            holder.productPrice.setText(String.format("Price: $%.2f", productPrice));
//        } else {
//            // Handle invalid price (NaN or 0)
//            holder.productPrice.setText("Price: Not Available");
//        }
//
//        // Display quantity
//        holder.productQuantity.setText("Quantity: " + cartItem.getProductQuantity());
//    }
//
//
//
//    @Override
//    public int getItemCount() {
//        return cartItems.size();
//    }
//
//    public static class CartViewHolder extends RecyclerView.ViewHolder {
//
//        TextView productName;
//        ImageView productImage;
//        TextView productPrice;
//        TextView productQuantity;
//
//        public CartViewHolder(View itemView) {
//            super(itemView);
//            productName = itemView.findViewById(R.id.product_name);
//            productImage = itemView.findViewById(R.id.product_image);
//            productPrice = itemView.findViewById(R.id.product_price);
//            productQuantity = itemView.findViewById(R.id.product_quantity);
//        }
//    }
//
//    // Load cart items from SharedPreferences
//    public static List<CartItem> loadCartItems(Context context) {
//        List<CartItem> cartItemList = new ArrayList<>();
//        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
//        String cartItemsString = sharedPreferences.getString("cart_items", "[]");
//
//        try {
//            JSONArray cartItemsArray = new JSONArray(cartItemsString);
//            for (int i = 0; i < cartItemsArray.length(); i++) {
//                JSONObject itemObject = cartItemsArray.getJSONObject(i);
//                String productId = itemObject.getString("productId");
//                String productName = itemObject.getString("productName");
//                String productImageUrl = itemObject.getString("productImageUrl");
//                double productPrice = itemObject.optDouble("productPrice", 0.0);
//                CartItem cartItem = new CartItem(productId, productName, productImageUrl,productPrice);
//
//                cartItemList.add(cartItem);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return cartItemList;
//    }
//}
package com.example.e_commerce_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText("$" + cartItem.getProductPrice());
        holder.productQuantity.setText(String.valueOf(cartItem.getProductQuantity()));
        Picasso.get().load(cartItem.getProductImageUrl()).into(holder.productImageView);
        // Set increment/decrement button listeners
        holder.incrementButton.setOnClickListener(v -> {
            int quantity = cartItem.getProductQuantity();
            cartItem.setQuantity(quantity + 1);  // Increment the quantity
            notifyItemChanged(position);
            ((Cart) context).updateTotalPrice();  // Update the total price
        });

        holder.decrementButton.setOnClickListener(v -> {
            int quantity = cartItem.getProductQuantity();
            if (quantity > 1) {
                cartItem.setQuantity(quantity - 1);  // Decrement the quantity
                notifyItemChanged(position);
                ((Cart) context).updateTotalPrice();  // Update the total price
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            // Remove the item from the cart list
            cartItemList.remove(position);
            notifyItemRemoved(position);

            // Update the shared preferences by saving the updated cart list


            // Update the total price
            ((Cart) context).updateTotalPrice();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {


        TextView productName, productPrice, productQuantity;
        Button incrementButton, decrementButton, deleteButton;
        ImageView productImageView;
        @SuppressLint("WrongViewCast")
        public CartViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);

            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            productImageView = itemView.findViewById(R.id.product_image);
        }
    }
}
