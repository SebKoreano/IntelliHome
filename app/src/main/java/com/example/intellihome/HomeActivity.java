package com.example.intellihome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private EditText descripcionInput, precioInput;
    private NumberPicker numHabitacionesPicker;
    private Button btnAddReglas, btnAddAmenidades;
    private Button btnPhoto;
    private Spinner spinnerTipoCasa, spinnerVehiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar vistas
        descripcionInput = findViewById(R.id.inputDescripcion);
        precioInput = findViewById(R.id.inputPrecioPorNoche);
        numHabitacionesPicker = findViewById(R.id.numHabitacionesPicker);
        btnAddReglas = findViewById(R.id.btnAddReglas);
        btnAddAmenidades = findViewById(R.id.btnAddAmenidades);
        btnPhoto = findViewById(R.id.btnProfilePhoto);
        spinnerTipoCasa = findViewById(R.id.spinnerTipoCasa);
        spinnerVehiculo = findViewById(R.id.spinnerVehiculo);

        // Configurar el NumberPicker
        numHabitacionesPicker.setMinValue(1);  // Valor mínimo
        numHabitacionesPicker.setMaxValue(99); // Valor máximo

        // Acción del botón para añadir reglas
        btnAddReglas.setOnClickListener(v -> {
            Toast.makeText(this, "Añadir reglas clicado", Toast.LENGTH_SHORT).show();
            // Lógica para añadir reglas
        });

        // Acción del botón para añadir amenidades
        btnAddAmenidades.setOnClickListener(v -> {
            Toast.makeText(this, "Añadir amenidades clicado", Toast.LENGTH_SHORT).show();
            // Lógica para añadir amenidades
        });

        // Acción del botón para añadir foto de perfil
        btnPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Añadir foto clicado", Toast.LENGTH_SHORT).show();
            // Lógica para seleccionar la foto
        });

        // Acción del spinner para tipo de casa
        spinnerTipoCasa.setOnItemSelectedListener(new CustomItemSelectedListener(this, "Tipo de casa"));

        // Acción del spinner para vehículo
        spinnerVehiculo.setOnItemSelectedListener(new CustomItemSelectedListener(this, "Vehículo"));
    }
}
