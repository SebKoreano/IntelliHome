package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.net.Socket;
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
        setContentView(R.layout.activity_recoverpassword);

        Correo = findViewById(R.id.correorecuper);
        confirmation_but = findViewById(R.id.button_recuerpa);
        View background = findViewById(R.id.background);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        confirmation_but.setBackgroundColor(currentColor);
        background.setBackgroundColor(currentColor);


        // Obtener la IP de forma asíncrona
        new Thread(() -> {
            runOnUiThread(() -> {
                // Una vez que se obtiene la IP, conectarse al servidor
                connectToServer(globalColor.getIp(), 3535); //192.168.18.206
            });
        }).start();
        confirmation_but.setOnClickListener(view -> {
            String phone = Correo.getText().toString();
            if (!phone.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Recuperacion").append("_").append(phone);
                String message = sb.toString();
                sendMessage(message);
                regresarLogIn();
            } else {
                Toast.makeText(RecuperationActivity.this, "Por favor ingrese un correo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regresarLogIn() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

