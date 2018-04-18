package com.example.shawon.foodies;

/**
 * Created by SHAWON on 2/13/2018.
 */

public class Order {
    private String UserPhone;
    private String ProductID;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String ProductImage;

    public Order() {
    }

    public Order(String userPhone, String productID, String productName, String quantity, String price, String discount, String productImage) {
        UserPhone = userPhone;
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        ProductImage = productImage;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }
}
