package com.example.intellihome;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LightControlActivity extends AppCompatActivity {

    private Button btnCuartoPrincipal, btnBanoCuartoPrincipal, btnCuarto1, btnCuarto2;
    private Button btnSala, btnBano, btnLavanderia;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private PrintWriter outArd;
    private Scanner inArd;
    private String humedad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control);

        // Inicialización de los botones de la interfaz
        btnCuartoPrincipal = findViewById(R.id.btnCuartoPrincipal);
        btnBanoCuartoPrincipal = findViewById(R.id.btnBanoCuartoPrincipal);
        btnCuarto1 = findViewById(R.id.btnCuarto1);
        btnCuarto2 = findViewById(R.id.btnCuarto2);
        btnSala = findViewById(R.id.btnSala);
        btnBano = findViewById(R.id.btnBano);
        btnLavanderia = findViewById(R.id.btnLavanderia);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        btnCuartoPrincipal.setBackgroundColor(currentColor);
        btnBanoCuartoPrincipal.setBackgroundColor(currentColor);
        btnCuarto1.setBackgroundColor(currentColor);
        btnCuarto2.setBackgroundColor(currentColor);
        btnSala.setBackgroundColor(currentColor);
        btnBano.setBackgroundColor(currentColor);
        btnLavanderia.setBackgroundColor(currentColor);

        // Configuración de listeners para cada botón
        btnCuartoPrincipal.setOnClickListener(v -> sendMessage("Z"));
        btnBanoCuartoPrincipal.setOnClickListener(v -> sendMessage("X"));
        btnCuarto1.setOnClickListener(v -> sendMessage("C"));
        btnCuarto2.setOnClickListener(v -> sendMessage("V"));
        btnSala.setOnClickListener(v -> sendMessage("B"));
        btnBano.setOnClickListener(v -> sendMessage("N"));
        btnLavanderia.setOnClickListener(v -> sendMessage("M"));

        // Hilo que establece la conexión con el servidor de Rasb
        new Thread(() -> {
            try {
                // Conectar a la dirección IP y puerto del servidor
                socket = new Socket("192.168.0.107", 6969);
                // Inicializar el PrintWriter para enviar datos al servidor
                out = new PrintWriter(socket.getOutputStream(), true);
                // Inicializar el Scanner para recibir datos del servidor (actualmente no se usa)
                in = new Scanner(socket.getInputStream());

            } catch (Exception e) {
                // Si hay un error en la conexión, mostrar un mensaje de error en la UI
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();  // Iniciar el hilo de conexión



        // Iniciar el hilo para conectarse a Arduino por medio de Server.
        new Thread(() -> {
            try {
                socket = new Socket("192.168.18.206", 3535); //192.168.18.206

                outArd = new PrintWriter(socket.getOutputStream(), true);
                inArd = new Scanner(socket.getInputStream());

                while (true) {
                    if (inArd.hasNextLine()) {
                        String message = inArd.nextLine();
                        sensorMessageProcesor(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para enviar un mensaje al servidor (en un hilo separado para evitar bloqueo en la UI)
    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (out != null) {  // Verificar que el PrintWriter esté inicializado
                    out.println(message);  // Enviar el mensaje al servidor
                    // Mostrar un mensaje de éxito en la UI
                    runOnUiThread(() -> mostrarMensaje("Éxito: Mensaje enviado para controlar las luces."));
                }
            } catch (Exception e) {
                // Mostrar un mensaje de error en la UI si ocurre un problema al enviar el mensaje
                runOnUiThread(() -> Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();  // Iniciar el hilo de envío de mensajes
    }

    // Método llamado cuando la actividad se destruye (por ejemplo, al cerrar la app)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el socket si aún está conectado
        if (socket != null) {
            try {
                socket.close();  // Cerrar la conexión al servidor
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para mostrar mensajes en forma de Toast (pequeños mensajes emergentes en la UI)
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();  // Mostrar el mensaje en pantalla
    }

    //Esta funcion debe llamarse con "SERVO_90" para girar 90 grados o "SERVO_0" para devolver a posicion inicial.
    public void sendArduinoMessage(String message){
        new Thread(() -> {
            try {
                if (outArd != null) {
                    outArd.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sensorMessageProcesor(String message){
        if (message.startsWith("F1")){//Indica que el fuego se encendió

        }

        else if (message.startsWith("F2")) { //Indicador de que el fuego prendido ahora está apagado

        }

        else if (message.startsWith("T")) { //Indicador de que tembló

        }

        else if (message.startsWith("Humedad:")) { //Indicador de humedad
            humedad = message.substring(8);
        }
    }

}