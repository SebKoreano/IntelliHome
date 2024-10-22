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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PublicarCasaActivity extends AppCompatActivity {

    private EditText descripcionInput, precioInput;
    private NumberPicker numHabitacionesPicker;
    private Button btnAddReglas, btnAddAmenidades, btnPhoto, btnPublicar;
    private int numeroReglas = 1, numeroAmenidad= 1, totalFotos = 0;;
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
        setContentView(R.layout.activity_publicarcasa);

        // Inicializar vistas
        descripcionInput = findViewById(R.id.inputDescripcion);
        precioInput = findViewById(R.id.inputPrecioPorNoche);
        numHabitacionesPicker = findViewById(R.id.numHabitacionesPicker);
        btnAddReglas = findViewById(R.id.btnAddReglas);
        btnAddAmenidades = findViewById(R.id.btnAddAmenidades);
        btnPhoto = findViewById(R.id.btnHousePhoto);
        linearLayout = findViewById(R.id.linearLayout);
        btnPublicar = findViewById((R.id.btnPublish));

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
            Intent intent = new Intent(PublicarCasaActivity.this, MapActivity.class);
            locationLauncher.launch(intent);
        });

        // Crear instancia de PhotoManager con el layout donde se agregarán las fotos
        photoManager = new PhotoManager(this, linearLayout);

        // Configurar el botón para mostrar el diálogo
        btnPhoto.setOnClickListener(v -> {
            photoManager.showPhotoSelectionDialog((dialog, which) -> {
                if (totalFotos < 10) {
                    if (which == 0) {
                        // Opción para tomar una foto
                        photoManager.dispatchTakePictureIntent();
                    } else if (which == 1) {
                        // Opción para seleccionar de la galería
                        photoManager.openGallery();
                    }
                    totalFotos++;
                }
                else
                {
                    Toast.makeText(PublicarCasaActivity.this, "Hay un maximo de 10 fotos por casa", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Acción del botón "Publicar"
        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    // Lógica para publicar la casa (si todo está correcto)
                    Toast.makeText(PublicarCasaActivity.this, "Publicando la casa...", Toast.LENGTH_SHORT).show();
                }
            }
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

    // Método para validar todos los campos antes de publicar
    private boolean validarCampos() {
        // Verificar si la descripción está vacía
        if (descripcionInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el precio está vacío
        if (precioInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "El precio no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si la ubicación (latitud y longitud) ha sido seleccionada
        if (latitudHome == 0.0 || longitudHome == 0.0) {
            Toast.makeText(this, "Debes elegir una ubicación", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una foto
        if (photoManager.getPhotoCount() == 0) {
            Toast.makeText(this, "Debes añadir al menos una foto", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una regla de uso
        if (numeroReglas <= 1) {
            Toast.makeText(this, "Debes añadir al menos una regla de uso", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una amenidad seleccionada
        if (selectedAmenidades.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos una amenidad", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el checkbox de check-in/check-out está seleccionado
        CheckBox checkInCheckOut = findViewById(R.id.checkInCheckOutCheckbox);
        if (!checkInCheckOut.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos de check-in y check-out", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si todas las validaciones pasan
        return true;
    }

    // Método para obtener el número de reglas de uso
    private int getReglasCount() {
        // Este método debe devolver cuántas reglas se han añadido al layout dinámico.
        return linearLayout.getChildCount();
    }

    // Método principal para mostrar el diálogo de selección múltiple
    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublicarCasaActivity.this);
        builder.setTitle("Selecciona Amenidades");

        // Configurar las opciones del diálogo con selección múltiple
        builder.setMultiChoiceItems(amenidadesArray, selectedItems, getMultiChoiceClickListener());

        // Configurar botones de acción (OK y Cancelar)
        builder.setPositiveButton("OK", getPositiveButtonClickListener());
        builder.setNegativeButton("Cancelar", getNegativeButtonClickListener());

        // Mostrar el diálogo
        builder.create().show();
    }

    // Método para manejar la selección y deselección de opciones en el diálogo
    private DialogInterface.OnMultiChoiceClickListener getMultiChoiceClickListener() {
        return new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                handleAmenidadSelection(which, isChecked);
            }
        };
    }

    // Método para manejar el botón OK del diálogo
    private DialogInterface.OnClickListener getPositiveButtonClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PublicarCasaActivity.this, "Amenidades seleccionadas: " + selectedAmenidades, Toast.LENGTH_LONG).show();
            }
        };
    }

    // Método para manejar el botón Cancelar del diálogo
    private DialogInterface.OnClickListener getNegativeButtonClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }

    // Método para manejar la lógica de selección/deselección de una amenidad
    private void handleAmenidadSelection(int which, boolean isChecked) {
        if (isChecked) {
            // Añadir la opción seleccionada a la lista y mostrar un Toast
            if (!selectedAmenidades.contains(amenidadesArray[which])) {
                selectedAmenidades.add(amenidadesArray[which]);
                Toast.makeText(PublicarCasaActivity.this, "Seleccionado: " + amenidadesArray[which], Toast.LENGTH_SHORT).show();
            }
        } else {
            // Eliminar la opción si se deselecciona
            selectedAmenidades.remove(amenidadesArray[which]);
        }
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
                        Toast.makeText(PublicarCasaActivity.this, "Latitud: " + latitudHome + ", Longitud: " + longitudHome, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PublicarCasaActivity.this, "No se obtuvo la ubicación", Toast.LENGTH_SHORT).show();
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
        return "";
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
