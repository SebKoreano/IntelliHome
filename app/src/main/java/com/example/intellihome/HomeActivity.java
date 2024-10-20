package com.example.intellihome;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

        // Configuración del Spinner de "Tipo de Casa"
        Spinner selectCasa = findViewById(R.id.spinnerTipoCasa);
        String[] casas = {
                getString(R.string.apartaRegisterActivity),
                getString(R.string.casacampRegisterActivity),
                getString(R.string.casaplaRegisterActivity),
                getString(R.string.cabañaRegisterActivity),
                getString(R.string.pisocciuRegisterActivity)
        };
        ArrayAdapter<String> casaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, casas);
        casaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCasa.setAdapter(casaAdapter);

        // Configuración del Spinner de "Vehículo"
        Spinner selectVehiculo = findViewById(R.id.spinnerVehiculo);
        String[] vehiculos = {getString(R.string.x4RegisterActivity), getString(R.string.pickupRegisterActivity), getString(R.string.sedanRegisterActivity), getString(R.string.suvRegisterActivity), getString(R.string.camionetaRegisterActivity)};
        ArrayAdapter<String> vehiculoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehiculos);
        vehiculoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVehiculo.setAdapter(vehiculoAdapter);
    }
}
