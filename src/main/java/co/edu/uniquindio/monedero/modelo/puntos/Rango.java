package co.edu.uniquindio.monedero.modelo.puntos;

import co.edu.uniquindio.monedero.util.Constantes;

/** Rangos de cliente según puntos acumulados. */
public enum Rango {
    BRONCE("Bronce", Constantes.UMBRAL_BRONCE, Constantes.BONO_PUNTOS_BRONCE, Constantes.DESCUENTO_COMISION_BRONCE),
    PLATA("Plata", Constantes.UMBRAL_PLATA, Constantes.BONO_PUNTOS_PLATA, Constantes.DESCUENTO_COMISION_PLATA),
    ORO("Oro", Constantes.UMBRAL_ORO, Constantes.BONO_PUNTOS_ORO, Constantes.DESCUENTO_COMISION_ORO),
    PLATINO("Platino", Constantes.UMBRAL_PLATINO, Constantes.BONO_PUNTOS_PLATINO, Constantes.DESCUENTO_COMISION_PLATINO);

    private final String etiqueta;
    private final long puntosMinimos;
    private final int bonoPuntosPorcentaje;
    private final int descuentoComisionPorcentaje;

    Rango(String etiqueta, long puntosMinimos, int bonoPuntosPorcentaje, int descuentoComisionPorcentaje) {
        this.etiqueta = etiqueta;
        this.puntosMinimos = puntosMinimos;
        this.bonoPuntosPorcentaje = bonoPuntosPorcentaje;
        this.descuentoComisionPorcentaje = descuentoComisionPorcentaje;
    }

    /**
     * Función de clasificación del enunciado:
     * Bronce 0-500, Plata 501-1000, Oro 1001-5000, Platino más de 5000.
     * Puntos negativos (posibles tras una reversión) cuentan como Bronce.
     */
    public static Rango desdePuntos(long puntos) {
        Rango[] todos = values();
        for (int i = todos.length - 1; i > 0; i--) {
            if (puntos >= todos[i].puntosMinimos) return todos[i];
        }
        return BRONCE;
    }

    /** Siguiente rango o null si ya es el máximo. */
    public Rango siguiente() {
        Rango[] todos = values();
        return ordinal() + 1 < todos.length ? todos[ordinal() + 1] : null;
    }

    public String getEtiqueta() { return etiqueta; }
    public long getPuntosMinimos() { return puntosMinimos; }
    /** % extra de puntos que recibe en cada transacción. */
    public int getBonoPuntosPorcentaje() { return bonoPuntosPorcentaje; }
    /** % de descuento sobre la comisión de transferencias. */
    public int getDescuentoComisionPorcentaje() { return descuentoComisionPorcentaje; }

    @Override
    public String toString() { return etiqueta; }
}
