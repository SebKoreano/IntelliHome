package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar el diseño XML
        setContentView(R.layout.activity_login);

        // Obtener referencias a los elementos del layout
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        CheckBox rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnAlreadyHaveAccount = findViewById(R.id.btnAlreadyHaveAccount);
        TextView btnRecoverPassword = findViewById(R.id.btnRecoverPassword);
        TextInputLayout Email = findViewById(R.id.email);

        // Lógica para los botones (si lo necesitas más adelante)
        btnLogin.setOnClickListener(v -> {
            // Acciones para el botón de Login
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            // Validar y procesar el login
        });

        btnAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnRecoverPassword.setOnClickListener(v -> {
            // Acción para "Recuperar contraseña"
            // Abrir actividad o enviar una solicitud de recuperación
        });
    }
}
