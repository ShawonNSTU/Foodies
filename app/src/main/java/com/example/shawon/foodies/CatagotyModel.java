package com.example.shawon.foodies;

/**
 * Created by SHAWON on 2/9/2018.
 */

public class CatagotyModel {

    private String name,image,isNew;

    public CatagotyModel() {
    }

    public CatagotyModel(String name, String image, String isNew) {
        this.name = name;
        this.image = image;
        this.isNew = isNew;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
