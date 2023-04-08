package com.example.favfood.Models;

public class GetOrder {
    int Image;
    String name,deliver,price,item,date,saved;
    public GetOrder(){

    }

    public GetOrder(int image, String name, String deliver, String price, String item, String date, String saved) {
        Image = image;
        this.name = name;
        this.deliver = deliver;
        this.price = price;
        this.item = item;
        this.date = date;
        this.saved = saved;
    }
    public int getImage() {
        return Image;
    }
    public void setImage(int image) {
        Image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDeliver() {
        return deliver;
    }
    public void setDeliver(String deliver) {
        this.deliver = deliver;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getSaved() {
        return saved;
    }
    public void setSaved(String saved) {
        this.saved = saved;
    }
}
