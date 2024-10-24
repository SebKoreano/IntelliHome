package com.example.intellihome;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import com.google.android.material.datepicker.MaterialDatePicker.Builder;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class AlquilarActivity extends AppCompatActivity {

    private TextView textoRangoFechas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alquilar);

        Button btnAbrirCalendario = findViewById(R.id.btnAbrirCalendario);
        textoRangoFechas = findViewById(R.id.textoRangoFechas);

        // Acción del botón para abrir el selector de rango de fechas
        btnAbrirCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarRangoFechas();
            }
        });
    }

    // Método para mostrar el selector de rango de fechas
    // Método para mostrar el rango de fechas seleccionado
    private void mostrarRangoFechas() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        builder.setTitleText(getString(R.string.seleccionar_rango_fechas)); // Usar string del archivo XML
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.show(getSupportFragmentManager(), "RangoDeFechas");

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Pair<Long, Long> rangoFechas = selection;
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaInicio = formatoFecha.format(new Date(rangoFechas.first));
            String fechaFin = formatoFecha.format(new Date(rangoFechas.second));

            textoRangoFechas.setText(String.format(getString(R.string.de_fecha), fechaInicio, fechaFin)); // Mostrar rango en TextView
        });
    }
}

