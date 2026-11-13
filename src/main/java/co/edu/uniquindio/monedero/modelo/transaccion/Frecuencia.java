package main.java.co.edu.uniquindio.monedero.modelo.transaccion;

import java.time.LocalDate;

public enum Frecuencia {
    UNICA, DIARIA, SEMANAL, MENSUAL;

    /**
     * Fecha de la ejecución número n (0 = la primera) contada desde la base.
     * Se calcula siempre desde la fecha base para que una programación del día
     * 31 no se quede en el 28 después de febrero.
     */
    public LocalDate fechaNumero(LocalDate base, int n) {
        return switch (this) {
            case UNICA -> base;
            case DIARIA -> base.plusDays(n);
            case SEMANAL -> base.plusWeeks(n);
            case MENSUAL -> base.plusMonths(n);
        };
    }
}
