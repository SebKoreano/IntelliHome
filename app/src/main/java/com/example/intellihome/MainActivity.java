package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import androidx.appcompat.app.AlertDialog;
import java.util.Scanner;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar el diseño XML
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        // Obtener referencias a los elementos del layout
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        CheckBox rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnAlreadyHaveAccount = findViewById(R.id.btnAlreadyHaveAccount);
        TextView btnRecoverPassword = findViewById(R.id.btnRecoverPassword);
        ImageView btnAbout = findViewById(R.id.btnAbout);
        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);
        View background = findViewById(R.id.background);
        TextView title = findViewById(R.id.appName);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        btnLogin.setBackgroundColor(currentColor);
        background.setBackgroundColor(currentColor);
        title.setTextColor(currentColor);

        //Accion del boton para ver o no la contraseña
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Si la contraseña es visible, ocultarla
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_closed);  // Cambiar el ícono al ojo cerrado
            } else {
                // Si la contraseña está oculta, mostrarla
                inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_open);  // Cambiar el ícono al ojo abierto
            }
            isPasswordVisible = !isPasswordVisible; // Alternar el estado
            inputPassword.setSelection(inputPassword.length()); // Mantener el cursor al final del texto
        });

        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                socket = new Socket(globalColor.getIp(), 3535); //192.168.18.206

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

        // Configurar el evento onClick del botón para mostrar el diálogo "About"
        btnAbout.setOnClickListener(v -> showAboutDialog());

        // Lógica para el botón de Login
        btnLogin.setOnClickListener(v -> {
            // Acciones para el botón de Log-In
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
                return;
            }
        });

        btnAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnRecoverPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent);
        });
    }

    // Método para mostrar el diálogo "About"
    private void showAboutDialog() {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.dialog_about, null))
                // Reemplaza el texto del botón "Cerrar" con el string de recursos
                .setPositiveButton(getString(R.string.msjabtActivity), (dialog, id) -> dialog.dismiss());

        // Mostrar el cuadro de diálogo
        builder.create().show();
    }

    private void moveToMainPage() {
        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    // Método para manejar la respuesta del servidor
    private void handleServerResponse(String response) {
        // Registrar el mensaje recibido
        Log.d("MainActivity", "Mensaje recibido: " + response);
        if (response.trim().equals("Fallo en contraseña") || response.trim().equals("Fallo en usuario")){
            // Mostrar la respuesta en un AlertDialog
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Tipo de Fallo");
                builder.setMessage(response);
                builder.setPositiveButton("OK", null);
                builder.show();
            });
            
        }
        else {
            String[] parts = response.split("_");
            String user = parts[0];
            String tipo = parts[1];

            String direccionCreacionCarpeta = "Usuarios/" + tipo + "/" + user + "/" + "Colores.txt";

            //Se crean variables globales con nombre de usuario
            GlobalColor globalVariables = (GlobalColor) getApplication();

            globalVariables.setCurrentuserName(user);
            globalVariables.setCurrenttipoUsuario(tipo);

            //Aquí falta cambiar el color con base en lo escrito en Colores.txt

            moveToMainPage();
        }
    }

}