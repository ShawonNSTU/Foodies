package com.example.shawon.foodies;

/**
 * Created by SHAWON on 2/18/2018.
 */

public class GetServerUserInfo {

    private String name,password,isstaff,phone;

    public GetServerUserInfo() {

    }

    public GetServerUserInfo(String name, String password) {
        this.name = name;
        this.password = password;
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
}
