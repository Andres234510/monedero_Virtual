package co.edu.uniquindio.monedero.algoritmos;

import co.edu.uniquindio.monedero.modelo.puntos.Rango;
import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.util.Constantes;

/**
 * Cálculo de puntos, todo en aritmética entera (sin errores de redondeo) y
 * O(1) por transacción.
 *
 *   base  = (monto / 100, redondeando hacia abajo) x tasa del tipo
 *   bono  = base x %bono del rango / 100 (hacia abajo)
 *   total = base + bono
 */
public final class CalculadoraPuntos {
    private CalculadoraPuntos() { }

    /** Desglose del resultado para mostrarlo en la interfaz y en el informe. */
    public record Resultado(long base, long bono, long total) { }

    public static int tasa(TipoTransaccion tipo) {
        return switch (tipo) {
            case DEPOSITO -> Constantes.PUNTOS_DEPOSITO;
            case RETIRO -> Constantes.PUNTOS_RETIRO;
            case TRANSFERENCIA -> Constantes.PUNTOS_TRANSFERENCIA;
        };
    }

    /** Puntos sin bono: solo bloques completos de 100 unidades. */
    public static long puntosBase(TipoTransaccion tipo, double monto) {
        if (!(monto > 0) || Double.isInfinite(monto)) return 0;
        long bloques = (long) Math.floor(monto / Constantes.UNIDADES_POR_BLOQUE);
        return bloques * tasa(tipo);
    }

    public static long bonoPorRango(long puntosBase, Rango rango) {
        return puntosBase * rango.getBonoPuntosPorcentaje() / 100;
    }

    public static Resultado calcular(TipoTransaccion tipo, double monto, Rango rango) {
        long base = puntosBase(tipo, monto);
        long bono = bonoPorRango(base, rango);
        return new Resultado(base, bono, base + bono);
    }
}
