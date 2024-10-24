package com.example.intellihome;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class HouseRental extends AppCompatActivity {

    private TextView title;
    private TextView type;
    private TextView vehicle;
    private TextView description;
    private TextView rooms;
    private TextView money;
    private TextView owner;
    private TextView coords;
    private TextView rules;
    private TextView amenities;
    private Button rent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_rental);

        PropertyModule house = (PropertyModule) getIntent().getSerializableExtra("property");

        title = findViewById(R.id.property_title);
        type = findViewById(R.id.property_type);
        vehicle = findViewById(R.id.property_vehicle);
        description = findViewById(R.id.property_description);
        rooms = findViewById(R.id.property_rooms);
        money = findViewById(R.id.property_money);
        owner = findViewById(R.id.property_owner);
        coords = findViewById(R.id.property_coords);
        rules = findViewById(R.id.property_rules);
        amenities = findViewById(R.id.property_amenities);
        rent = findViewById(R.id.btnRent);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        rent.setBackgroundColor(currentColor);

        // Set the information of the house
        if (house != null) {
            title.setText(house.getTitle());
            type.setText(house.getType());
            vehicle.setText(house.getVehicle());
            description.setText(house.getDescription());
            rooms.setText(house.getRooms());
            money.setText(house.getMoney());
            owner.setText(house.getOwner());
            coords.setText("X: " + house.getX_coords() + ", Y: " + house.getY_coords());

            // Joining rules and amenities with commas
            List<String> rulesList = house.getRules();
            List<String> amenitiesList = house.getAmenities();
            rules.setText(rulesList != null ? String.join(", ", rulesList) : "No rules available");
            amenities.setText(amenitiesList != null ? String.join(", ", amenitiesList) : "No amenities available");
        }
    }
}
