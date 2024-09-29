package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;


public class RegisterActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputUsername, inputPhone, inputEmail, inputPassword, inputRepeatPassword;
    private Spinner selectCasa, selectVehiculo, selectHobbies, selectDomicilio;
    private DatePicker datePicker, expDatePicker;
    private CheckBox checkboxPropietario, checkboxAlquilar, checkboxTerms;
    private LinearLayout propietarioSection, alquilarSection;
    private EditText inputIban, inputCardNumber, inputCVV, inputCardHolder;

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

        // Manejar el botón de crear cuenta
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(view -> {
            if (checkboxTerms.isChecked()) {
                if (validarDatos() && validarEmail(inputEmail) && validarPassword(inputPassword) && validarContraseñasIguales() && verificarEdad()) {
                    obtenerDatos();
                    mostrarMensaje("Cuenta creada con éxito");
                    regresarLogIn();
                }
            } else {
                mostrarMensaje("Debe aceptar los Términos y Condiciones");
            }
        });
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

