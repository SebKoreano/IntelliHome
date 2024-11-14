package com.example.intellihome;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetHousesList extends Application {
    private String ip = "192.168.18.134";
    private List<String> listaCasas = new ArrayList<>();

    public void obtenerNombresViviendas() {
        // Crear un socket para conectar con el servidor
        try (Socket socket = new Socket(ip, 3535)) {

            Log.d("CreacionListaCasas", "Se creo socket");
            // Enviar el mensaje "NombresViviendas" al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("NombresViviendas");

            // Recibir la respuesta del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = entrada.readLine();

            Log.d("CreacionListaCasas", "Se recibió mensaje");

            // Retornar la respuesta que es la cadena de nombres de archivos
            listaCasas(respuesta);

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public void listaCasas(String cadena) {
        // Verificar si la cadena es null o vacía
        if (cadena == null || cadena.isEmpty()) {
            Log.d("CreacionListaCasas", "El mensaje de entrada con nombres de vivienda es vacío.");
        }

        Log.d("CreacionListaCasas", "Se creó listas con nombres de casas.");
        // Dividir la cadena en una lista usando "_" como delimitador
        String[] partes = cadena.split("_");

        // Convertir el arreglo en una lista y retornarla
        listaCasas = Arrays.asList(partes);
    }

    public List<String> getListaCasas()
    {
        return listaCasas;
    }
}
