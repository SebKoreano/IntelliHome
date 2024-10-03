package com.example.intellihome;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextUtils;
import java.util.Calendar;
import java.io.PrintWriter;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;
import java.net.Socket;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.os.Build;
import androidx.core.app.ActivityCompat; // Para manejar permisos
import androidx.core.content.ContextCompat; // Para verificar permisos
import android.Manifest; // Para usar los permisos de Android, incluyendo READ_MEDIA_IMAGES
import androidx.annotation.Nullable; // Para la anotación Nullable
import android.widget.ArrayAdapter;



public class RegisterActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputUsername, inputPhone, inputEmail, inputPassword, inputRepeatPassword, inputHobbies;
    private Spinner selectCasa, selectVehiculo, selectDomicilio;
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
    private ImageView profileImage, iconHelpPassword;
    private Button btnCreateAccount, btnProfilePhoto;

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

        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        btnCreateAccount.setBackgroundColor(currentColor);

        // Conectar al servidor
        connectToServer("172.18.251.41", 3535);

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
            showPasswordRequirementsDialog();
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
                        showTermsAndConditionsDialog();
                    }
                });


        // Manejar el botón de crear cuenta
        btnCreateAccount.setOnClickListener(view -> {
            if (checkboxTerms.isChecked()) {
                if (validarDatos() && validarEmail(inputEmail) && validarPassword(inputPassword) && validarContraseñasIguales() && verificarEdad()) {
                    obtenerDatos();
                    sendMessage(concatenarDatos()); // Enviar mensaje al servidor
                    mostrarMensaje(getString(R.string.cuentcreexRegisterActivity));
                    regresarAConfig();
                }
            } else {
                mostrarMensaje(getString(R.string.debeacepterRegisterActivity));
            }
        });
    }

    private void showTermsAndConditionsDialog() {
        // Inflar el layout del diálogo
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_terms_and_conditions, null);
        dialogBuilder.setView(dialogView);

        AlertDialog dialog = dialogBuilder.create();

        // Inicializar los elementos del layout

        Button btnContinue = dialogView.findViewById(R.id.btnContinue);


        // Botón de continuar cierra el diálogo
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    // Método para mostrar los requerimientos de la contraseña en un AlertDialog
    private void showPasswordRequirementsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.requecontraRegisterActivity))
                .setMessage(getString(R.string.contradebecontRegisterActivity) + "\n" +
                        getString(R.string.mayusculacontraRegisterActivity) + "\n" +
                        getString(R.string.minusculacontraRegisterActivity) + "\n" +
                        getString(R.string.numcontraRegisterActivity) + "\n" +
                        getString(R.string.simbocontraRegisterActivity) + "\n" +
                        getString(R.string.mincaractcontraRegisterActivity))
                .setPositiveButton("OK", null)
                .show();
    }

    // Mostrar un diálogo para elegir entre tomar una foto o seleccionar de la galería
    private void showPhotoSelectionDialog() {
        String[] options = {getString(R.string.tomarfotoRegisterActivity), getString(R.string.selectgaleRegisterActivity)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                        PICK_IMAGE);
                            } else {
                                openGallery();
                            }
                        } else {
                            openGallery();
                        }
                    }
                });
        builder.show();
    }

    // Método para abrir la cámara
    private void dispatchTakePictureIntent() {
        // Verifica si se necesita permiso para la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicita el permiso si no está concedido
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Permiso ya concedido, abre la cámara
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Método para seleccionar imagen de la galería
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                handleCameraImage(data);
            } else if (requestCode == PICK_IMAGE) {
                handleGalleryImage(data);
            }
        }
    }

    // Método para manejar la imagen tomada con la cámara
    private void handleCameraImage(@Nullable Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Convertir el Bitmap a Drawable y establecerlo como fondo
            setButtonBackgroundFromBitmap(imageBitmap);
        }
    }

    // Método para manejar la imagen seleccionada de la galería
    private void handleGalleryImage(@Nullable Intent data) {
        Uri selectedImage = data.getData();
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            // Convertir el Bitmap a Drawable y establecerlo como fondo
            setButtonBackgroundFromBitmap(imageBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para convertir el Bitmap en Drawable y establecerlo como fondo del botón
    private void setButtonBackgroundFromBitmap(Bitmap bitmap) {
        Drawable drawableImage = new BitmapDrawable(getResources(), bitmap);
        btnProfilePhoto.setBackground(drawableImage);
        btnProfilePhoto.setBackgroundTintList(null); // Quitar la tint para evitar que afecte la imagen
    }


    // Manejar la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, getString(R.string.accesoimaRegisterActivity), Toast.LENGTH_SHORT).show();
            }
        }
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
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();


        if (checkboxPropietario.isChecked()) {
            String iban = inputIban.getText().toString();
        }

        if (checkboxAlquilar.isChecked()) {
            String cardNumber = inputCardNumber.getText().toString();
            String cvv = inputCVV.getText().toString();
            String cardHolder = inputCardHolder.getText().toString();
        }
    }
    private String concatenarDatos() {
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();

        StringBuilder sb = new StringBuilder();
        sb.append("CrearCuenta").append("_");
        sb.append(firstName).append("_");
        sb.append(lastName).append("_");
        sb.append(username).append("_");
        sb.append(phone).append("_");
        sb.append(email).append("_");
        sb.append(password).append("_");
        sb.append(repeatPassword);

        // Agregar información de las secciones de propietario y alquilar si están visibles
        if (checkboxPropietario.isChecked()) {
            String iban = inputIban.getText().toString();
            sb.append("_").append(iban);
        }

        if (checkboxAlquilar.isChecked()) {
            String cardNumber = inputCardNumber.getText().toString();
            sb.append("_").append(cardNumber);
        }

        return sb.toString();
    }

    //Método para verificar que todos los datos esten completos
    private boolean validarDatos() {
        List<EditText> campos = new ArrayList<>();
        campos.add(inputFirstName);
        campos.add(inputLastName);
        campos.add(inputUsername);
        campos.add(inputPhone);
        if (checkboxPropietario.isChecked()) {
            campos.add(inputIban);
            if (!(validarIBAN())){
                return false;
            }
        }
        if (checkboxAlquilar.isChecked()) {
            campos.add(inputCardNumber);
            campos.add(inputCVV);
            campos.add(inputCardHolder);
            if (!(validarTarjeta())){
                return false;
            }
        }
        if (!(checkboxPropietario.isChecked() || checkboxAlquilar.isChecked())){
            mostrarMensaje(getString(R.string.elegirrolRegisterActivity));
            return false;
        }
        for (EditText campo : campos) {
            if (TextUtils.isEmpty(campo.getText().toString())) {
                campo.setError(getString(R.string.campoobliRegisterActivity));
                return false;
            }
        }
        return true;
    }

    //Metodo para verificar datos de la tarjeta
    private boolean validarTarjeta(){
        String cardNumber = inputCardNumber.getText().toString();
        String cvv = inputCVV.getText().toString();
        if (!(15 <= cardNumber.length() && cardNumber.length() <= 16)){
            inputCardNumber.setError(getString(R.string.numtarjetainvRegisterActivity));
            return false;
        }
        if (!(3 <= cvv.length() && cvv.length() <= 4)){
            inputCVV.setError(getString(R.string.cvvinvRegisterActivity));
            return false;
        }

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Enero = 0, por eso se suma 1

        // Obtener la fecha seleccionada por el usuario
        int selectedYear = expDatePicker.getYear();
        int selectedMonth = expDatePicker.getMonth() + 1;

        if (selectedYear < currentYear || (selectedYear == currentYear && selectedMonth <= currentMonth)) {
            mostrarMensaje(getString(R.string.fechavencsupRegisterActivity));
            return false;
        }

        return true;
    }

    //Método para validar la edad del usuario
    private boolean verificarEdad() {
        // Obtener la fecha seleccionada del DatePicker
        int selectedDay = datePicker.getDayOfMonth();
        int selectedMonth = datePicker.getMonth() + 1; // Los meses en DatePicker comienzan desde 0
        int selectedYear = datePicker.getYear();
        LocalDate fechaDeNacimiento = LocalDate.of(selectedYear, selectedMonth, selectedDay);
        LocalDate fechaActual = LocalDate.now();
        int edad = calcularEdad(fechaDeNacimiento, fechaActual);
        System.out.println((edad));

        if (edad >= 18) {
            return true;
        } else {
            mostrarMensaje(getString(R.string.debe18tenerRegisterActivity));
            return false;
        }
    }

    // Método para calcular la edad
    public int calcularEdad(LocalDate fechaDeNacimiento, LocalDate fechaActual) {
        return Period.between(fechaDeNacimiento, fechaActual).getYears();
    }

    //Método para validar el IBAN
    private boolean validarIBAN() {
        String iban = inputIban.getText().toString();

        // Expresión regular para IBAN
        final String regex = "[a-zA-Z]{2}[0-9]{2}[A-Za-z]{0,4}[0-9]{18,20}$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(iban);

        // Verificar si el IBAN coincide con la expresión regular
        if (!matcher.matches()) {
            inputIban.setError(getString(R.string.ibaninvRegisterActivity));
            return false;
        }
        return true;
    }

    // Método para validar el email
    private boolean validarEmail(EditText emailInput) {
        String email = emailInput.getText().toString();
        final String emailRegex = "([a-zA-Z0-9]+)([\\_\\.\\-{1}])*([a-zA-Z0-9]+)\\@([a-zA-Z0-9]+)([\\.])([a-zA-Z\\.]+)";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            mostrarMensaje(getString(R.string.emailinvRegisterActivity));
            return false;
        }
        return true;
    }

    // Método para validar la contraseña
    private boolean validarPassword(EditText passwordInput) {
        String password = passwordInput.getText().toString();
        final String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[\\W]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            mostrarMensaje(getString(R.string.contrainvRegisterActivity));
            return false;
        }
        return true;
    }

    // Método para validar que ambas contraseñas coinciden
    private boolean validarContraseñasIguales() {
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();

        if (!password.equals(repeatPassword)) {
            mostrarMensaje(getString(R.string.contranocoindiRegisterActivity));
            return false;
        }
        return true;
    }
}

