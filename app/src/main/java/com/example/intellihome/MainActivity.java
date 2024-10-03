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
import androidx.appcompat.app.AlertDialog;
import java.util.Scanner;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.google.android.material.textfield.TextInputLayout;

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
                socket = new Socket("192.168.18.5", 3535); //192.168.18.206
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        handleServerResponse(message); // Llama a handleServerResponse con el mensaje recibido
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Lógica para el botón de Login
        btnLogin.setOnClickListener(v -> {
            // Acciones para el botón de Login
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            StringBuilder sb = new StringBuilder();

            sb.append("Login").append("_");
            sb.append(email).append("_");
            sb.append(password);
            String message = sb.toString();

            new Thread(() -> {
                try {
                    if (out != null) {
                        out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });

        rememberMeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mover a ConfigActivity cuando el CheckBox está marcado
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });

        btnAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnRecoverPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecuperationActivity.class);
            startActivity(intent);
        });
    }
    // Método para manejar la respuesta del servidor
    private void handleServerResponse(String response) {
        // Registrar el mensaje recibido
        Log.d("MainActivity", "Mensaje recibido: " + response);

        // Mostrar la respuesta en un AlertDialog
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Respuesta del Servidor");
            builder.setMessage(response);
            builder.setPositiveButton("OK", null);
            builder.show();
        });
    }
}
