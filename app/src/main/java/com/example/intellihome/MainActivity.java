package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    // Declaraciones globales
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private boolean isPasswordVisible = false;

    // Método onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Cargar el diseño XML

        // Obtener referencias a los elementos del layout
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        CheckBox rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnAlreadyHaveAccount = findViewById(R.id.btnAlreadyHaveAccount);
        TextView btnRecoverPassword = findViewById(R.id.btnRecoverPassword);
        ImageView btnAbout = findViewById(R.id.btnAbout);
        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);

        // Configuración de color global (si aplica)
        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        btnLogin.setBackgroundColor(currentColor);

        // Configurar funcionalidad de mostrar/ocultar contraseña
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility(inputPassword, btnTogglePassword));

        // Conectar al servidor
        connectToServer();

        // Configurar botón About
        btnAbout.setOnClickListener(v -> showAboutDialog());

        // Lógica para el botón de Login
        btnLogin.setOnClickListener(v -> handleLogin(inputEmail.getText().toString(), inputPassword.getText().toString()));

        // Lógica para el botón de Registro
        btnAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Lógica para el botón de Recuperación de Contraseña
        btnRecoverPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PublicarCasaActivity.class);
            startActivity(intent);
        });

        // Lógica para Remember Me
        rememberMeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                //startActivity(intent); // Descomentar si es necesario
            }
        });
    }

    // Método para alternar la visibilidad de la contraseña
    private void togglePasswordVisibility(EditText inputPassword, ImageView btnTogglePassword) {
        if (isPasswordVisible) {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_closed);
        } else {
            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        inputPassword.setSelection(inputPassword.length());
    }

    // Método para conectarse al servidor
    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("172.18.251.41", 3535);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        handleServerResponse(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para manejar el login y pasar a HomeActivity
    private void handleLogin(String email, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("Login").append("_").append(email).append("_").append(password);
        String message = sb.toString();

        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                    // Cambiar a la nueva actividad HomeActivity tras enviar la solicitud
                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, PublicarCasaActivity.class);
                        startActivity(intent);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para manejar la respuesta del servidor
    private void handleServerResponse(String response) {
        Log.d("MainActivity", "Mensaje recibido: " + response);
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Respuesta del Servidor");
            builder.setMessage(response);
            builder.setPositiveButton("OK", null);
            builder.show();
        });
    }

    // Método para mostrar el diálogo "About"
    private void showAboutDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.dialog_about, null))
                .setPositiveButton(getString(R.string.msjabtActivity), (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }
}
