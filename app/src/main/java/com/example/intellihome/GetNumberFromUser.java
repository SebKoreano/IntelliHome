package com.example.intellihome;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class GetNumberFromUser extends Application {
    private String ip = "192.168.18.5";
    private String numUsuario;
    private String nombreUsuario;

    public GetNumberFromUser(String nombre)
    {
        this.nombreUsuario = nombre;
        obtenerNumeroDeUsuario();
    }

    public void obtenerNumeroDeUsuario()
    {
        // Crear un socket para conectar con el servidor
        try (Socket socket = new Socket(ip, 3535)) {

            Log.d("ObtencionNumeroTelefono", "Se creo socket");
            // Enviar el mensaje "NombresViviendas" al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("leerInformacionDeUsuario_Telefono_" + nombreUsuario);

            // Recibir la respuesta del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = entrada.readLine();

            numUsuario = respuesta;
            Log.d("ObtencionNumeroTelefono", "Se recibió numero de usuario" + nombreUsuario);


        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public String getNum()
    {
        return numUsuario;
    }
}
