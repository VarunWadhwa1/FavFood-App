package com.example.favfood.Models;

public class DatabaseUser {
    String Name,email,imageUrl,phone, Address,dob;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public DatabaseUser(String name, String email, String phone,String imageUrl, String address, String dob) {
        Name = name;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        Address = address;
        this.dob = dob;
    }
}