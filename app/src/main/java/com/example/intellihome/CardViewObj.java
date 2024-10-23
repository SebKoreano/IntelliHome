package com.example.intellihome;

import android.app.Application;

public class CardViewObj extends Application {

    int image;
    String name, description, coords, price;

    public CardViewObj(int image, String price, String description, String coords, String name){

        this.image = image;
        this.price = price;
        this.description = description;
        this.coords = coords;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCoords() {
        return coords;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
