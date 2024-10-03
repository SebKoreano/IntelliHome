package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

public class RecuperationActivity extends AppCompatActivity {
    private EditText Correo;
    private Button confirmation_but;

    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoverpassword); // Asegúrate de que el nombre del layout sea correcto

        Correo = findViewById(R.id.correorecuper); // Cambia esto por el ID correcto de tu EditText
        confirmation_but = findViewById(R.id.button_recuerpa); // Cambia esto por el ID correcto de tu Button

        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        confirmation_but.setBackgroundColor(currentColor);

        // Obtener la IP de forma asíncrona
        new Thread(() -> {
            runOnUiThread(() -> {
                // Una vez que se obtiene la IP, conectarse al servidor
                connectToServer("172.18.251.41", 3535);
            });
        }).start();

        confirmation_but.setOnClickListener(view -> {
            StringBuilder sb = new StringBuilder();
            String phone = Correo.getText().toString();
            sb.append("Recuperacion").append("_");
            sb.append(phone);
            String message = sb.toString();
            sendMessage(message);
            regresarLogIn();
        });
    }

    private void regresarLogIn(){
        Intent intent = new Intent(RecuperationActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
                        runOnUiThread(() -> {
                            Log.d("RecuperationActivity", "Mensaje recibido: " + message);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
