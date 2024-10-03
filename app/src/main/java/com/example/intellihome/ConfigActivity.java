package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
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

        // Inicializa los elementos de la interfaz
        radioGroupLanguage = findViewById(R.id.radioGroupLanguage);
        btnHelp = findViewById(R.id.btnHelp);
        btnTheme = findViewById(R.id.btnTheme);

        // Conexión al servidor
        connectToServer("192.168.18.5", 3535);

        // Configura los botones
        btnHelp.setOnClickListener(view -> showHelp());
        btnTheme.setOnClickListener(view -> CambiarTema());
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
        // Lógica para mostrar ayuda
        Toast.makeText(this, "Ayuda solicitada", Toast.LENGTH_SHORT).show();
    }

    private void CambiarTema() {
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
