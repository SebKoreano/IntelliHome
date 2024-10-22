package com.example.intellihome;

import android.app.Application;

public class GlobalColor extends Application {
    private int currentColor = 0x57cdf5;

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }
}