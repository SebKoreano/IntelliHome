package com.example.intellihome;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
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
    private String HouseType;
    private String vehicleType;
    private List<String> Amenidades = new ArrayList<>();


    public GetHouseInfo(String informacionDeCasa) {
        Log.d("CreacionDeObjetoCasa", "Se creó objeto casa");
        this.uriList = new ArrayList<>();
        asignarAtributo(informacionDeCasa);
    }

    public Task<Uri> getHouseImageUri() {
        String direccionCarpeta = "Viviendas Arrendadas/" + nombreCasa;
        StorageReference folderRef = FirebaseStorage.getInstance().getReference(direccionCarpeta);

        TaskCompletionSource<Uri> taskCompletionSource = new TaskCompletionSource<>();

        folderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // Buscar el archivo con nombre "imagen0"
                    for (StorageReference fileRef : listResult.getItems()) {
                        if (fileRef.getName().equals("Imagen0")) {
                            // Obtener el Uri de la imagen específica
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> taskCompletionSource.setResult(uri))
                                    .addOnFailureListener(e -> {
                                        Log.e("GetHouseImageUri", "Error al obtener el Uri: " + e.getMessage());
                                        taskCompletionSource.setException(e);
                                    });
                            return; // Salir del bucle una vez encontrado
                        }
                    }
                    // Si no se encuentra la imagen
                    taskCompletionSource.setException(new Exception("Imagen 'imagen0' no encontrada en la carpeta"));
                })
                .addOnFailureListener(e -> {
                    Log.e("GetHouseImageUri", "Error al listar archivos: " + e.getMessage());
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public void procesarImagen(Uri imageUri) {
        // Aquí puedes usar directamente el Uri para lo que necesites
        Log.d("ProcesarImagen", "Procesando la imagen con Uri: " + imageUri.toString());
    }

    public void downloadImage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri.toString());

        // Definir el archivo de destino en el almacenamiento local
        File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "imagen_descargada.png");

        storageReference.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    // El archivo se ha descargado con éxito
                    Log.d("DownloadImage", "Imagen descargada correctamente en: " + localFile.getAbsolutePath());
                })
                .addOnFailureListener(e -> {
                    // Si ocurre algún error al descargar el archivo
                    Log.e("DownloadImage", "Error al descargar la imagen: " + e.getMessage());
                });
    }



    public void asignarAtributo(String messageWithHouseInfo) {
        String[] datos = messageWithHouseInfo.split("_");
        Log.d("CreacionDeObjetoCasa", "Se inicia asignacion de datos de casa:" + messageWithHouseInfo);
        for (String dato : datos) {
            if (dato.startsWith("DuenoDeVivienda:")) {
                this.DuenoDeVivienda = dato.replace("DuenoDeVivienda:", "").trim();
                Log.d("GenerarInstancia", "Dueño:" + this.DuenoDeVivienda);

            } else if (dato.startsWith("DescripcionGeneral:")) {
                this.DescripcionGeneral = dato.replace("DescripcionGeneral:", "").trim();
                Log.d("GenerarInstancia", "Descr:" + this.DescripcionGeneral);

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
            } else if (dato.startsWith("NombreDeVivienda:")) {
                this.nombreCasa = dato.replace("NombreDeVivienda:", "");
            } else if (dato.startsWith("TipoCasa:")) {
                this.HouseType = dato.replace("TipoCasa:", "");
            } else if (dato.startsWith("VehiculoPreferencia:")) {
                this.vehicleType = dato.replace("VehiculoPreferencia:", "");
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
    public String getNombreCasa() { return nombreCasa; }
    public String getHouseType() {return HouseType; }
    public String getVehicleType() {return vehicleType; }
}
