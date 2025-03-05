package com.example.e_commerce_app;

public class Product {
    private String category;
    private String date;
    private String image;
    private String pid;
    private String pname;
    private String price;
    private String time;

    // Default constructor required for Firebase
    public Product() {}

    // Constructor
    public Product(String category, String date, String image, String pid, String pname, String price, String time) {
        this.category = category;
        this.date = date;
        this.image = image;
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.time = time;
    }

    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
