package com.example.intellihome;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ColorWheel extends AppCompatActivity {
    ImageView imgView;
    TextView mColorValues;
    View mColorViews;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_colorpicker);
        imgView = findViewById(R.id.colorwheel);
        mColorValues = findViewById(R.id.displayValues);
        mColorViews = findViewById(R.id.displayColor);

        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.colorwheel);

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
    }
}