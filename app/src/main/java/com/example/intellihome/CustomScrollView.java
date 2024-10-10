package com.example.intellihome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Evitar que el ScrollView intercepte los eventos táctiles mientras se interactúa con DatePicker o expDatePicker
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Si el evento táctil está sobre uno de los DatePicker, no interceptamos el evento.
            if (isTouchOnDatePicker(ev)) {
                return false; // No interceptar, dejar que el DatePicker maneje el evento.
            }
        }
        return super.onInterceptTouchEvent(ev); // Dejar que el ScrollView maneje el resto de los eventos.
    }

    private boolean isTouchOnDatePicker(MotionEvent ev) {
        // Obtener las ubicaciones de ambos DatePicker
        int[] locationMain = new int[2];
        int[] locationExp = new int[2];

        DatePicker datePicker = findViewById(R.id.datePicker);   // Primer DatePicker
        DatePicker expDatePicker = findViewById(R.id.expDatePicker); // Segundo DatePicker (Inquilino)

        if (datePicker != null) {
            datePicker.getLocationOnScreen(locationMain);
        }
        if (expDatePicker != null) {
            expDatePicker.getLocationOnScreen(locationExp);
        }

        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();

        // Comprobar si el toque está dentro de los límites de alguno de los DatePicker
        boolean touchOnMainDatePicker = (x >= locationMain[0] && x <= locationMain[0] + datePicker.getWidth() &&
                y >= locationMain[1] && y <= locationMain[1] + datePicker.getHeight());

        boolean touchOnExpDatePicker = (x >= locationExp[0] && x <= locationExp[0] + expDatePicker.getWidth() &&
                y >= locationExp[1] && y <= locationExp[1] + expDatePicker.getHeight());

        return touchOnMainDatePicker || touchOnExpDatePicker;
    }
}

