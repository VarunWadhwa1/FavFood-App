package com.example.favfood.Models;

public class GetCompareItem {
    String Food_name;
    String Price;
    String carrier;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFood_name() {
        return Food_name;
    }

    public GetCompareItem(String food_name, String price,String carrier) {
        Food_name = food_name;
        Price = price;
        this.carrier = carrier;
    }

    public void setFood_name(String food_name) {
        Food_name = food_name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
