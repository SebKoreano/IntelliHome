package com.example.intellihome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import android.net.Uri;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputUsername, inputPhone, inputEmail, inputPassword, inputRepeatPassword, inputHobbies;
    private AutoCompleteTextView selectCasa, selectVehiculo, selectDomicilio;
    private DatePicker datePicker, expDatePicker;
    private CheckBox checkboxPropietario, checkboxAlquilar, checkboxTerms;
    private LinearLayout propietarioSection, alquilarSection;
    private EditText inputIban, inputCardNumber, inputCVV, inputCardHolder;
    private Socket socket; // Socket para la conexión
    private PrintWriter out; // PrintWriter para enviar datos
    private Scanner in;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private ImageView profileImage, iconHelpPassword, btnTogglePassword, btnTogglePassword2;
    private Button btnCreateAccount, btnProfilePhoto;
    private boolean isPasswordVisible = false, isPasswordVisible2 = false;
    private DialogManager dialogManager;
    private PhotoManager photoManager;
    private Validator validator;
    private List<EditText> campos;

    private LinearProgressIndicator progress;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar elementos
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnProfilePhoto = findViewById(R.id.btnProfilePhoto);
        inputFirstName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputUsername = findViewById(R.id.inputUsername);
        inputPhone = findViewById(R.id.inputPhone);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputRepeatPassword = findViewById(R.id.inputRepeatPassword);
        selectCasa = findViewById(R.id.selectCasa);
        selectVehiculo = findViewById(R.id.selectVehiculo);
        inputHobbies = findViewById(R.id.inputHobbies);
        selectDomicilio = findViewById(R.id.selectDomicilio);
        datePicker = findViewById(R.id.datePicker);
        checkboxPropietario = findViewById(R.id.checkboxPropietario);
        checkboxAlquilar = findViewById(R.id.checkboxAlquilar);
        checkboxTerms = findViewById(R.id.checkboxTerms);
        propietarioSection = findViewById(R.id.propietarioSection);
        alquilarSection = findViewById(R.id.alquilarSection);
        inputIban = findViewById(R.id.inputIban);
        inputCardNumber = findViewById(R.id.inputCardNumber);
        inputCVV = findViewById(R.id.inputCVV);
        inputCardHolder = findViewById(R.id.inputCardHolder);
        expDatePicker = findViewById(R.id.expDatePicker);
        iconHelpPassword = findViewById(R.id.iconHelpPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnTogglePassword2 = findViewById(R.id.btnTogglePassword2);
        dialogManager = new DialogManager(this);
        photoManager = new PhotoManager(this, btnProfilePhoto);

        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        btnCreateAccount.setBackgroundColor(currentColor);

        // Conectar al servidor
        connectToServer("172.18.83.115", 3535); //192.168.18.206

        // Ocultar inicialmente las secciones de Propietario y Alquilar
        propietarioSection.setVisibility(View.GONE);
        alquilarSection.setVisibility(View.GONE);

        // Configurar los eventos de los CheckBoxes
        checkboxPropietario.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                propietarioSection.setVisibility(View.VISIBLE);
            } else {
                propietarioSection.setVisibility(View.GONE);
            }
        });

        checkboxAlquilar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                alquilarSection.setVisibility(View.VISIBLE);
            } else {
                alquilarSection.setVisibility(View.GONE);
            }
        });

        // Configurar el evento onClick para mostrar el cuadro de diálogo
        iconHelpPassword.setOnClickListener(v -> {
            // Mostrar el cuadro de diálogo con los requerimientos de la contraseña
            dialogManager.showPasswordRequirementsDialog();
        });

        // Spinner de Casa, usando recursos de strings
        String[] casas = {getString(R.string.apartaRegisterActivity), getString(R.string.casacampRegisterActivity), getString(R.string.casaplaRegisterActivity), getString(R.string.cabañaRegisterActivity), getString(R.string.pisocciuRegisterActivity)};
        ArrayAdapter<String> casaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, casas);
        casaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCasa.setAdapter(casaAdapter);

        // Spinner de Vehículo
        String[] vehiculos = {getString(R.string.x4RegisterActivity), getString(R.string.pickupRegisterActivity), getString(R.string.sedanRegisterActivity), getString(R.string.suvRegisterActivity), getString(R.string.camionetaRegisterActivity)};
        ArrayAdapter<String> vehiculoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehiculos);
        vehiculoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVehiculo.setAdapter(vehiculoAdapter);

        // Spinner de Domicilio
        String[] domicilios = {getString(R.string.sanjoseRegisterActivity), getString(R.string.alajuelaRegisterActivity), getString(R.string.herediaRegisterActivity), getString(R.string.limonRegisterActivity), getString(R.string.puntarenasRegisterActivity), getString(R.string.guanaRegisterActivity), getString(R.string.cartagoRegisterActivity)};
        ArrayAdapter<String> domicilioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, domicilios);
        domicilioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectDomicilio.setAdapter(domicilioAdapter);

        //Boton para tomar foto
        btnProfilePhoto.setOnClickListener(view -> showPhotoSelectionDialog());

        //ChackBox de los terminos y condiciones
        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mostrar el diálogo con los términos y condiciones
                dialogManager.showTermsAndConditionsDialog();
            }
        });

        // Inicializar validator
        validator = new Validator(this);

        // Agregar los campos que se validarán
        campos = new ArrayList<>();
        campos.add(inputFirstName);
        campos.add(inputLastName);
        campos.add(inputUsername);
        campos.add(inputPhone);

        btnCreateAccount.setOnClickListener(view -> {
            if (checkboxTerms.isChecked()) {
                boolean isPropietario = checkboxPropietario.isChecked();
                boolean isAlquilar = checkboxAlquilar.isChecked();
                // Validar los campos generales
                if (validarCamposGenerales(isPropietario, isAlquilar)) {
                    if (isPropietario && !isAlquilar) {
                        obtenerDatos();
                        sendMessage(concatenarDatos("Propietario"));

                        validator.mostrarMensaje(getString(R.string.cuentcreexRegisterActivity));
                        imageUri = getImageUriFromButton(btnProfilePhoto);
                        uploadInfoToFirebase(imageUri, "Propietario");
                        regresarAConfig();
                    }
                    if (isAlquilar && !isPropietario) {
                        obtenerDatos();
                        sendMessage(concatenarDatos("Alquilador"));

                        validator.mostrarMensaje(getString(R.string.cuentcreexRegisterActivity));
                        imageUri = getImageUriFromButton(btnProfilePhoto);
                        uploadInfoToFirebase(imageUri, "Alquilador");
                        regresarAConfig();
                    }
                    if (isAlquilar && isPropietario) {
                        obtenerDatos();
                        sendMessage(concatenarDatos("AmbasFunciones"));

                        validator.mostrarMensaje(getString(R.string.cuentcreexRegisterActivity));
                        imageUri = getImageUriFromButton(btnProfilePhoto);
                        uploadInfoToFirebase(imageUri, "AmbasFunciones");
                        regresarAConfig();
                    }

                }
            } else {
                validator.mostrarMensaje(getString(R.string.debeacepterRegisterActivity));
            }
        });

        // Manejo de botones para ver la contraseña
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility(inputPassword, btnTogglePassword));
        btnTogglePassword2.setOnClickListener(v -> togglePasswordVisibility(inputRepeatPassword, btnTogglePassword2));
    }

    // Método para alternar la visibilidad de la contraseña
    private void togglePasswordVisibility(EditText passwordField, ImageView toggleButton) {
        if (isPasswordVisible) {
            // Si la contraseña es visible, ocultarla
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_eye_closed);  // Cambiar el ícono al ojo cerrado
        } else {
            // Si la contraseña está oculta, mostrarla
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_eye_open);  // Cambiar el ícono al ojo abierto
        }
        isPasswordVisible = !isPasswordVisible; // Alternar el estado
        passwordField.setSelection(passwordField.length()); // Mantener el cursor al final del texto
    }

    // Método para validar campos generales
    private boolean validarCamposGenerales(boolean isPropietario, boolean isAlquilar) {
        return validator.validarDatosGenerales(campos, isPropietario, isAlquilar)
                && validator.validarEmail(inputEmail.getText().toString())
                && validator.validarPassword(inputPassword.getText().toString())
                && validator.validarContraseñasIguales(inputPassword.getText().toString(), inputRepeatPassword.getText().toString())
                && validator.verificarEdad(datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
    }

    // Método para validar el IBAN
    private boolean validarIban() {
        return validator.validarIBAN(inputIban.getText().toString());
    }

    // Método para validar la tarjeta
    private boolean validarTarjeta() {
        return validator.validarTarjeta(inputCardNumber.getText().toString(), inputCVV.getText().toString(), expDatePicker);
    }

    private void showPhotoSelectionDialog() {
        // Usar el diálogo de selección de foto
        photoManager.showPhotoSelectionDialog((dialog, which) -> {
            if (which == 0) {
                photoManager.dispatchTakePictureIntent();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES}, DialogManager.PhotoManager.PICK_IMAGE);
                    } else {
                        photoManager.openGallery();
                    }
                } else {
                    photoManager.openGallery();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoManager.REQUEST_IMAGE_CAPTURE) {
                photoManager.handleCameraImage(data,true);
            } else if (requestCode == PhotoManager.PICK_IMAGE) {
                photoManager.handleGalleryImage(data, true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        photoManager.handleRequestPermissionsResult(requestCode, grantResults);
    }

    private void connectToServer(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        runOnUiThread(() -> {
                            Log.d("RegisterActivity", "Mensaje recibido: " + message);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    //Pasar al ConfigActivity
    private void regresarAConfig() {
        Intent intent = new Intent(RegisterActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    //Obtengo los datos de la cuenta
    private void obtenerDatos() {
        // Obtener datos básicos
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // Obtener fecha seleccionada del DatePicker (siempre se obtiene)
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Los meses empiezan en 0, por eso sumamos 1
        int year = datePicker.getYear();
        String birthDate = day + "/" + month + "/" + year; // Formato de fecha

        // Obtener datos adicionales si el checkbox de propietario está marcado
        if (checkboxPropietario.isChecked()) {
            String iban = inputIban.getText().toString();
        }

        // Obtener datos de la tarjeta si el checkbox de alquilar está marcado
        if (checkboxAlquilar.isChecked()) {
            String cardNumber = inputCardNumber.getText().toString();
            String cvv = inputCVV.getText().toString();
            String cardHolder = inputCardHolder.getText().toString();

            // Obtener fecha de expiración de la tarjeta del expDatePicker
            int expMonth = expDatePicker.getMonth() + 1; // Los meses empiezan en 0, por eso sumamos 1
            int expYear = expDatePicker.getYear();
            String expirationDate = expMonth + "/" + expYear; // Formato de fecha de expiración
        }
    }
    private String concatenarDatos(String Tipo) {
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        StringBuilder sb = new StringBuilder();
        sb.append("CrearCuenta").append("_");
        sb.append(Tipo).append("_");
        sb.append(username).append("_");
        sb.append(phone).append("_");
        sb.append(email).append("_");
        sb.append(password).append("_");

        // Agregar información de las secciones de propietario y alquilar si están visibles
        if (checkboxPropietario.isChecked()) {
            String iban = inputIban.getText().toString();
            sb.append(iban).append("_");
        }

        if (checkboxAlquilar.isChecked()) {
            String cardNumber = inputCardNumber.getText().toString();
            String CardCVV = inputCVV.getText().toString();
            String CardHolder = inputCardHolder.getText().toString();

            // Obtener fecha de expiración de la tarjeta del expDatePicker
            int expMonth = expDatePicker.getMonth() + 1; // Los meses empiezan en 0, por eso sumamos 1
            int expYear = expDatePicker.getYear();
            String expirationDate = expMonth + "/" + expYear; // Formato de fecha de expiración

            sb.append(cardNumber).append("_");
            sb.append(CardCVV).append("_");
            sb.append(expirationDate).append("_");
            sb.append(CardHolder).append("_");

        }

        return sb.toString();
    }
    private void uploadInfoToFirebase(Uri imageUri, String tipo) {
        if (imageUri != null) {
            // Crear una referencia a Firebase Storage
            String direccionCreacionCarpeta = "Usuarios/" + tipo + "/";
            String nombre = inputUsername.getText().toString(); // Nombre de la carpeta por crear basada en el username

            // Crear la referencia para la carpeta del usuario
            StorageReference carpetaRef = FirebaseStorage.getInstance().getReference(direccionCreacionCarpeta + nombre + "/");

            // Crear la carpeta (archivo dummy) y subir el archivo de texto
            crearCarpeta(carpetaRef.getPath()); // Cambia a carpetaRef.getPath() si es necesario
            crearYSubirTxt(carpetaRef.child("info.txt")); // Aquí subimos el .txt dentro de la carpeta

            // Crear un nombre único para el archivo de imagen
            String fileName = "ProfilePicture.png"; // o el tipo de imagen que sea
            StorageReference fileReference = carpetaRef.child(fileName); // Asegúrate de que esté dentro de la misma carpeta

            // Subir la imagen
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Imagen subida con éxito
                        Toast.makeText(RegisterActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de errores
                        Toast.makeText(RegisterActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri getImageUriFromButton(Button button) {
        // Obtener el drawable del botón
        Drawable drawable = button.getBackground();
        if (drawable != null) {
            // Convertir el drawable a bitmap
            Bitmap bitmap = drawableToBitmap(drawable);

            // Guardar el bitmap en un archivo temporal
            try {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_photo.png");
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
        return null;
    }

    // Función para convertir un Drawable a Bitmap
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private void crearCarpeta(String rutaCarpeta) {
        try {

            // Obtener el nombre del archivo del input (nombre del usuario)
            String name = inputUsername.getText().toString().trim(); // Eliminar espacios innecesarios
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
    private void crearYSubirTxt(StorageReference storageRef) {
        try {
            String firstName = inputFirstName.getText().toString();
            String lastName = inputLastName.getText().toString();
            String username = inputUsername.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();
            String hobbies = inputHobbies.getText().toString();
            String vehiculo = selectVehiculo.getText().toString();
            String casa = selectCasa.getText().toString();
            String domicilio = selectDomicilio.getText().toString();


            // Obtener fecha seleccionada del DatePicker (siempre se obtiene)
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Los meses empiezan en 0, por eso sumamos 1
            int year = datePicker.getYear();
            String birthDate = day + "/" + month + "/" + year; // Formato de fecha

            // Crear un StringBuilder para formar el contenido del archivo
            StringBuilder contenidoArchivo = new StringBuilder();

            // Añadir líneas de ejemplo (puedes reemplazar con tus propios datos)
            contenidoArchivo.append("Nombre:").append(firstName).append("\n");
            contenidoArchivo.append("Apellido:").append(lastName).append("\n");
            contenidoArchivo.append("Username:").append(username).append("\n");
            contenidoArchivo.append("Telefono:").append(phone).append("\n");
            contenidoArchivo.append("Correo:").append(email).append("\n");
            contenidoArchivo.append("Hobbies:").append(hobbies).append("\n");
            contenidoArchivo.append("Vehiculo:").append(vehiculo).append("\n");
            contenidoArchivo.append("CasaPreferencia:").append(casa).append("\n");
            contenidoArchivo.append("Domicilio:").append(domicilio).append("\n");
            contenidoArchivo.append("FechaNacimiento:").append(birthDate).append("\n");


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

}
