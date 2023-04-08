package com.example.favfood.Models;

public class GetItem {
    String Food_Image;
    String Food_Id;
    String Food_name;
    String Restaurant;
    String Price;
    String Rating;
    String carrier;

    public GetItem() {
    }

    public String getFood_Image() {
        return Food_Image;
    }

    public void setFood_Image(String Food_Image) {
        this.Food_Image = Food_Image;
    }

    public String getFood_name() {
        return Food_name;
    }

    public void setFood_name(String food_name) {
        Food_name = food_name;
    }

    public String getRestaurant() {
        return Restaurant;
    }

    public void setRestaurant(String Restaurant) {
        this.Restaurant = Restaurant;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getFood_Id() { return Food_Id; }

    public void setFood_Id(String food_Id) { Food_Id = food_Id; }

    public String getCarrier() { return carrier; }

    public void setCarrier(String carrier) { this.carrier = carrier; }

    public GetItem(String Food_Id,String Food_Image, String Food_name, String Price, String Rating, String Restaurant,String carrier) {
        this.Food_Image = Food_Image;
        this.Food_name = Food_name;
        this.Restaurant = Restaurant;
        this.Price = Price;
        this.Rating = Rating;
        this.Food_Id = Food_Id;
        this.carrier = carrier;
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GetItem)) {
            return false;
        }
        GetItem that = (GetItem) other;
        return this.getFood_name().equals(that.getFood_name()) && this.getPrice().equals(that.getPrice());
    }

}
