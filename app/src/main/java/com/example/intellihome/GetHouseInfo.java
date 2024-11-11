package com.example.intellihome;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetHouseInfo extends Application {
    private String nombreCasa;
    private List<Uri> uriList;
    private String DuenoDeVivienda;
    private String DescripcionGeneral;
    private String NumeroHabitaciones;
    private String Precio;
    private String Longitud;
    private String Latitud;
    private List<String> Amenidades = new ArrayList<>();

    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private GlobalColor globalVaribles;

    public GetHouseInfo(String informacionDeCasa) {
        this.uriList = new ArrayList<>();
        asignarAtributo(informacionDeCasa);
    }

    public Task<List<Uri>> getHouseImageUris() {
        String direccionCarpeta = "Viviendas Arrendadas/" + nombreCasa;
        StorageReference folderRef = FirebaseStorage.getInstance().getReference(direccionCarpeta);

        TaskCompletionSource<List<Uri>> taskCompletionSource = new TaskCompletionSource<>();

        folderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<Task<Uri>> uriTasks = new ArrayList<>();
                    for (StorageReference fileRef : listResult.getItems()) {
                        Task<Uri> uriTask = fileRef.getDownloadUrl();
                        uriTasks.add(uriTask);
                    }

                    Tasks.whenAllSuccess(uriTasks)
                            .addOnSuccessListener(results -> {
                                List<Uri> uris = new ArrayList<>();
                                for (Object result : results) {
                                    if (result instanceof Uri) {
                                        uris.add((Uri) result);
                                    }
                                }
                                uriList.clear();
                                uriList.addAll(uris);
                                taskCompletionSource.setResult(uriList);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("GetHouseInfo", "Error al obtener los Uris: " + e.getMessage());
                                taskCompletionSource.setException(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("GetHouseInfo", "Error al listar archivos: " + e.getMessage());
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public void asignarAtributo(String messageWithHouseInfo) {
        String[] datos = messageWithHouseInfo.split("_");

        for (String dato : datos) {
            if (dato.startsWith("DuenoDeVivienda:")) {
                this.DuenoDeVivienda = dato.replace("DuenoDeVivienda:", "").trim();
            } else if (dato.startsWith("DescripcionGeneral:")) {
                this.DescripcionGeneral = dato.replace("DescripcionGeneral:", "").trim();
            } else if (dato.startsWith("NumeroHabitaciones:")) {
                this.NumeroHabitaciones = dato.replace("NumeroHabitaciones:", "").trim();
            } else if (dato.startsWith("Precio:")) {
                this.Precio = dato.replace("Precio:", "").trim();
            } else if (dato.startsWith("Longitud:")) {
                this.Longitud = dato.replace("Longitud:", "").trim();
            } else if (dato.startsWith("Latitud:")) {
                this.Latitud = dato.replace("Latitud:", "").trim();
            } else if (dato.startsWith("Amenidad")) {
                this.Amenidades.add(dato.replace("Amenidad", "").replaceAll("\\d*:", "").trim());
            }
        }
    }

    // Getters
    public String getDuenoDeVivienda() { return DuenoDeVivienda; }
    public String getDescripcionGeneral() { return DescripcionGeneral; }
    public String getNumeroHabitaciones() { return NumeroHabitaciones; }
    public String getPrecio() { return Precio; }
    public String getLongitud() { return Longitud; }
    public String getLatitud() { return Latitud; }
    public List<String> getAmenidades() { return Amenidades; }
}
