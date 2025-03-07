package com.example.e_commerce_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

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
//        View view = LayoutInflater.from(context).inflate(R.layout.product_items, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//        holder.productName.setText(product.getPname());
//        holder.productCategory.setText(product.getCategory());
//        holder.productPrice.setText("$" + product.getPrice());
//
//        // Load the product image using Picasso
//        Picasso.get().load(product.getImage()).into(holder.productImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    public class ProductViewHolder extends RecyclerView.ViewHolder {
//        TextView productName, productCategory, productPrice;
//        ImageView productImage;
//
//        public ProductViewHolder(View itemView) {
//            super(itemView);
//            productName = itemView.findViewById(R.id.product_name);
//            productCategory = itemView.findViewById(R.id.product_description);
//            productPrice = itemView.findViewById(R.id.product_price);
//            productImage = itemView.findViewById(R.id.product_image);
//        }
//    }
//}
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

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
        holder.productName.setText(product.getPname());
        // Set product image using Picasso or Glide
        Picasso.get().load(product.getImage()).into(holder.productImage);

        holder.productImage.setOnClickListener(v -> {
            // Handle item click, for example, add to cart
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
