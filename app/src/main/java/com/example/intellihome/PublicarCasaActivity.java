package com.example.intellihome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublicarCasaActivity extends AppCompatActivity {

    private EditText descripcionInput, precioInput, inputTitulo;
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
    private List<Bitmap> listaDeFotos = new ArrayList<>();


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
        inputTitulo = findViewById((R.id.inputTitulo));

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
        amenidadesArray = getResources().getStringArray(R.array.amenidades_array);
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
                    Toast.makeText(PublicarCasaActivity.this, getString(R.string.maxFotoCasa), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {

                    // Lógica para publicar la casa (si todo está correcto)
                    Toast.makeText(PublicarCasaActivity.this, getString(R.string.publicarCasa), Toast.LENGTH_SHORT).show();

                    subirFoto();

                    mostrarReglas();
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

    private void mostrarReglas(){
        // Convertir la lista de textos a una sola cadena
        List<String> textosReglas = obtenerTextosReglas();
        StringBuilder reglasConcatenadas = new StringBuilder();

        for (String regla : textosReglas) {
            reglasConcatenadas.append(regla).append("\n");  // Concatenar cada regla con un salto de línea
        }

        // Mostrar el resultado en un Toast
        Toast.makeText(PublicarCasaActivity.this, reglasConcatenadas.toString(), Toast.LENGTH_LONG).show();
    }

    private void subirFoto(){
        String carpetaVivienda = "Viviendas Arrendadas/" + inputTitulo.getText().toString() + "/";
        crearCarpeta(carpetaVivienda);

        StorageReference carpetaRef = FirebaseStorage.getInstance().getReference(carpetaVivienda );
        crearYSubirTxt(carpetaRef.child("info.txt"));

        for (int i = 0; i < listaDeFotos.size(); i++) {
            Bitmap bit = listaDeFotos.get(i);
            Uri imageUri = getUriFromBitmap(bit);
            uploadPictureToFirebase(imageUri, i);
        }
    }

    // Método para validar todos los campos antes de publicar
    private boolean validarCampos() {
        // Verificar si la el titulo está vacío
        if (inputTitulo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.errorTitulo), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si la descripción está vacía
        if (descripcionInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.validarDescripcion), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el precio está vacío
        if (precioInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.validarPrecio), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si la ubicación (latitud y longitud) ha sido seleccionada
        if (latitudHome == 0.0 || longitudHome == 0.0) {
            Toast.makeText(this, getString(R.string.validarUbicacion), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una foto
        if (photoManager.getPhotoCount() == 0) {
            Toast.makeText(this, getString(R.string.validarFoto), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una regla de uso
        if (numeroReglas <= 1) {
            Toast.makeText(this, getString(R.string.validarReglas), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si hay al menos una amenidad seleccionada
        if (selectedAmenidades.isEmpty()) {
            Toast.makeText(this, getString(R.string.validarAmenidades), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el checkbox de check-in/check-out está seleccionado
        CheckBox checkInCheckOut = findViewById(R.id.checkInCheckOutCheckbox);
        if (!checkInCheckOut.isChecked()) {
            Toast.makeText(this, getString(R.string.validarcheckInCheckOut), Toast.LENGTH_SHORT).show();
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
        builder.setTitle(getString(R.string.seleccionAmenidades));

        // Configurar las opciones del diálogo con selección múltiple
        builder.setMultiChoiceItems(amenidadesArray, selectedItems, getMultiChoiceClickListener());

        // Configurar botones de acción (OK y Cancelar)
        builder.setPositiveButton(getString(R.string.ok), getPositiveButtonClickListener());
        builder.setNegativeButton(getString(R.string.msjabtActivity), getNegativeButtonClickListener());

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
                Toast.makeText(PublicarCasaActivity.this, getString(R.string.amenidadSeleccionadas) + selectedAmenidades, Toast.LENGTH_LONG).show();
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
                Toast.makeText(PublicarCasaActivity.this, getString(R.string.amenidadesSeleccionadas) + amenidadesArray[which], Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublicarCasaActivity.this, getString(R.string.hint_latitud) + latitudHome + ", " + getString(R.string.hint_longitud) + longitudHome, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PublicarCasaActivity.this, getString(R.string.errorUbicacion), Toast.LENGTH_SHORT).show();
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

        if (tipoCampo.equals("regla")) {
            // Asignar un id único basado en el número de regla
            nuevoCampo.setId(View.generateViewId());  // Genera un ID único
        }

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

    // Método para obtener todos los datos de los campos de reglas
    private List<String> obtenerTextosReglas() {
        List<String> textosReglas = new ArrayList<>();

        LinearLayout reglasLayout = findViewById(R.id.reglasLayout);

        // Iterar sobre todos los hijos del LinearLayout
        for (int i = 0; i < numeroReglas; i++) {
            // Obtener cada EditText basado en el ID generado
            EditText reglaCampo = (EditText) reglasLayout.getChildAt(i);

            // Verificar si el campo no es nulo y añadir el texto a la lista
            if (reglaCampo != null) {
                String texto = reglaCampo.getText().toString().trim();
                if (!texto.isEmpty()) {
                    textosReglas.add(texto);
                }
            }
        }

        return textosReglas;
    }

    // Método para agregar las imágenes a la lista
    public void agregarImagenALaLista(Bitmap bitmap) {
        listaDeFotos.add(bitmap);
        // Mostrar el tamaño de la lista en los logs
        Log.d("PublicarCasaActivity", "Total fotos en la lista: " + listaDeFotos.size());
    }


    private void crearCarpeta(String rutaCarpeta) {
        try {

            // Obtener el nombre del archivo del input (nombre del usuario)
            String name = inputTitulo.getText().toString().trim(); // Eliminar espacios innecesarios
            String archivoDummy = name + ".txt"; // Archivo vacío
            String rutaCompleta = rutaCarpeta + archivoDummy; // Ruta completa combinada

            // Obtener la referencia al Storage de Firebase
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Crear una referencia a la ruta completa
            StorageReference carpetaRef = storageRef.child(rutaCompleta);

        } catch (Exception e) {
            System.err.println("Error al crear la carpeta: " + e.getMessage());
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        // Guardar el bitmap en un archivo temporal
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Crear y retornar el Uri del archivo
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void crearYSubirTxt(StorageReference storageRef) {
        try {
            // Crear un StringBuilder para formar el contenido del archivo
            StringBuilder contenidoArchivo = new StringBuilder();
            Spinner vehiculo = findViewById(R.id.spinnerVehiculo);
            Spinner casa = findViewById(R.id.spinnerTipoCasa);
            String nombreCasa = inputTitulo.getText().toString();
            String descripcionCasa = descripcionInput.getText().toString();
            String precioPorNoche = precioInput.getText().toString();
            GlobalColor globalVariable = (GlobalColor) getApplication();


            // Añadir líneas de ejemplo (puedes reemplazar con tus propios datos)
            contenidoArchivo.append("DuenoDeVivienda:").append(globalVariable.getCurrentuserName()).append("\n");
            contenidoArchivo.append("NombreDeVivienda:").append(nombreCasa).append("\n");
            contenidoArchivo.append("DescripcionGeneral:").append(descripcionCasa).append("\n");
            contenidoArchivo.append("NumeroHabitaciones:").append(numHabitacionesPicker.getValue()).append("\n");
            contenidoArchivo.append("Precio:").append(precioPorNoche).append("\n");
            contenidoArchivo.append("Longitud:").append(longitudHome).append("\n");
            contenidoArchivo.append("Latitud:").append(latitudHome).append("\n");

            //int j = 0;
            //for (String strg:
              //   ) {

            //}

            int i = 0;
            for (String strg: selectedAmenidades
                 ) {
                contenidoArchivo.append("Amenidad").append(i).append(":").append(strg).append("\n");
                i++;
            }
            contenidoArchivo.append("TipoCasa:").append(casa.getSelectedItem().toString()).append("\n");
            contenidoArchivo.append("VehiculoPreferencia:").append(vehiculo.getSelectedItem().toString()).append("\n");


            // Convertir el contenido a bytes
            byte[] data = contenidoArchivo.toString().getBytes("UTF-8");

            // Subir el archivo al Storage en la referencia dada
            storageRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Manejar el éxito de la subida
                        System.out.println("Archivo subido exitosamente a: " + storageRef.getPath());
                    })
                    .addOnFailureListener(e -> {
                        // Manejar errores en la subida
                        System.err.println("Error al subir el archivo: " + e.getMessage());
                    });

        } catch (Exception e) {
            System.err.println("Error al crear o subir el archivo: " + e.getMessage());
        }
    }

    private void uploadPictureToFirebase(Uri imageUri, int numImagen) {
        if (imageUri != null) {
            // Crear una referencia a Firebase Storage
            String nombreCasa = inputTitulo.getText().toString();
            String direccionCreacionCarpeta = "Viviendas Arrendadas/" + nombreCasa + "/";
            String nombre = "Imagen" + numImagen;

            // Crear la referencia para la carpeta del usuario
            StorageReference carpetaRef = FirebaseStorage.getInstance().getReference(direccionCreacionCarpeta + nombre + "/");

            // Crear un nombre único para el archivo de imagen
            String fileName = "Imagen" + numImagen;
            StorageReference fileReference = carpetaRef; // Asegúrate de que esté dentro de la misma carpeta

            // Subir la imagen
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Imagen subida con éxito
                        Toast.makeText(PublicarCasaActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de errores
                        Toast.makeText(PublicarCasaActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
