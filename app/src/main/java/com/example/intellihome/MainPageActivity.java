package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<PropertyModule> elements, originalElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        drawerLayout = findViewById(R.id.drawer_menu);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.menu_toolbar);
        searchView = findViewById(R.id.mainpage_search_bar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        recyclerView = findViewById(R.id.recyclerView);

        navigationView.bringToFront();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        toggle.syncState();

        initModules();
        initListener();

        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return MainPageActivity.this.onNavigationItemSelected(menuItem);
            }
        });
    }

    public void initListener() {
        searchView.setOnQueryTextListener(this);
    }

    public void initModules() {
        elements = new ArrayList<>();
        originalElements = new ArrayList<>();

        List<String> rules1 = new ArrayList<>();
        rules1.add("No smoking");
        rules1.add("No pets");

        List<String> amenities1 = new ArrayList<>();
        amenities1.add("Pool");
        amenities1.add("Wi-Fi");
        amenities1.add("Parking");

        List<String> rules2 = new ArrayList<>();
        rules2.add("No parties");
        rules2.add("No loud music");

        List<String> amenities2 = new ArrayList<>();
        amenities2.add("Gym");
        amenities2.add("Laundry");
        amenities2.add("Wi-Fi");

        List<String> rules3 = new ArrayList<>();
        rules3.add("No smoking");
        rules3.add("No loud music");

        List<String> amenities3 = new ArrayList<>();
        amenities3.add("Pool");
        amenities3.add("Parking");

        List<String> rules4 = new ArrayList<>();
        rules4.add("No pets");
        rules4.add("No loud music");

        List<String> amenities4 = new ArrayList<>();
        amenities4.add("Wi-Fi");
        amenities4.add("Gym");
        amenities4.add("Laundry");

        List<String> rules5 = new ArrayList<>();
        rules5.add("No parties");
        rules5.add("No smoking");

        List<String> amenities5 = new ArrayList<>();
        amenities5.add("Parking");
        amenities5.add("Wi-Fi");
        amenities5.add("Pool");

        PropertyModule property1 = new PropertyModule("House 1", "Apartment", "No parking", "Beautiful apartment with 2 rooms", "2", "1000", "John Doe", "12.34", "56.78", rules1, amenities1);
        PropertyModule property2 = new PropertyModule("House 2", "House", "Garage", "Spacious house with a garden", "3", "1500", "Jane Smith", "23.45", "67.89", rules2, amenities2);
        PropertyModule property3 = new PropertyModule("House 3", "Condo", "Street parking", "Modern condo with pool access", "1", "1200", "Bob Johnson", "34.56", "78.90", rules3, amenities3);
        PropertyModule property4 = new PropertyModule("House 4", "Villa", "Garage", "Luxury villa near the beach", "5", "3000", "Alice Brown", "45.67", "89.01", rules4, amenities4);
        PropertyModule property5 = new PropertyModule("House 5", "Cabin", "No parking", "Cozy cabin in the mountains", "2", "800", "Charlie Davis", "56.78", "90.12", rules5, amenities5);

        elements.add(property1);
        elements.add(property2);
        elements.add(property3);
        elements.add(property4);
        elements.add(property5);

        originalElements.addAll(elements);

        CardViewAdapter cardViewAdapter = new CardViewAdapter(elements, this, new CardViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PropertyModule obj) {
                moveToHouseRental(obj);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cardViewAdapter);
    }

    public void filter(String strSearch) {
        if (strSearch.length() == 0) {
            elements.clear();
            elements.addAll(originalElements);
        } else {
            List<PropertyModule> filterElements = new ArrayList<>();

            for (PropertyModule property : originalElements) {
                if (property.getTitle().toLowerCase().contains(strSearch)) {
                    filterElements.add(property);
                }
            }

            elements.clear();
            elements.addAll(filterElements);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_profile) {
            // Lógica para perfil
        } else if (id == R.id.menu_settings) {
            Intent intent = new Intent(MainPageActivity.this, ConfigActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_add_house) {
            Intent intent = new Intent(MainPageActivity.this, PublicarCasaActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_control_house) {
            Intent intent = new Intent(MainPageActivity.this, LightControlActivity.class);
            startActivity(intent);
        }

        if (id == R.id.bottom_nav_home) {
            Intent intent = new Intent(MainPageActivity.this, ConfigActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.bottom_nav_search) {
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void moveToHouseRental(PropertyModule item) {
        Intent intent = new Intent(this, HouseRental.class);
        intent.putExtra("property", item);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText.toLowerCase());

        recyclerView.getAdapter().notifyDataSetChanged();
        return true;
    }

}
