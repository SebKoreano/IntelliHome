package com.example.intellihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    EditText txtLatitud, txtLongitud;
    private Button btnAceptar;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        btnAceptar = findViewById(R.id.btnAceptar);

        // Lógica para el botón del mapa
        btnAceptar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitud", Double.parseDouble(txtLatitud.getText().toString()));
            resultIntent.putExtra("longitud", Double.parseDouble(txtLongitud.getText().toString()));
            setResult(RESULT_OK, resultIntent);  // Envía el resultado a HomeActivity
            finish();  // Cierra MapActivity
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng costaRica = new LatLng(9.9355456,-84.1133451);
        mMap.addMarker(new MarkerOptions().position(costaRica).title("Costa Rica"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(costaRica));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));

        mMap.clear();
        LatLng costaRica = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(costaRica).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(costaRica));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));

        mMap.clear();
        LatLng costaRica = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(costaRica).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(costaRica));
    }
}
