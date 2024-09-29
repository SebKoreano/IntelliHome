package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.Scanner;

import com.google.android.material.textfield.TextInputLayout;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private PrintWriter out;
    private Scanner in;
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

        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                socket = new Socket("192.168.18.206", 1717);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        runOnUiThread(() -> {
                            // Registrar el mensaje recibido
                            Log.d("MainActivity", "Mensaje recibido: " + message);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


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
