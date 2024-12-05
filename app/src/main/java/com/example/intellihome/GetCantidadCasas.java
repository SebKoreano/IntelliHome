package com.example.intellihome;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GetCantidadCasas extends Application {
    private String ip = "192.168.18.5";
    private int numCasas;
    Boolean cantidadRecibida = obtenerNombresViviendas();

    public Boolean obtenerNombresViviendas() {
        // Crear un socket para conectar con el servidor
        try (Socket socket = new Socket(ip, 3535)) {

            Log.d("ObtencionCantidadDeCasas", "Se creo socket");
            // Enviar el mensaje "NombresViviendas" al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("CantidadCasas");

            // Recibir la respuesta del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = entrada.readLine();
            procesarNumViviendas(respuesta);

            Log.d("ObtencionCantidadDeCasas", "Se obtuvo cantidad de casas");

            return true;

        } catch (IOException e) {
            Log.d("ObtencionCantidadDeCasas", "Error al obtener cantidad de casas");
            return false;
        }
    }

    public void procesarNumViviendas(String respuesta) {
        try {
            numCasas = Integer.parseInt(respuesta);
            // Aquí puedes trabajar con el número convertido
            Log.d("ObtencionCantidadDeCasas", "Se paso de string a int con exito:" + respuesta);
        } catch (NumberFormatException e) {
            Log.d("ObtencionCantidadDeCasas", "Error al pasasr de string a int");
        }
    }

}
