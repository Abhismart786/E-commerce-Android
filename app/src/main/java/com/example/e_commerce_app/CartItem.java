
package com.example.e_commerce_app;

public class CartItem {
    private String productId;
    private String productName;
    private String productImageUrl;

    // Constructor
    public CartItem(String productId, String productName, String productImageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
    }

    // Getter methods
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }
}
