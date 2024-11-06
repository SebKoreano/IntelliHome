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

    public GetHouseInfo(String house) {
        this.nombreCasa = house;
        this.uriList = new ArrayList<>();
        iniciarInfo();
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

    public void iniciarInfo()
    {
        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                socket = new Socket("192.168.18.5", 3535); //192.168.18.206

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        handleServerResponse(message); // Llama a handleServerResponse con el mensaje recibido
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try {
                if (out != null) {
                    out.println("ObtenerInformacionVivienda_" + this.nombreCasa);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void handleServerResponse(String messageWithHouseInfo) {
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
    public String getDuenoDeVivienda() {
        return DuenoDeVivienda;
    }

    public String getDescripcionGeneral() {
        return DescripcionGeneral;
    }

    public String getNumeroHabitaciones() {
        return NumeroHabitaciones;
    }

    public String getPrecio() {
        return Precio;
    }

    public String getLongitud() {
        return Longitud;
    }

    public String getLatitud() {
        return Latitud;
    }

    public List<String> getAmenidades() {
        return Amenidades;
    }
}