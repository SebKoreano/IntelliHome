package com.example.intellihome;

import android.content.Context;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private Context context;

    public Validator(Context context) {
        this.context = context;
    }

    // Método para validar los datos de un formulario general
    public boolean validarDatosGenerales(List<EditText> campos, boolean isPropietario, boolean isAlquilar) {
        for (EditText campo : campos) {
            if (TextUtils.isEmpty(campo.getText().toString())) {
                campo.setError(context.getString(R.string.campoobliRegisterActivity));
                return false;
            }
        }

        if (!isPropietario && !isAlquilar) {
            mostrarMensaje(context.getString(R.string.elegirrolRegisterActivity));
            return false;
        }

        return true;
    }

    // Validar tarjeta de crédito
    public boolean validarTarjeta(String cardNumber, String cvv, DatePicker expDatePicker) {
        if (cardNumber.length() < 15 || cardNumber.length() > 16) {
            mostrarError(context.getString(R.string.numtarjetainvRegisterActivity));
            return false;
        }

        if (cvv.length() < 3 || cvv.length() > 4) {
            mostrarError(context.getString(R.string.cvvinvRegisterActivity));
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int selectedYear = expDatePicker.getYear();
        int selectedMonth = expDatePicker.getMonth() + 1;

        if (selectedYear < currentYear || (selectedYear == currentYear && selectedMonth <= currentMonth)) {
            mostrarError(context.getString(R.string.fechavencsupRegisterActivity));
            return false;
        }

        return true;
    }

    // Validar IBAN
    public boolean validarIBAN(String iban) {
        final String regex = "[a-zA-Z]{2}[0-9]{2}[A-Za-z]{0,4}[0-9]{18,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(iban);

        if (!matcher.matches()) {
            mostrarError(context.getString(R.string.ibaninvRegisterActivity));
            return false;
        }

        return true;
    }

    // Validar email
    public boolean validarEmail(String email) {
        final String emailRegex = "([a-zA-Z0-9]+)([\\_\\.\\-{1}])*([a-zA-Z0-9]+)\\@([a-zA-Z0-9]+)([\\.])([a-zA-Z\\.]+)";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            mostrarError(context.getString(R.string.emailinvRegisterActivity));
            return false;
        }

        return true;
    }

    // Validar contraseñas
    public boolean validarPassword(String password) {
        final String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[\\W]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            mostrarError(context.getString(R.string.contrainvRegisterActivity));
            return false;
        }

        return true;
    }

    // Validar que ambas contraseñas coincidan
    public boolean validarContraseñasIguales(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            mostrarError(context.getString(R.string.contranocoindiRegisterActivity));
            return false;
        }

        return true;
    }

    // Verificar la edad del usuario
    public boolean verificarEdad(int day, int month, int year) {
        LocalDate fechaDeNacimiento = LocalDate.of(year, month, day);
        LocalDate fechaActual = LocalDate.now();
        int edad = Period.between(fechaDeNacimiento, fechaActual).getYears();

        if (edad >= 18) {
            return true;
        } else {
            mostrarMensaje(context.getString(R.string.debe18tenerRegisterActivity));
            return false;
        }
    }

    // Método para mostrar un mensaje de error
    private void mostrarError(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

    // Método para mostrar un mensaje de éxito o información
    public void mostrarMensaje(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
