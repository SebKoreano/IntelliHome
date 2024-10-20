package com.example.intellihome;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private EditText descripcionInput, precioInput;
    private NumberPicker numHabitacionesPicker;
    private Button btnAddReglas, btnAddAmenidades;
    private Button btnPhoto;
    private int numeroReglas = 1, numeroAmenidad= 1;

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
        btnAddReglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevoCampo("regla");
            }
        });

        // Acción del botón para añadir amenidades
        btnAddAmenidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevoCampo("amenidad");
            }
        });

        // Acción del botón para añadir foto de perfil
        btnPhoto.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.btnPhoto), Toast.LENGTH_SHORT).show();
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
        String[] vehiculos = {
                getString(R.string.x4RegisterActivity),
                getString(R.string.pickupRegisterActivity),
                getString(R.string.sedanRegisterActivity),
                getString(R.string.suvRegisterActivity),
                getString(R.string.camionetaRegisterActivity)
        };
        ArrayAdapter<String> vehiculoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehiculos);
        vehiculoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVehiculo.setAdapter(vehiculoAdapter);
    }

    // Método general para agregar un nuevo campo (regla o amenidad)
    private void agregarNuevoCampo(String tipoCampo) {
        // Crear el campo de texto
        EditText nuevoCampo = crearNuevoCampo(tipoCampo);

        // Agregar el campo al layout correspondiente
        agregarCampoAlLayout(nuevoCampo, tipoCampo);

        // Incrementar el contador correspondiente
        incrementarContador(tipoCampo);
    }

    // Método para crear un nuevo EditText con el hint adecuado y el límite de caracteres
    private EditText crearNuevoCampo(String tipoCampo) {
        EditText nuevoCampo = new EditText(this);
        String hint = obtenerHint(tipoCampo);
        nuevoCampo.setHint(hint);
        nuevoCampo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        // Establecer el límite de 100 caracteres
        nuevoCampo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
        return nuevoCampo;
    }

    // Método para obtener el hint según el tipo de campo
    private String obtenerHint(String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            return getString(R.string.reglasHint) + numeroReglas;
        } else if (tipoCampo.equals("amenidad")) {
            return getString(R.string.amenidadHint) + numeroAmenidad;
        }
        return ""; // Por si hay más tipos en el futuro
    }

    // Método para agregar el EditText al layout correspondiente
    private void agregarCampoAlLayout(EditText nuevoCampo, String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            LinearLayout reglasLayout = findViewById(R.id.reglasLayout);
            reglasLayout.addView(nuevoCampo);
        } else if (tipoCampo.equals("amenidad")) {
            LinearLayout amenidadesLayout = findViewById(R.id.amenidadesLayout);
            amenidadesLayout.addView(nuevoCampo);
        }
    }

    // Método para incrementar el contador de reglas o amenidades
    private void incrementarContador(String tipoCampo) {
        if (tipoCampo.equals("regla")) {
            numeroReglas++;
        } else if (tipoCampo.equals("amenidad")) {
            numeroAmenidad++;
        }
    }
}
