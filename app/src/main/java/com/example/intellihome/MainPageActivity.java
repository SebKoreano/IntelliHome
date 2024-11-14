package com.example.intellihome;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private boolean isConnected = false;
    private final List<String> mensajesRecibidos = new ArrayList<>();
    private Uri uriHouse;
    private List<Uri> listUrisHouse;

    //Conexión con servidor
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    private final Handler handler = new Handler();
    private final int CHECK_INTERVAL = 500;

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

        createConectionToServer();

        checkMessagesAndInitModules();
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
    }

    private void checkMessagesAndInitModules() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mensajesRecibidos.size() >= 3) {
                    initModules();
                } else {
                    handler.postDelayed(this, CHECK_INTERVAL);
                }
            }
        }, CHECK_INTERVAL);
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

        listUrisHouse = obtenerUrisDeImagenes();

        List<String> rules1 = new ArrayList<>();
        rules1.add("No smoking");
        rules1.add("No pets");

        List<String> rules2 = new ArrayList<>();
        rules2.add("No parties");
        rules2.add("No loud music");

        List<String> rules3 = new ArrayList<>();
        rules3.add("No smoking");
        rules3.add("No loud music");

        List<String> uriStrings = new ArrayList<>();
        for (Uri uri : listUrisHouse) {
            uriStrings.add(uri.toString());
        }

        if (mensajesRecibidos == null || mensajesRecibidos.isEmpty()) {
            Log.d("COMMAND", "NULL");
        } else {
            int index = 0;
            List<String> rules = new ArrayList<>();

            for (String command : mensajesRecibidos) {
                Log.d("COMMAND", command);
                GetHouseInfo house1 = new GetHouseInfo(command);

                switch (index) {
                    case 0:
                        rules = rules1;
                        break;
                    case 1:
                        rules = rules2;
                        break;
                    case 2:
                        rules = rules3;
                        break;
                }

                elements.add(new PropertyModule(
                        house1.getNombreCasa(),
                        house1.getHouseType(),
                        house1.getVehicleType(),
                        house1.getDescripcionGeneral(),
                        house1.getNumeroHabitaciones(),
                        house1.getPrecio(),
                        house1.getDuenoDeVivienda(),
                        house1.getLatitud(),
                        house1.getLongitud(),
                        rules,
                        house1.getAmenidades(),
                        uriStrings,
                        index
                ));
                index++;
            }
        }

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

        filter("");
        recyclerView.getAdapter().notifyDataSetChanged();
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
        recyclerView.getAdapter().notifyDataSetChanged();
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
            Intent intent = new Intent(MainPageActivity.this, MainPageActivity.class);
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
                in = new Scanner(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                Log.d("MainPageActivity", "Conexión establecida y out/in creados.");
                sendMessagesToServer();

                // Hilo separado para la recepción de mensajes
                new Thread(() -> {
                    try {
                        String message;
                        while ((message = in.nextLine()) != null)
                        {
                            if (!mensajesRecibidos.contains(message)) {
                                Log.d("MENSAJESREPETIDOS", message);
                                // setNewHouse(message);
                                mensajesRecibidos.add(message); // Solo se agrega si el mensaje es nuevo
                            }
                        }
                    } catch (Exception e) {
                        Log.d("MainPageActivity", "Error al leer mensajes desde el servidor");
                        e.printStackTrace();
                    }
                }).start();

            } catch (Exception e) {
                Log.d("MainPageActivity", "No se creó el out, in ni socket");
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessagesToServer() {
        new Thread(() -> {
            GetHousesList housesList = new GetHousesList();
            housesList.obtenerNombresViviendas();
            List<String> casas = housesList.getListaCasas();

            if (casas.isEmpty()) {
                Log.d("MainPageActivity", "No se recibieron nombres de viviendas.");
                return;
            }

            for (String casa : casas) {
                try {
                    if (isConnected && out != null) {
                        Log.d("MainPageActivity", "Enviando mensaje al servidor.");
                        out.println("ObtenerInformacionVivienda_" + casa);
                        Thread.sleep(1000);
                    } else {
                        Log.d("MainPageActivity", "Conexión no lista o out es null");
                    }
                } catch (Exception e) {
                    Log.e("MainPageActivity", "Error al mandar mensaje", e);
                }
            }
        }).start();
    }

    public void setNewHouse(String message)
    {
        Log.d("MainPageActivity", "Se inició la creacion de objeto GetHouseInfo");
        List<Uri> listUrisHouse = new ArrayList<>();
        GetHouseInfo house = new GetHouseInfo(message);

        /*
        Task<Uri> uriTask = house.getHouseImageUri();

        uriTask.addOnSuccessListener(uri -> {
            // Cuando se obtiene el Uri, lo asignamos a la variable uriHouse
            uriHouse = uri;

            // Si deseas agregarlo a la lista de Uris
            listUrisHouse.add(uriHouse);

            // Procesamos la imagen con el Uri obtenido
            house.procesarImagen(uri);

            // Opcional: Si también quieres descargar la imagen
            house.downloadImage(uriHouse);  // Si necesitas descargar la imagen a local

            Log.d("UriDeCasaAnadido", "Se creó foto");
        }).addOnFailureListener(e -> {
            // Si algo falla, manejamos el error aquí
            Log.e("ObtenerImagen", "Error al obtener el Uri de la imagen: " + e.getMessage());
        });


        elements.add(new PropertyModule(house.getNombreCasa(),
                house.getHouseType(),
                house.getVehicleType(),
                house.getDescripcionGeneral(),
                house.getNumeroHabitaciones(),
                house.getPrecio(),
                house.getDuenoDeVivienda(),
                house.getLatitud(),
                house.getLongitud(),
                null,
                house.getAmenidades(), null));
         */
    }


    private List<Uri> obtenerUrisDeImagenes() {
        List<Uri> uris = new ArrayList<>();

        int[] drawableIds = {
                R.drawable.house14,
                R.drawable.house15,
                R.drawable.house18
        };

        for (int drawableId : drawableIds) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + drawableId);
            uris.add(uri);
        }

        return uris;
    }
}
