package com.example.intellihome;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                // Validar email, contraseña y coincidencia de contraseñas
                if (validarEmail(inputEmail.getText().toString()) && validarPassword(inputPassword.getText().toString()) && validarContraseñasIguales()) {
                    crearCuenta();
                }
            } else {
                Toast.makeText(this, "Debe aceptar los Términos y Condiciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearCuenta() {
        // Lógica para manejar la creación de la cuenta
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (checkboxPropietario.isChecked()) {
            String iban = inputIban.getText().toString();
            // Guardar el IBAN o hacer algo con él
        }

        if (checkboxAlquilar.isChecked()) {
            String cardNumber = inputCardNumber.getText().toString();
            String cvv = inputCVV.getText().toString();
            String cardHolder = inputCardHolder.getText().toString();
            // Guardar los datos de la tarjeta o hacer algo con ellos
        }

        Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
    }

    // Método para validar el email
    private boolean validarEmail(String email) {
        final String emailRegex = "([a-zA-Z0-9]+)([\\_\\.\\-{1}])*([a-zA-Z0-9]+)\\@([a-zA-Z0-9]+)([\\.])([a-zA-Z\\.]+)";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Método para validar la contraseña
    private boolean validarPassword(String password) {
        final String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[\\W]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            Toast.makeText(this, "Contraseña inválida. ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Método para validar que ambas contraseñas coinciden
    private boolean validarContraseñasIguales() {
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

