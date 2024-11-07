package com.example.intellihome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
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
    private Button btnFuego, btnSismos, btnHumedad, btnPuerta;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

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

    private void setButtonToggleIcon(Button button) {
        button.setTag(false);

        button.setOnClickListener(v -> {
            boolean isOn = (boolean) button.getTag();

            isOn = !isOn;
            button.setTag(isOn);
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, isOn ? R.drawable.ic_lightbulb : R.drawable.ic_lightbulb_off, 0);
        });
    }

    public void validarDispositivo() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, getString(R.string.biometric_no_hardware), Toast.LENGTH_LONG).show();
        }
    }

    public void ejecutarBiometria(){
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_invalid_fingerprint), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_valid_fingerprint), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), getString(R.string.biometric_authentication_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}