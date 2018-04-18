package com.example.shawon.foodies;

/**
 * Created by SHAWON on 3/9/2018.
 */

public class Rating {

    private String phone,foodID,ratingValue,comment,name;

    public Rating() {
    }

    public Rating(String phone, String foodID, String ratingValue, String comment, String name) {
        this.phone = phone;
        this.foodID = foodID;
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
