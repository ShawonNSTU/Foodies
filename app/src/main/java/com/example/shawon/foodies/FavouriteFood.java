package com.example.shawon.foodies;

/**
 * Created by SHAWON on 3/26/2018.
 */

public class FavouriteFood {

    private String FoodID;
    private String UserPhone;
    private String FoodName;
    private String FoodImage;
    private String FoodPrice;
    private String FoodDiscount;
    private String FoodDescription;
    private String FoodMenuID;

    public FavouriteFood() {

    }

    public FavouriteFood(String foodID, String userPhone, String foodName, String foodImage, String foodPrice, String foodDiscount, String foodDescription, String foodMenuID) {
        FoodID = foodID;
        UserPhone = userPhone;
        FoodName = foodName;
        FoodImage = foodImage;
        FoodPrice = foodPrice;
        FoodDiscount = foodDiscount;
        FoodDescription = foodDescription;
        FoodMenuID = foodMenuID;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getFoodMenuID() {
        return FoodMenuID;
    }

    public void setFoodMenuID(String foodMenuID) {
        FoodMenuID = foodMenuID;
    }
}
