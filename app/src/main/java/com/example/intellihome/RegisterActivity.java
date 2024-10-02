package com.example.intellihome;

import android.content.Intent;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.PrintWriter;
import java.util.*;
import android.widget.Toast;
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



public class RegisterActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputUsername, inputPhone, inputEmail, inputPassword, inputRepeatPassword;
    private Spinner selectCasa, selectVehiculo, selectHobbies, selectDomicilio;
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
    private ImageView profileImage;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar elementos
        inputFirstName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputUsername = findViewById(R.id.inputUsername);
        inputPhone = findViewById(R.id.inputPhone);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputRepeatPassword = findViewById(R.id.inputRepeatPassword);
        selectCasa = findViewById(R.id.selectCasa);
        selectVehiculo = findViewById(R.id.selectVehiculo);
        selectHobbies = findViewById(R.id.selectHobbies);
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
        profileImage = findViewById(R.id.profileImageView);
        
        // Conectar al servidor
        connectToServer("192.168.0.114", 1717);

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

        //Boton para tomar foto
        Button btnProfilePhoto = findViewById(R.id.btnProfilePhoto);
        btnProfilePhoto.setOnClickListener(view -> showPhotoSelectionDialog());


        // Manejar el botón de crear cuenta
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(view -> {
            if (checkboxTerms.isChecked()) {
                if (validarDatos() && validarEmail(inputEmail) && validarPassword(inputPassword) && validarContraseñasIguales() && verificarEdad()) {
                    obtenerDatos();
                    sendMessage(concatenarDatos()); // Enviar mensaje al servidor
                    mostrarMensaje("Cuenta creada con éxito");
                    regresarLogIn();
                }
            } else {
                mostrarMensaje("Debe aceptar los Términos y Condiciones");
            }
        });
    }


    // Mostrar un diálogo para elegir entre tomar una foto o seleccionar de la galería
    private void showPhotoSelectionDialog() {
        String[] options = {"Tomar foto", "Seleccionar de la galería"};

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

    // Manejar el resultado de la toma de foto o selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                handleCapturedImage(data);
            } else if (requestCode == PICK_IMAGE) {
                handleSelectedImage(data);
            }
        }
    }

    // Manejar la imagen capturada desde la cámara
    private void handleCapturedImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            displayImage(imageBitmap);  // Mostrar la imagen en el ImageView
            imageBytes = convertBitmapToBytes(imageBitmap);  // Convertir la imagen a bytes
            // sendMessage("Imagen capturada: " + Arrays.toString(imageBytes));
        }
    }

    // Manejar la imagen seleccionada desde la galería
    private void handleSelectedImage(Intent data) {
        Uri selectedImage = data.getData();
        if (selectedImage != null) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                displayImage(imageBitmap);  // Mostrar la imagen en el ImageView
                imageBytes = convertBitmapToBytes(imageBitmap);  // Convertir la imagen a bytes
                // sendMessage("Imagen seleccionada: " + Arrays.toString(imageBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Mostrar la imagen en el ImageView
    private void displayImage(Bitmap imageBitmap) {
        profileImage.setImageBitmap(imageBitmap);
    }

    // Convertir un Bitmap a un array de bytes
    private byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);  // Puedes ajustar el formato y la calidad
        return byteArrayOutputStream.toByteArray();
    }

    // Convertir bytes a Bitmap para mostrar la imagen nuevamente
    private Bitmap convertBytesToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    // Manejar la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso de acceso a imágenes denegado", Toast.LENGTH_SHORT).show();
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

    //Pasar al LogIn
    private void regresarLogIn(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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
            mostrarMensaje("Debe de elegir un rol en la aplicación");
            return false;
        }
        for (EditText campo : campos) {
            if (TextUtils.isEmpty(campo.getText().toString())) {
                campo.setError("Este campo es obligatorio");
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
            inputCardNumber.setError("Numero de tarjeta inválido");
            return false;
        }
        if (!(3 <= cvv.length() && cvv.length() <= 4)){
            inputCVV.setError("CVV inválido");
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
            mostrarMensaje("La fecha de vencimiento debe ser posterior a la fecha actual");
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
            mostrarMensaje("Debes tener al menos 18 años");
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
            inputIban.setError("IBAN inválido");
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
            mostrarMensaje("Email inválido");
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
            mostrarMensaje("Contraseña inválida");
            return false;
        }
        return true;
    }

    // Método para validar que ambas contraseñas coinciden
    private boolean validarContraseñasIguales() {
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();

        if (!password.equals(repeatPassword)) {
            mostrarMensaje("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }
}

