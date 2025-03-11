
package com.example.e_commerce_app;

public class CartItem {
    private String productId;
    private String productName;
    private String productImageUrl;
    private int productQuantity;
    private double productPrice;

    // Constructor
    public CartItem(String productId, String productName, String productImageUrl, double productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productQuantity = 1;
        this.productPrice = productPrice;
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

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public void setQuantity(int quantity) {
        this.productQuantity = quantity;
    }

    public double getTotalPrice() {
        return productPrice * productQuantity;
    }

}
