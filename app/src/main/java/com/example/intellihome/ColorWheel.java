package com.example.intellihome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ColorWheel extends AppCompatActivity {
    ImageView imgView;
    TextView mColorValues;
    View mColorViews;
    Button btnSelectColor; // Agregamos el botón

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_colorpicker);
        imgView = findViewById(R.id.colorwheel);
        mColorValues = findViewById(R.id.displayValues);
        mColorViews = findViewById(R.id.displayColor);
        btnSelectColor = findViewById(R.id.btnSelectColor); // Inicializamos el botón

        GlobalColor globalVariables = (GlobalColor) getApplicationContext();
        int currentColor = globalVariables.getCurrentColor();
        btnSelectColor.setBackgroundColor(currentColor);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.colorwheel);

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getX() >= 0 && event.getX() < bitmap.getWidth() &&
                            event.getY() >= 0 && event.getY() < bitmap.getHeight()) {
                        int pixels = bitmap.getPixel((int) event.getX(), (int) event.getY());

                        int r = Color.red(pixels);
                        int g = Color.green(pixels);
                        int b = Color.blue(pixels);

                        String hex = String.format("#%06X", (0xFFFFFF & pixels));
                        mColorViews.setBackgroundColor(Color.rgb(r, g, b));
                        mColorValues.setText("RGB: " + r + ", " + g + ", " + b + " \nHEX: " + hex);
                    }
                }
                return true;
            }
        });

        btnSelectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el color de mColorViews
                if (mColorViews.getBackground() instanceof ColorDrawable) {
                    int color = ((ColorDrawable) mColorViews.getBackground()).getColor();

                    // Cambiar el color global
                    GlobalColor globalColor = (GlobalColor) getApplication();
                    globalColor.setCurrentColor(color);

                    // Regresar a MainActivity
                    Intent intent = new Intent(ColorWheel.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
