package com.example.intellihome;
import android.app.Application;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ArduinoConection extends Application {
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    public ArduinoConection(){
        crearConexion();
    }

    public void crearConexion()
    {
        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                socket = new Socket("192.168.18.206", 3535); //192.168.18.206

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        analizarRespuestaArduino(message); // Llama a handleServerResponse con el mensaje recibido
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendArduinoMessage(String message){
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

    public void analizarRespuestaArduino(String message){

    }
}
