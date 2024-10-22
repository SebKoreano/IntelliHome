package com.example.intellihome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private EditText descripcionInput, precioInput;
    private NumberPicker numHabitacionesPicker;
    private Button btnAddReglas, btnAddAmenidades, btnPhoto;
    private int numeroReglas = 1, numeroAmenidad= 1;
    public static final int MAP_REQUEST_CODE = 1;
    private double latitudHome, longitudHome;
    private PhotoManager photoManager;
    private LinearLayout linearLayout;
    private String[] amenidadesArray;  // Lista de opciones
    private boolean[] selectedItems;   // Lista que guarda qué opciones están seleccionadas
    private ArrayList<String> selectedAmenidades;  // Lista para almacenar las opciones seleccionadas



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar vistas
        descripcionInput = findViewById(R.id.inputDescripcion);
        precioInput = findViewById(R.id.inputPrecioPorNoche);
        numHabitacionesPicker = findViewById(R.id.numHabitacionesPicker);
        btnAddReglas = findViewById(R.id.btnAddReglas);
        btnAddAmenidades = findViewById(R.id.btnAddAmenidades);
        btnPhoto = findViewById(R.id.btnHousePhoto);
        linearLayout = findViewById(R.id.linearLayout);

        // Configurar el NumberPicker
        numHabitacionesPicker.setMinValue(1);  // Valor mínimo
        numHabitacionesPicker.setMaxValue(99); // Valor máximo

        // Acción del botón para añadir reglas
        btnAddReglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevoCampo("regla");
            }
        });

        // Lista de amenidades (opciones)
        amenidadesArray = getResources().getStringArray(R.array.amenidades_array);  // Puedes definirlo en res/values/strings.xml
        selectedItems = new boolean[amenidadesArray.length];
        selectedAmenidades = new ArrayList<>();

        // Configurar el botón para mostrar el diálogo de selección
        btnAddAmenidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiSelectDialog();
            }
        });

        // Acción del botón para añadir foto de perfil
        btnPhoto.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.btnPhoto), Toast.LENGTH_SHORT).show();
            // Lógica para seleccionar la foto
        });

        // Configuración del Spinner de "Tipo de Casa"
        Spinner selectCasa = findViewById(R.id.spinnerTipoCasa);
        String[] casas = {
                getString(R.string.apartaRegisterActivity),
                getString(R.string.casacampRegisterActivity),
                getString(R.string.casaplaRegisterActivity),
                getString(R.string.cabañaRegisterActivity),
                getString(R.string.pisocciuRegisterActivity)
        };
        ArrayAdapter<String> casaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, casas);
        casaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCasa.setAdapter(casaAdapter);

        // Configuración del Spinner de "Vehículo"
        Spinner selectVehiculo = findViewById(R.id.spinnerVehiculo);
        String[] vehiculos = {
                getString(R.string.x4RegisterActivity),
                getString(R.string.pickupRegisterActivity),
                getString(R.string.sedanRegisterActivity),
                getString(R.string.suvRegisterActivity),
                getString(R.string.camionetaRegisterActivity)
        };
        ArrayAdapter<String> vehiculoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehiculos);
        vehiculoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVehiculo.setAdapter(vehiculoAdapter);

        // Configura el botón para abrir el mapa
        Button btnElegirUbicacion = findViewById(R.id.btnElegirUbicacion);
        btnElegirUbicacion.setOnClickListener(v -> {
            // Lanzar la actividad de MapActivity
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            locationLauncher.launch(intent);
        });

        // Crear instancia de PhotoManager con el layout donde se agregarán las fotos
        photoManager = new PhotoManager(this, linearLayout);

        // Configurar el botón para mostrar el diálogo
        btnPhoto.setOnClickListener(v -> {
            photoManager.showPhotoSelectionDialog((dialog, which) -> {
                if (which == 0) {
                    // Opción para tomar una foto
                    photoManager.dispatchTakePictureIntent();
                } else if (which == 1) {
                    // Opción para seleccionar de la galería
                    photoManager.openGallery();
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PhotoManager.REQUEST_IMAGE_CAPTURE) {
                // Manejar imagen de la cámara
                photoManager.handleCameraImage(data, false);
            } else if (requestCode == PhotoManager.PICK_IMAGE) {
                // Manejar imagen de la galería
                photoManager.handleGalleryImage(data,false);
            }
        }
    }

    // Método para mostrar el diálogo de selección múltiple
    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Selecciona Amenidades");

        builder.setMultiChoiceItems(amenidadesArray, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    // Añadir la opción seleccionada a la lista y mostrar un Toast
                    if (!selectedAmenidades.contains(amenidadesArray[which])) {
                        selectedAmenidades.add(amenidadesArray[which]);
                        Toast.makeText(HomeActivity.this, "Seleccionado: " + amenidadesArray[which], Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Eliminar la opción si se deselecciona
                    selectedAmenidades.remove(amenidadesArray[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes manejar lo que sucede cuando el usuario presiona "OK"
                Toast.makeText(HomeActivity.this, "Amenidades seleccionadas: " + selectedAmenidades, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Manejo de la acción de cancelar
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    // Definir el ActivityResultLauncher
    private ActivityResultLauncher<Intent> locationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        latitudHome = data.getDoubleExtra("latitud", 0.0);
                        longitudHome = data.getDoubleExtra("longitud", 0.0);

                        // Mostrar los valores en un Toast
                        Toast.makeText(HomeActivity.this, "Latitud: " + latitudHome + ", Longitud: " + longitudHome, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No se obtuvo la ubicación", Toast.LENGTH_SHORT).show();
                }
            }
    );


    // Método general para agregar un nuevo campo (regla o amenidad)
    private void agregarNuevoCampo(String tipoCampo) {
        // Crear el campo de texto
        EditText nuevoCampo = crearNuevoCampo(tipoCampo);

        // Agregar el campo al layout correspondiente
        agregarCampoAlLayout(nuevoCampo, tipoCampo);

        // Incrementar el contador correspondiente
        incrementarContador(tipoCampo);
    }

    // Método para crear un nuevo EditText con el hint adecuado y el límite de caracteres
    private EditText crearNuevoCampo(String tipoCampo) {
        EditText nuevoCampo = new EditText(this);
        String hint = obtenerHint(tipoCampo);
        nuevoCampo.setHint(hint);
        nuevoCampo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        // Establecer el límite de 100 caracteres
        nuevoCampo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
        return nuevoCampo;
    }

    // Método para obtener el hint según el tipo de campo
    private String obtenerHint(String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            return getString(R.string.reglasHint) + numeroReglas;
        } else if (tipoCampo.equals("amenidad")) {
            return getString(R.string.amenidadHint) + numeroAmenidad;
        }
        return ""; // Por si hay más tipos en el futuro
    }

    // Método para agregar el EditText al layout correspondiente
    private void agregarCampoAlLayout(EditText nuevoCampo, String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            LinearLayout reglasLayout = findViewById(R.id.reglasLayout);
            reglasLayout.addView(nuevoCampo);
        }
    }

    // Método para incrementar el contador de reglas o amenidades
    private void incrementarContador(String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            numeroReglas++;
        } else if (tipoCampo.equals("amenidad")) {
            numeroAmenidad++;
        }
    }
}
