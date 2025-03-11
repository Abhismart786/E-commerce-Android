////package com.example.e_commerce_app;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.ImageView;
////import android.widget.TextView;
////
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.squareup.picasso.Picasso;
////
////import java.util.List;
////
////
////public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
////
////    private List<Product> productList;
////    private Context context;
////
////    // Constructor
////    public ProductAdapter(Context context, List<Product> productList) {
////        this.context = context;
////        this.productList = productList;
////    }
////
////// Override methods
////    @Override
////    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(context).inflate(R.layout.product_items, parent, false);
////        return new ProductViewHolder(view);
////    }
////
////    // Override methods
////    @Override
////    public void onBindViewHolder(ProductViewHolder holder, int position) {
////        Product product = productList.get(position);
////        holder.productName.setText(product.getPname());
////        // Set product image using Picasso or Glide
////        Picasso.get().load(product.getImage()).into(holder.productImage);
////
////        holder.productImage.setOnClickListener(v -> {
////            // Handle item click, for example, add to cart
////        });
////    }
////
////    @Override
////    public int getItemCount() {
////        return productList.size();
////    }
////
////    // ViewHolder class
////    public static class ProductViewHolder extends RecyclerView.ViewHolder {
////        TextView productName;
////        ImageView productImage;
////
////        public ProductViewHolder(View itemView) {
////            super(itemView);
////            productName = itemView.findViewById(R.id.product_name);
////            productImage = itemView.findViewById(R.id.product_image);
////        }
////    }
////}
//package com.example.e_commerce_app;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
//
//    private Context context;
//    private List<Product> productList;
//
//    public ProductAdapter(Context context, List<Product> productList) {
//        this.context = context;
//        this.productList = productList;
//    }
//
//    @Override
//    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // Inflate the layout for individual product items
//        View view = LayoutInflater.from(context).inflate(R.layout.product_items, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//
//        // Bind product data to the views here (TextViews, ImageViews, etc.)
//        holder.productName.setText(product.getPname());
//        Picasso.get().load(product.getImage()).into(holder.productImage);
//
//        // Save the product to SharedPreferences when the image is clicked
//        holder.productImage.setOnClickListener(v -> {
//            // Save the product to SharedPreferences
//            SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            String cartItem = product.getPid() + "," + product.getPname() + "," + product.getImage();
//            String cartItems = sharedPreferences.getString("cart_items", "");
//
//            if (cartItems.isEmpty()) {
//                cartItems = cartItem;
//            } else {
//                cartItems += "," + cartItem;
//            }
//
//            editor.putString("cart_items", cartItems);
//            editor.apply();
//
//            Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//
//        TextView productName;
//        ImageView productImage;
//
//        public ProductViewHolder(View itemView) {
//            super(itemView);
//            productName = itemView.findViewById(R.id.product_name);
//            productImage = itemView.findViewById(R.id.product_image);
//        }
//    }
//}
package com.example.e_commerce_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_items, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the product name and image
        holder.productName.setText(product.getPname());
        Picasso.get().load(product.getImage()).into(holder.productImage);

        // Parse the price as a double (assuming the price is stored as a String in Firebase)
        double price = 0.0;
        try {
            price = Double.parseDouble(product.getPrice());  // Convert String to double
        } catch (NumberFormatException e) {
            // Handle error if price is not a valid number
            Log.e("ProductAdapter", "Invalid price format", e);
        }

        // Format the price as currency and set it to the price TextView
        holder.productPrice.setText(String.format("$%.2f", price));

        // Handle click on product image to add to cart
        holder.productImage.setOnClickListener(v -> {
            addToCart(product);
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        TextView productName;
        ImageView productImage;
        TextView productPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }

    // Add product to cart
    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get existing cart items
        String cartItemsString = sharedPreferences.getString("cart_items", "[]");

        // Check if cartItemsString is a valid JSON array
        try {
            // Try parsing the existing cart items string
            new JSONArray(cartItemsString);
        } catch (JSONException e) {
            // If the value is not a valid JSON array, reset it to "[]"
            Log.e("Cart", "Cart data is corrupted, resetting to empty array.");
            cartItemsString = "[]";
        }

        // Log the cart string before adding a new item
        Log.d("Cart", "Cart before adding: " + cartItemsString);

        try {
            // Parse the cart items into a JSONArray
            JSONArray cartItemsArray = new JSONArray(cartItemsString);

            // Create a new JSONObject for the product to add
            JSONObject productObject = new JSONObject();
            productObject.put("productId", product.getPid());
            productObject.put("productName", product.getPname());
            productObject.put("productImageUrl", product.getImage());
            // Ensure product price is passed correctly when adding to cart
            productObject.put("productPrice", product.getPrice()); // Assuming the product has a method getPrice()

            // Add the product to the cart array
            cartItemsArray.put(productObject);

            // Save the updated cart back to SharedPreferences
            editor.putString("cart_items", cartItemsArray.toString());
            editor.apply();

            // Log the updated cart after adding an item
            Log.d("Cart", "Updated Cart after adding item: " + cartItemsArray.toString());

            // Show confirmation toast
            Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
            ((MainActivity) context).updateCartIcon();  // Update cart icon in MainActivity
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Cart", "Error adding product to cart: " + e.getMessage());
        }
    }


}
