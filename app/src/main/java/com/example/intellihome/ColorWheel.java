package com.example.intellihome;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast; // Importar Toast

import androidx.appcompat.app.AppCompatActivity;

public class ColorWheel extends AppCompatActivity {
    ImageView imgView;
    TextView mColorValues;
    View mColorViews;
    Button btnSelectColor;

    Bitmap bitmap;

    private static final String PREFS_NAME = "ColorPrefs";
    private static final String COLOR_KEY = "selectedColor";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_colorpicker);
        imgView = findViewById(R.id.colorwheel);
        mColorValues = findViewById(R.id.displayValues);
        mColorViews = findViewById(R.id.displayColor);
        btnSelectColor = findViewById(R.id.btnSelectColor);

        loadColor();

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
                if (mColorViews.getBackground() instanceof ColorDrawable) {
                    int color = ((ColorDrawable) mColorViews.getBackground()).getColor();
                    saveColor(color);

                    GlobalColor globalColor = (GlobalColor) getApplication();
                    globalColor.setCurrentColor(color);

                    Intent intent = new Intent(ColorWheel.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveColor(int color) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(COLOR_KEY, color);
        editor.apply();

        Toast.makeText(this, "Color guardado: " + color, Toast.LENGTH_SHORT).show();
    }

    private void loadColor() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedColor = preferences.getInt(COLOR_KEY, Color.WHITE); // Color por defecto
        btnSelectColor.setBackgroundColor(savedColor); // Cargar el color guardado
    }
}
