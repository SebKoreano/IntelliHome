package com.example.intellihome;

import android.app.Application;

public class GlobalColor extends Application {
    private int currentColor = 0xFF57CDF5;
    private String userName;
    private String tipoUsuario;
    private String ip = "192.168.18.5";

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public String getCurrentuserName(){ return userName;}

    public void setCurrentuserName(String newUser){this.userName = newUser;}

    public String getCurrenttipoUsuario(){ return tipoUsuario;}

    public void setCurrenttipoUsuario(String newUserType){this.tipoUsuario = newUserType;}

    public String getIp() {
        return ip;
    }
}