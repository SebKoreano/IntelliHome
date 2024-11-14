package com.example.intellihome;
import java.time.LocalDate;
import android.app.Application;

public class AlgoritmoDelBanquero extends Application {
    private int dia;
    private int mes;
    private int anio;
    private double comision;
    private double precioNoche; //Precio ingresado por el dueno de la casa
    private double precioTotal;
    private final double porcentLimpieza  = 0.10;
    private final double porcentajeImpuesto = 13.0; // Impuesto fijo del 13%

    // Constructor que solo requiere la comisión y el monto total, el día, mes y año se obtienen automáticamente
    public AlgoritmoDelBanquero(double comision, double precioNoche) {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
        this.dia = fechaActual.getDayOfMonth();
        this.mes = fechaActual.getMonthValue();
        this.anio = fechaActual.getYear();

        this.comision = comision;
        this.precioNoche = precioNoche;
    }

    // Método para calcular la nueva cantidad ajustada
    public double calcularNuevaCantidad() {
        double limiteMaximo = calcularLimiteMaximo();
        double mediaArmonicaAjustada = calcularMediaArmonicaAjustada(limiteMaximo);
        double costoLimpieza = calcularCostoLimpieza();
        double costoImpuesto = calcularCostoImpuesto();

        precioTotal = mediaArmonicaAjustada + costoLimpieza + costoImpuesto;
        return precioTotal;
    }

    // Calcula el límite máximo basado en el precio por noche (10%)
    private double calcularLimiteMaximo() {
        return 0.10 * precioNoche;
    }

    // Calcula la media armónica ajustada y la limita según el límite máximo
    private double calcularMediaArmonicaAjustada(double limiteMaximo) {
        double mediaArmonica = calcularMediaArmonica();
        double factorAjuste = calcularFactorAjuste();
        double mediaArmonicaAjustada = mediaArmonica * factorAjuste;

        if (mediaArmonicaAjustada > limiteMaximo) {
            mediaArmonicaAjustada = limiteMaximo;
        }
        return mediaArmonicaAjustada;
    }

    // Calcula la media armónica basada en porcentaje de impuesto y comisión
    private double calcularMediaArmonica() {
        if (porcentajeImpuesto + comision > 0) {
            return 2 / ((1 / porcentajeImpuesto) + (1 / comision));
        }
        return 0;
    }

    // Calcula el factor de ajuste según el día y el mes
    private double calcularFactorAjuste() {
        return (dia + mes) / 100.0;
    }

    // Calcula el costo de limpieza basado en el precio por noche y el porcentaje de limpieza
    private double calcularCostoLimpieza() {
        return precioNoche * porcentLimpieza;
    }

    // Calcula el costo del impuesto basado en el precio por noche y el porcentaje de impuesto
    private double calcularCostoImpuesto() {
        return precioNoche * (porcentajeImpuesto / 100);
    }

}
