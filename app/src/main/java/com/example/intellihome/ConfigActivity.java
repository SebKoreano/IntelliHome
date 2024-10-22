package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConfigActivity extends AppCompatActivity {

    private RadioGroup radioGroupLanguage;
    private Button btnHelp, btnTheme;
    private Socket socket; // Socket para la conexión
    private PrintWriter out; // PrintWriter para enviar datos
    private Scanner in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization); // Asegúrate de que el nombre del layout sea correcto

        btnHelp = findViewById(R.id.btnHelp);
        btnTheme = findViewById(R.id.btnTheme);

        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        btnHelp.setBackgroundColor(currentColor);
        btnTheme.setBackgroundColor(currentColor);

        // Conexión al servidor
        connectToServer("172.18.83.115", 3535); //192.168.18.206

        // Configura los botones
        btnHelp.setOnClickListener(view -> showHelp());
        btnTheme.setOnClickListener(view -> changeTheme());
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
                        runOnUiThread(() -> Log.d("ConfigActivity", "Mensaje recibido: " + message));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showHelp() {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help, null);

        // Construir el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Establecer la vista inflada
        builder.setView(dialogView);

        // Crear el mensaje de ayuda
        String mensaje = "Funcionalidad general de aplicación:\n\n" +
                "Acceso a cuenta:\n" +
                "Login: Ingrese las credenciales solicitadas para dirigirse al menú principal de la aplicación.\n" +
                "Crear Cuenta: Cree una cuenta con diferentes credenciales que le serán solicitadas.\n" +
                "Recuperación: En caso de olvidar contraseña, ingrese el correo que utilizó para crear cuenta, se enviará una nueva contraseña.\n\n" +
                "Personalización:\n" +
                "   Cambiar paleta de colores: Se puede cambiar la paleta de colores de la aplicación.\n" +
                "   Idioma: Este se adaptará al que utilice en su dispositivo.";

        // Buscar el TextView dentro del layout inflado y establecer el texto
        TextView textView = dialogView.findViewById(R.id.helpMe);
        textView.setText(mensaje);

        // Agregar el botón de cerrar
        builder.setPositiveButton(getString(R.string.msjabtActivity), (dialog, id) -> dialog.dismiss());

        // Mostrar el cuadro de diálogo
        builder.create().show();
    }

    private void changeTheme() {
        Intent intent = new Intent(ConfigActivity.this, ColorWheel.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el socket si está abierto
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
