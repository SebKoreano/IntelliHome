package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        drawerLayout = findViewById(R.id.drawer_menu);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.menu_toolbar);
        searchView = findViewById(R.id.mainpage_search_bar);
        bottomNavigationView = findViewById(R.id.bottom_nav);

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
            // Lógica para monitorear viviendas
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

    private List<String> obtenerNombresDeCarpetas(String carpetaBase) {
        // Inicializa FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Referencia a la carpeta base pasada como parámetro
        StorageReference carpetaRef = storageRef.child(carpetaBase);

        // Lista para guardar los nombres de las carpetas
        List<String> listaCarpetas = new ArrayList<>();

        // Llama a listAll() para obtener todos los items dentro de la carpeta especificada
        carpetaRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // Obtén los directorios dentro de la carpeta base
                for (StorageReference prefix : listResult.getPrefixes()) {
                    // Extrae el nombre de cada carpeta
                    String folderName = prefix.getName();
                    listaCarpetas.add(folderName.trim());
                }

                // Convierte la lista de nombres de carpetas a un solo string
                StringBuilder carpetasString = new StringBuilder();
                for (String nombre : listaCarpetas) {
                    carpetasString.append(nombre).append("\n");
                }

                // Muestra la lista en una AlertDialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainPageActivity.this);  // 'context' es tu Activity o Fragment
                dialogBuilder.setTitle("Carpetas disponibles");
                dialogBuilder.setMessage(carpetasString.toString());
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();

            }
        });
        return listaCarpetas;
    }

    List<String> listaCasas = obtenerNombresDeCarpetas("Viviendas Arrendadas");

}
