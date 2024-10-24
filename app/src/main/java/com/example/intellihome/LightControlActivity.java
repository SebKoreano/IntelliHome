package com.example.intellihome;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LightControlActivity extends AppCompatActivity {

    private Button btnCuartoPrincipal, btnBanoCuartoPrincipal, btnCuarto1, btnCuarto2;
    private Button btnSala, btnBano, btnLavanderia;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control);

        // Inicialización de los botones
        btnCuartoPrincipal = findViewById(R.id.btnCuartoPrincipal);
        btnBanoCuartoPrincipal = findViewById(R.id.btnBanoCuartoPrincipal);
        btnCuarto1 = findViewById(R.id.btnCuarto1);
        btnCuarto2 = findViewById(R.id.btnCuarto2);
        btnSala = findViewById(R.id.btnSala);
        btnBano = findViewById(R.id.btnBano);
        btnLavanderia = findViewById(R.id.btnLavanderia);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        btnCuartoPrincipal.setBackgroundColor(currentColor);
        btnBanoCuartoPrincipal.setBackgroundColor(currentColor);
        btnCuarto1.setBackgroundColor(currentColor);
        btnCuarto2.setBackgroundColor(currentColor);
        btnSala.setBackgroundColor(currentColor);
        btnBano.setBackgroundColor(currentColor);
        btnLavanderia.setBackgroundColor(currentColor);

        // Configuración de listeners para cada botón
        btnCuartoPrincipal.setOnClickListener(v -> sendMessage("Z"));
        btnBanoCuartoPrincipal.setOnClickListener(v -> sendMessage("X"));
        btnCuarto1.setOnClickListener(v -> sendMessage("C"));
        btnCuarto2.setOnClickListener(v -> sendMessage("V"));
        btnSala.setOnClickListener(v -> sendMessage("B"));
        btnBano.setOnClickListener(v -> sendMessage("N"));
        btnLavanderia.setOnClickListener(v -> sendMessage("M"));

        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                socket = new Socket("192.168.18.134", 6969);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                    runOnUiThread(() -> mostrarMensaje("Éxito: Mensaje enviado para controlar las luces."));
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el socket si está conectado
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}