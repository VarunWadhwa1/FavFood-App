package com.example.favfood.Models;

public class GetRestaurant {
    String Restaurant_Image;
    String Restaurant_Name,Restaurant_Location;
    String Restaurant_Rating;
    String Restaurant_Phone,Restaurant_MainDish,OpeningTime;

    public String getRestaurant_Image() {
        return Restaurant_Image;
    }
    public void setRestaurant_Image(String Restaurant_Image) { this.Restaurant_Image = Restaurant_Image; }

    public String getRestaurant_Name() {
        return Restaurant_Name;
    }
    public void setRestaurant_Name(String Restaurant_Name) { this.Restaurant_Name = Restaurant_Name; }

    public String getRestaurant_Phone() { return Restaurant_Phone; }
    public void setRestaurant_Phone(String restaurant_Phone) { Restaurant_Phone = restaurant_Phone; }

    public String getRestaurant_Location() {
        return Restaurant_Location;
    }
    public void setRestaurant_Location(String Restaurant_Location) { this.Restaurant_Location = Restaurant_Location; }

    public String getRestaurant_Rating() {
        return Restaurant_Rating;
    }
    public void setRestaurant_Rating(String Restaurant_Rating) { this.Restaurant_Rating = Restaurant_Rating; }

    public String getRestaurant_MainDish() { return Restaurant_MainDish; }

    public void setRestaurant_MainDish(String restaurant_MainDish) { Restaurant_MainDish = restaurant_MainDish; }

    public String getOpeningTime() { return OpeningTime; }

    public void setOpeningTime(String openingTime) { OpeningTime = openingTime; }

    public GetRestaurant(String OpeningTime,String Restaurant_Image,  String Restaurant_Location,String Restaurant_MainDish, String Restaurant_Name,String Restaurant_Phone, String Restaurant_Rating) {
        this.Restaurant_Image = Restaurant_Image;
        this.Restaurant_Name = Restaurant_Name;
        this.Restaurant_Location = Restaurant_Location;
        this.Restaurant_Rating = Restaurant_Rating;
        this.Restaurant_MainDish = Restaurant_MainDish;
        this.Restaurant_Phone = Restaurant_Phone;
        this.OpeningTime = OpeningTime;
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GetRestaurant)) {
            return false;
        }
        GetRestaurant that = (GetRestaurant) other;
        return this.getRestaurant_Name().equals(that.getRestaurant_Name());
    }
}
