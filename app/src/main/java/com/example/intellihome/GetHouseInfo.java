package com.example.intellihome;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GetHouseInfo extends Application {
    private String nombreCasa;
    private List<Uri> uriList;

    public GetHouseInfo(String house) {
        this.nombreCasa = house;
        this.uriList = new ArrayList<>();
    }

    // Método para obtener todos los Uris de las imágenes de una casa
    public Task<List<Uri>> getHouseImageUris() {
        // Crear la referencia a la carpeta de imágenes de la casa
        String direccionCarpeta = "Viviendas Arrendadas/" + nombreCasa;
        StorageReference folderRef = FirebaseStorage.getInstance().getReference(direccionCarpeta);

        // Crear una tarea que recolecta todos los Uris
        TaskCompletionSource<List<Uri>> taskCompletionSource = new TaskCompletionSource<>();

        // Listar todos los archivos en la carpeta
        folderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<Task<Uri>> uriTasks = new ArrayList<>();

                    // Para cada archivo en la lista, obtener el Uri de descarga
                    for (StorageReference fileRef : listResult.getItems()) {
                        Task<Uri> uriTask = fileRef.getDownloadUrl();
                        uriTasks.add(uriTask);
                    }

                    // Cuando todos los Uris se hayan obtenido, almacenar en la lista uriList
                    Tasks.whenAllSuccess(uriTasks)
                            .addOnSuccessListener(results -> {
                                List<Uri> uris = new ArrayList<>(); // Crear lista de Uri explícitamente
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
}
