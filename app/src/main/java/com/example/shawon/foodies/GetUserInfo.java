package com.example.shawon.foodies;

/**
 * Created by SHAWON on 2/9/2018.
 */

public class GetUserInfo {

    private String name,password,isstaff;
    private String phone;

    public GetUserInfo() {

    }

    public GetUserInfo(String name, String password) {
        this.name = name;
        this.password = password;
        this.isstaff = "false";
    }

    public GetUserInfo(String name, String password, String phone) {
        this.name = name;
        this.password = password;
        this.phone = phone;
    }



    public String getIsstaff() {
        return isstaff;
    }

    public void setIsstaff(String isstaff) {
        this.isstaff = isstaff;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
