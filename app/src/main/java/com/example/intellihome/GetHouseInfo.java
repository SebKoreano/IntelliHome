package com.example.intellihome;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GetHouseInfo extends Application {
    private String propietario;
    private String houseName;
    private String descripcion;
    private String longitud;
    private String latitud;
    private String carro;
    private String tipoCasa;
    private List<String> Amenidades;
    private List<String> Reglas;

    public GetHouseInfo(String houseName) {
        this.houseName = houseName;
        this.Amenidades = new ArrayList<>(); // Inicializa la lista de amenidades
        this.Reglas = new ArrayList<>(); // Inicializa la lista de reglas
        SetAllInfo();
    }

    public void SetAllInfo() {
        String ubicacion = "Viviendas Arrendadas/" + this.houseName;
        procesarDatos(obtenerNombresDeElementos(ubicacion));
    }

    public List<String> getReglas() {
        return this.Reglas;
    }

    public List<String> getAmenidades() {
        return this.Amenidades;
    }

    public String getPropietario() {
        return this.propietario;
    }

    public String getHouseName() {
        return this.houseName;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getLongitud() {
        return this.longitud;
    }

    public String getLatitud() {
        return this.latitud;
    }

    public String getCarro() {
        return this.carro;
    }

    public String getTipoCasa() {
        return this.tipoCasa;
    }

    private List<String> obtenerNombresDeElementos(String carpetaBase) {
        // Inicializa FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Referencia a la carpeta base pasada como parámetro
        StorageReference carpetaRef = storageRef.child(carpetaBase);

        // Lista para guardar los nombres de carpetas y archivos
        List<String> listaElementos = new ArrayList<>();

        // Llama a listAll() para obtener todos los items dentro de la carpeta especificada
        carpetaRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // Obtén los directorios dentro de la carpeta base (subcarpetas)
                for (StorageReference prefix : listResult.getPrefixes()) {
                    // Extrae el nombre de cada carpeta
                    String folderName = prefix.getName();
                    listaElementos.add("Carpeta: " + folderName.trim());
                }

                // Obtén los archivos dentro de la carpeta base
                for (StorageReference item : listResult.getItems()) {
                    // Extrae el nombre de cada archivo
                    String fileName = item.getName();
                    listaElementos.add("Archivo: " + fileName.trim());
                }

                // Opcional: Convierte la lista de nombres a un solo string para visualizar
                StringBuilder elementosString = new StringBuilder();
                for (String nombre : listaElementos) {
                    elementosString.append(nombre).append("\n");
                }
            }
        }).addOnFailureListener(e -> {
            System.err.println("Error al obtener los elementos: " + e.getMessage());
        });

        return listaElementos;
    }

    public void procesarDatos(List<String> lineas) {
        // Procesar cada línea y separarla en clave y valor
        for (String linea : lineas) {
            Log.d("GetHouseInfo", "Procesando línea: " + linea); // Agrega log para depuración
            String[] partes = linea.split(":", 2);
            if (partes.length == 2) {
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                Log.d("GetHouseInfo", "Clave: " + clave + ", Valor: " + valor); // Agrega log para depuración

                // Asignar valores según la clave
                switch (clave) {
                    case "DuenoDeVivienda":
                        this.propietario = valor;
                        break;
                    case "DescripcionGeneral":
                        this.descripcion = valor;
                        break;
                    case "Longitud":
                        this.longitud = valor;
                        break;
                    case "Latitud":
                        this.latitud = valor;
                        break;
                    case "VehiculoPreferencia":
                        this.carro = valor;
                        break;
                    case "TipoCasa":
                        this.tipoCasa = valor;
                        break;
                    default:
                        // Procesar amenidades y reglas
                        if (clave.startsWith("Amenidad")) {
                            this.Amenidades.add(valor);
                        } else if (clave.startsWith("Regla")) {
                            this.Reglas.add(valor);
                        }
                        break;
                }
            }
        }
    }
}
