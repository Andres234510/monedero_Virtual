package co.edu.uniquindio.monedero.util;

import co.edu.uniquindio.monedero.estructuras.hash.TablaHash;

/** Genera ids legibles por prefijo: TX-000001, MON-000003, ... */
public final class GeneradorId {
    private static final TablaHash<String, Long> contadores = new TablaHash<>();

    private GeneradorId() { }

    public static synchronized String siguiente(String prefijo) {
        long n = contadores.obtenerOPorDefecto(prefijo, 0L) + 1;
        contadores.poner(prefijo, n);
        return prefijo + "-" + String.format("%06d", n);
    }

    /** Para restaurar el contador al cargar datos guardados. */
    public static synchronized void establecerContador(String prefijo, long valor) {
        contadores.poner(prefijo, valor);
    }

    public static synchronized void reiniciar() { contadores.limpiar(); }
}
