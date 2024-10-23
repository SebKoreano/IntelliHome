package com.example.intellihome;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LightControlActivity extends AppCompatActivity {

    // Declaración de los botones para controlar las luces en diferentes habitaciones
    private Button btnCuartoPrincipal, btnBanoCuartoPrincipal, btnCuarto1, btnCuarto2;
    private Button btnSala, btnBano, btnLavanderia;

    // Variables para la conexión al servidor
    private Socket socket;
    private PrintWriter out;  // Para enviar datos al servidor
    private Scanner in;       // Para recibir datos del servidor (si se implementara)

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

        // Configuración de los listeners (acciones) para cada botón
        // Al hacer clic en cada botón, se envía una letra específica al servidor
        btnCuartoPrincipal.setOnClickListener(v -> sendMessage("Z"));
        btnBanoCuartoPrincipal.setOnClickListener(v -> sendMessage("X"));
        btnCuarto1.setOnClickListener(v -> sendMessage("C"));
        btnCuarto2.setOnClickListener(v -> sendMessage("V"));
        btnSala.setOnClickListener(v -> sendMessage("B"));
        btnBano.setOnClickListener(v -> sendMessage("N"));
        btnLavanderia.setOnClickListener(v -> sendMessage("M"));

        // Hilo que establece la conexión con el servidor
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
}
