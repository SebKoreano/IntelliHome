package com.example.intellihome;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyModule implements Serializable {
    private String title, type, vehicle, description, rooms, money, owner, x_coords, y_coords;
    private List<String> rules, amenities;
    private List<String> imageUriStrings;

    private int imageIndex;

    public PropertyModule(String title,
                          String type,
                          String vehicle,
                          String description,
                          String rooms, String money,
                          String owner, String x_coords, String y_coords,
                          List<String> rules, List<String> amenities, List<String> imageUriStrings,
                          int imageIndex) {
        this.title = title;
        this.type = type;
        this.vehicle = vehicle;
        this.description = description;
        this.rooms = rooms;
        this.money = money;
        this.owner = owner;
        this.x_coords = x_coords;
        this.y_coords = y_coords;
        this.rules = rules;
        this.amenities = amenities;
        this.imageUriStrings = imageUriStrings;
        this.imageIndex = imageIndex;
    }

    public String getX_coords() {
        return x_coords;
    }

    public void setX_coords(String x_coords) {
        this.x_coords = x_coords;
    }

    public String getY_coords() {
        return y_coords;
    }

    public void setY_coords(String y_coords) {
        this.y_coords = y_coords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public List<String> getRules() {
        return rules;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public List<Uri> getImageUris() {
        List<Uri> imageUris = new ArrayList<>();
        if (imageUriStrings != null) {
            for (String uriString : imageUriStrings) {
                imageUris.add(Uri.parse(uriString));
            }
        }
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUriStrings = imageUris;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }
}
