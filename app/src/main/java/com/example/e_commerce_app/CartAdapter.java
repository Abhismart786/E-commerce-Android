package com.example.e_commerce_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<CartItem> cartItems; // List to hold cart items

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getProductName());
        Picasso.get().load(cartItem.getProductImageUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView productImage;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

    // Load cart items from SharedPreferences
    public static List<CartItem> loadCartItems(Context context) {
        List<CartItem> cartItemList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        String cartItemsString = sharedPreferences.getString("cart_items", "[]");

        try {
            JSONArray cartItemsArray = new JSONArray(cartItemsString);
            for (int i = 0; i < cartItemsArray.length(); i++) {
                JSONObject itemObject = cartItemsArray.getJSONObject(i);
                String productId = itemObject.getString("productId");
                String productName = itemObject.getString("productName");
                String productImageUrl = itemObject.getString("productImageUrl");

                CartItem cartItem = new CartItem(productId, productName, productImageUrl);
                cartItemList.add(cartItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cartItemList;
    }
}
