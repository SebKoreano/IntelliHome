package com.example.intellihome;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private Context context;
    private String label;

    // Constructor para pasar el contexto y la etiqueta del Spinner
    public CustomItemSelectedListener(Context context, String label) {
        this.context = context;
        this.label = label;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Obtener el elemento seleccionado
        String selectedItem = parent.getItemAtPosition(position).toString();

        // Mostrar un Toast con el elemento seleccionado
        Toast.makeText(context, label + " seleccionado: " + selectedItem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Implementar si se desea alguna acción cuando no se selecciona nada
    }
}
