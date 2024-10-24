package com.example.intellihome;
import java.time.LocalDate;
import android.app.Application;

public class AlgoritmoDelBanquero extends Application {
    private int dia;
    private int mes;
    private int anio;
    private double comision;
    private double montoTotal;
    private final double porcentajeImpuesto = 13.0; // Impuesto fijo del 13%

    // Constructor que solo requiere la comisión y el monto total, el día, mes y año se obtienen automáticamente
    public AlgoritmoDelBanquero(double comision, double montoTotal) {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
        this.dia = fechaActual.getDayOfMonth();
        this.mes = fechaActual.getMonthValue();
        this.anio = fechaActual.getYear();

        this.comision = comision;
        this.montoTotal = montoTotal;
    }

    // Método para calcular la nueva cantidad ajustada
    public double calcularNuevaCantidad() {
        // Calcular el límite máximo (10% del monto total)
        double limiteMaximo = 0.10 * montoTotal;

        // Calcular la media armónica
        double mediaArmonica;
        if (porcentajeImpuesto + comision > 0) {
            mediaArmonica = 2 / ((1 / porcentajeImpuesto) + (1 / comision));
        } else {
            mediaArmonica = 0;
        }

        // Calcular el factor de ajuste basado en el día y el mes
        double factorAjuste = (dia + mes) / 100.0;

        // Ajustar la media armónica
        double mediaArmonicaAjustada = mediaArmonica * factorAjuste;

        // Asegurarse de que la media armónica ajustada no exceda el límite máximo
        if (mediaArmonicaAjustada > limiteMaximo) {
            mediaArmonicaAjustada = limiteMaximo;
        }

        return mediaArmonicaAjustada;
    }
}
