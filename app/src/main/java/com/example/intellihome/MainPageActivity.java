package com.example.intellihome;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<PropertyModule> elements, originalElements;
    private ImageButton tagsbtn;
    private List<GetHouseInfo> houseList;
    private boolean isConnected = false;

    //Conexión con servidor
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

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
        tagsbtn = findViewById(R.id.tags);
        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor = globalColor.getCurrentColor();

        changeHeader(currentColor);

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

        tagsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiSelectDialog();
            }
        });
        createConectionToServer();
    }

    private void changeHeader(int currentColor) {
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        LinearLayout menuHeader = headerView.findViewById(R.id.menu_header);
        menuHeader.setBackgroundColor(currentColor);
    }

    private void showMultiSelectDialog() {
        // Opciones de tags para seleccionar
        String[] tags = getResources().getStringArray(R.array.amenities_array);;
        boolean[] checkedTags = new boolean[tags.length];
        List<String> selectedTags = new ArrayList<>();

        // Configuración del diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
        builder.setTitle("Select Tags");

        // Checkbox para múltiples selecciones
        builder.setMultiChoiceItems(tags, checkedTags, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedTags.add(tags[which]);
            } else {
                selectedTags.remove(tags[which]);
            }
        });

        // Botón OK para buscar con los tags seleccionados
        builder.setPositiveButton("OK", (dialog, which) -> {
            searchByTags(selectedTags); // Llamada al método para filtrar por tags
        });

        // Botón Cancel para cerrar el diálogo
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Mostrar el diálogo
        builder.create().show();
    }

    private void searchByTags(List<String> selectedTags) {
        if (selectedTags.isEmpty()) {
            // Si no hay tags seleccionados, muestra todas las propiedades
            elements.clear();
            elements.addAll(originalElements);
        } else {
            List<PropertyModule> filteredProperties = new ArrayList<>();

            // Filtra las propiedades que tienen al menos un tag seleccionado
            for (PropertyModule property : originalElements) {
                for (String tag : selectedTags) {
                    if (property.getAmenities().contains(tag)) {
                        filteredProperties.add(property);
                        break;
                    }
                }
            }

            // Actualiza la lista de propiedades en el RecyclerView
            elements.clear();
            elements.addAll(filteredProperties);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
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

        GetHouseInfo houseInfo;
        for (String name : listaCasas) {
            try {
                houseInfo = new GetHouseInfo(name);
                elements.add(new PropertyModule(name, "Apartament", "4X4", houseInfo.getDescripcionGeneral(), houseInfo.getNumeroHabitaciones(), houseInfo.getPrecio(), houseInfo.getDuenoDeVivienda(), houseInfo.getLatitud(), houseInfo.getLongitud(), rules1, houseInfo.getAmenidades(), null));

                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Log.e("HouseError", "Error al cargar la casa: " + e.getMessage());
                Toast.makeText(this, "!Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        GetHouseInfo house = new GetHouseInfo("House 14");
        elements.add(new PropertyModule("House 14", "Apartament", "4X4", house.getDescripcionGeneral(), house.getNumeroHabitaciones(), house.getPrecio(), house.getDuenoDeVivienda(), house.getLatitud(), house.getLongitud(), rules1, house.getAmenidades(), null));

        PropertyModule property1 = new PropertyModule("House 1", "Apartment", "No parking", "Beautiful apartment with 2 rooms", "2", "1000", "John Doe", "12.34", "56.78", rules1, amenities1, null);
        PropertyModule property2 = new PropertyModule("House 2", "House", "Garage", "Spacious house with a garden", "3", "1500", "Jane Smith", "23.45", "67.89", rules2, amenities2, null);
        PropertyModule property3 = new PropertyModule("House 3", "Condo", "Street parking", "Modern condo with pool access", "1", "1200", "Bob Johnson", "34.56", "78.90", rules3, amenities3, null);
        PropertyModule property4 = new PropertyModule("House 4", "Villa", "Garage", "Luxury villa near the beach", "5", "3000", "Alice Brown", "45.67", "89.01", rules4, amenities4, null);
        PropertyModule property5 = new PropertyModule("House 5", "Cabin", "No parking", "Cozy cabin in the mountains", "2", "800", "Charlie Davis", "56.78", "90.12", rules5, amenities5, null);
        PropertyModule property6 = new PropertyModule("House 6", "Condo", "Street parking", "Modern condo with pool access", "1", "1200", "Bob Johnson", "34.56", "78.90", rules3, amenities3, null);
        PropertyModule property7 = new PropertyModule("House 7", "Villa", "Garage", "Luxury villa near the beach", "5", "3000", "Alice Brown", "45.67", "89.01", rules4, amenities4, null);
        PropertyModule property8 = new PropertyModule("House 8", "Cabin", "No parking", "Cozy cabin in the mountains", "2", "800", "Charlie Davis", "56.78", "90.12", rules5, amenities5, null);

        elements.add(property1);
        elements.add(property2);
        elements.add(property3);
        elements.add(property4);
        elements.add(property5);
        elements.add(property6);
        elements.add(property7);
        elements.add(property8);

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
        if (strSearch.isEmpty()) {
            elements.clear();
            elements.addAll(originalElements);
        } else {
            List<PropertyModule> filterElements = new ArrayList<>();

            for (PropertyModule property : originalElements) {
                if (property.getTitle().toLowerCase().contains(strSearch) || property.getMoney().contains(strSearch)) {
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

    List<String> listaCasas = obtenerNombresDeCarpetas("Viviendas Arrendadas");
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

    public void createConectionToServer() {
        Log.d("MainPageActivity", "Se intentó conexion con server");
        new Thread(() -> {
            try {
                GlobalColor globalColor = (GlobalColor) getApplication();
                socket = new Socket(globalColor.getIp(), 3535);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());
                isConnected = true;  // Actualizamos el estado de conexión
                Log.d("MainPageActivity", "Conexión establecida y out/in creados.");
                sendMessagesToServer();
                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        setNewHouse(message);
                    }
                }
            } catch (Exception e) {
                Log.d("MainPageActivity", "No se creo el out, in ni socket");
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessagesToServer() {
        new Thread(() -> {
            List<String> listaCasasAux = new ArrayList<>();
            listaCasasAux.add("House 14");
            listaCasasAux.add("House 16");

            for (String casa: listaCasasAux)
            {
                try {
                    if (isConnected && out != null) {  // Comprobamos si la conexión está lista
                        Log.d("MainPageActivity", "Enviando mensaje al servidor.");
                        out.println("ObtenerInformacionVivienda_" + casa);
                        Thread.sleep(1000);
                    } else {
                        Log.d("MainPageActivity", "Conexión no lista o out es null");
                    }
                } catch (Exception e) {
                    Log.d("MainPageActivity", "Error al mandar mensaje" + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setNewHouse(String message)
    {
        Log.d("MainPageActivity", "Se inició la creacion de objeto GetHouseInfo");
        for (String casa : listaCasas) {
            if (message.startsWith(casa + ":")) {
                String nuevoMensaje = message.replaceFirst(casa + ":", "");
                GetHouseInfo newHouse = new GetHouseInfo(nuevoMensaje);
                houseList.add(newHouse);
            }
        }

    }
}
