package com.example.intellihome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

public class LightControlActivity extends AppCompatActivity {

    private Button btnCuartoPrincipal, btnBanoCuartoPrincipal, btnCuarto1, btnCuarto2;
    private Button btnSala, btnBano, btnLavanderia;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private PrintWriter outArd;
    private Scanner inArd;
    private String humedad;
    private TextView humedadTextView;

    private String phoneNumber = "+50685400752";

    private Button btnFuego, btnSismos, btnHumedad, btnPuerta;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    private boolean puerta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control);

        // Inicialización de TextView para mostrar humedad
        humedadTextView = findViewById(R.id.Humedadvariable);

        // Inicialización de los botones de la interfaz
        btnCuartoPrincipal = findViewById(R.id.btnCuartoPrincipal);
        btnBanoCuartoPrincipal = findViewById(R.id.btnBanoCuartoPrincipal);
        btnCuarto1 = findViewById(R.id.btnCuarto1);
        btnCuarto2 = findViewById(R.id.btnCuarto2);
        btnSala = findViewById(R.id.btnSala);
        btnBano = findViewById(R.id.btnBano);
        btnLavanderia = findViewById(R.id.btnLavanderia);
        btnFuego = findViewById(R.id.Fuego);
        btnHumedad = findViewById(R.id.Humedad);
        btnPuerta = findViewById(R.id.Lock);
        btnSismos = findViewById(R.id.Sismo);

        GlobalColor globalColor = (GlobalColor) getApplication();
        int currentColor =  globalColor.getCurrentColor();

        btnCuartoPrincipal.setBackgroundColor(currentColor);
        btnBanoCuartoPrincipal.setBackgroundColor(currentColor);
        btnCuarto1.setBackgroundColor(currentColor);
        btnCuarto2.setBackgroundColor(currentColor);
        btnSala.setBackgroundColor(currentColor);
        btnBano.setBackgroundColor(currentColor);
        btnLavanderia.setBackgroundColor(currentColor);

        btnSismos.setBackgroundColor(currentColor);
        btnHumedad.setBackgroundColor(currentColor);
        btnPuerta.setBackgroundColor(currentColor);
        btnFuego.setBackgroundColor(currentColor);

        // Configuración de listeners para cada botón
        btnCuartoPrincipal.setOnClickListener(v -> sendMessage("Z"));
        btnBanoCuartoPrincipal.setOnClickListener(v -> sendMessage("X"));
        btnCuarto1.setOnClickListener(v -> sendMessage("C"));
        btnCuarto2.setOnClickListener(v -> sendMessage("V"));
        btnSala.setOnClickListener(v -> sendMessage("B"));
        btnBano.setOnClickListener(v -> sendMessage("N"));
        btnLavanderia.setOnClickListener(v -> sendMessage("M"));
        btnPuerta.setOnClickListener(v -> {
            validarDispositivo();
            ejecutarBiometria();

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_prompt_title))
                    .setSubtitle(getString(R.string.biometric_prompt_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_prompt_cancel))
                    .build();

            biometricPrompt.authenticate(promptInfo);
        });

        // Configuración de listeners para cada botón con alternancia de iconos
        setButtonToggleIcon(btnCuartoPrincipal);
        setButtonToggleIcon(btnBanoCuartoPrincipal);
        setButtonToggleIcon(btnCuarto1);
        setButtonToggleIcon(btnCuarto2);
        setButtonToggleIcon(btnSala);
        setButtonToggleIcon(btnBano);
        setButtonToggleIcon(btnLavanderia);

        // Hilo que establece la conexión con el servidor de Rasb

        new Thread(() -> {
            try {
                // Conectar a la dirección IP y puerto del servidor
                socket = new Socket("192.168.18.134", 6969);
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
                socket = new Socket(globalColor.getIp(), 3535); //192.168.18.206

                outArd = new PrintWriter(socket.getOutputStream(), true);
                inArd = new Scanner(socket.getInputStream());

                while (true) {
                    if (inArd.hasNextLine()) {
                        String message = inArd.nextLine();
                        sensorMessageProcessor(message, globalColor.getIp());
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

    public void sensorMessageProcessor(String message, String ip) {
        try {
            if (message.startsWith("F1")) {
                SensorFuegoPrendido(ip);
            } else if (message.startsWith("F2")) {
                SensorFuegoApagado();
            } else if (message.startsWith("T")) {
                SensorSismos(ip);
            } else if (message.startsWith("Humedad:")) {
                SensorHumedad(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void SensorFuegoPrendido(String ip) {
        runOnUiThread(() -> {
            Toast.makeText(this, "¡SE QUEMA LA CASAAA!", Toast.LENGTH_SHORT).show();
        });
        btnFuego.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_fire, 0);
        String firealertMessage = getString(R.string.FireAlert_Message);
        WhatsAppNotificationHelper.sendWhatsAppMessageViaServer(ip, 3535, phoneNumber, firealertMessage);
        NotificationHelper.sendNotification(this, getString(R.string.FireAlert_Title), R.drawable.ic_fire);
    }

    private void SensorFuegoApagado() {
        btnFuego.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle, 0);
    }

    private void SensorSismos(String ip) {
        runOnUiThread(() -> Toast.makeText(this, "¡Alerta de temblor detectada!", Toast.LENGTH_SHORT).show());
        btnSismos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_earthquake, 0);

        String sismoalertMessage = getString(R.string.EarthquakeAlert_Message);
        WhatsAppNotificationHelper.sendWhatsAppMessageViaServer(ip, 3535, phoneNumber, sismoalertMessage);
        NotificationHelper.sendNotification(this, getString(R.string.EarthquakeAlert_Title), R.drawable.ic_earthquake);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnSismos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle, 0);
        }, 5000);
    }

    private void SensorHumedad(String message) {
        String humedad = message.substring(8);
        runOnUiThread(() -> humedadTextView.setText(humedad + " g/m²"));
    }


    private void setButtonToggleIcon(Button button) {
        button.setTag(false);

        button.setOnClickListener(v -> {
            boolean isOn = (boolean) button.getTag();

            isOn = !isOn;
            button.setTag(isOn);
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, isOn ? R.drawable.ic_lightbulb : R.drawable.ic_lightbulb_off, 0);
        });
    }

    //Aqui se valida que el dispositivo tenga biometria
    public void validarDispositivo() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, getString(R.string.biometric_no_hardware), Toast.LENGTH_LONG).show();
        }
    }

    //Este metodo se encarga de ejecutar la biometria
    public void ejecutarBiometria(){
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
            //Este es el caso de fallo
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_invalid_fingerprint), Toast.LENGTH_SHORT).show();
            }
            //Este es el caso de exito
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                manejarPuerta();
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_valid_fingerprint), Toast.LENGTH_SHORT).show();
            }
            //Este es el caso de error
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_authentication_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Este metodo se encarga de manejar el envio de señales al arduino para el control de la puerta
    public void manejarPuerta(){
        if (!puerta){
            sendArduinoMessage("SERVO_90");
            btnPuerta.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock_open, 0);
            puerta = true;
        }
        else{
            puerta = false;
            btnPuerta.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0);
            sendArduinoMessage("SERVO_0");
        }
    }
}