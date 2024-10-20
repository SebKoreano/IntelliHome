package com.example.intellihome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LightControlActivity extends AppCompatActivity {

    // Declaración de botones
    private Button btnCuartoPrincipal, btnBanoCuartoPrincipal, btnCuarto1, btnCuarto2;
    private Button btnSala, btnBano, btnLavanderia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control); // Asegúrate de que este archivo XML exista

        // Inicialización de los botones
        btnCuartoPrincipal = findViewById(R.id.btnCuartoPrincipal);
        btnBanoCuartoPrincipal = findViewById(R.id.btnBanoCuartoPrincipal);
        btnCuarto1 = findViewById(R.id.btnCuarto1);
        btnCuarto2 = findViewById(R.id.btnCuarto2);
        btnSala = findViewById(R.id.btnSala);
        btnBano = findViewById(R.id.btnBano);
        btnLavanderia = findViewById(R.id.btnLavanderia);

        // Configuración de listeners para cada botón
        btnCuartoPrincipal.setOnClickListener(v -> controlarLuces("Cuarto Principal"));
        btnBanoCuartoPrincipal.setOnClickListener(v -> controlarLuces("Baño Cuarto Principal"));
        btnCuarto1.setOnClickListener(v -> controlarLuces("Cuarto 1"));
        btnCuarto2.setOnClickListener(v -> controlarLuces("Cuarto 2"));
        btnSala.setOnClickListener(v -> controlarLuces("Sala"));
        btnBano.setOnClickListener(v -> controlarLuces("Baño"));
        btnLavanderia.setOnClickListener(v -> controlarLuces("Lavandería"));
    }

    // Método para controlar las luces
    private void controlarLuces(String habitacion) {
        // Mostrar mensaje de éxito
        Toast.makeText(this, "Éxito: Las luces del " + habitacion + " han sido controladas.", Toast.LENGTH_SHORT).show();
    }
}
