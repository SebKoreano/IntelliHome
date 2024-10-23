package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

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

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<CardViewObj> arrayList = new ArrayList<>();

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

        /*
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList.add(new CardViewObj(300, "9.17", "Casas de Casas", "(123,456)", "casa0"));
        arrayList.add(new CardViewObj(300, "8.16", "Casas y Casas", "(0,46)", "casaI"));
        arrayList.add(new CardViewObj(300, "7.15", "Casas o Casas", "(23,4)", "casaII"));
        arrayList.add(new CardViewObj(300, "6.14", "Casas e Casas", "(13,78)", "casaIII"));

        CardViewObjRecycleView cardViewObjRecycleView = new CardViewObjRecycleView(arrayList, this);
        recyclerView.setAdapter(cardViewObjRecycleView);

         */

        navigationView.bringToFront();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return MainPageActivity.this.onNavigationItemSelected(menuItem);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        // Lógica para el NavigationView
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

        // Lógica para el BottomNavigationView
        if (id == R.id.bottom_nav_home) {
            Intent intent = new Intent(MainPageActivity.this, ConfigActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.bottom_nav_search) {
            // Lógica para buscar
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
