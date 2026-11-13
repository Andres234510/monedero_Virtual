package main.java.co.edu.uniquindio.monedero.util;

/**
 * Parámetros del sistema en un solo lugar. Los marcados con (*) NO vienen en
 * el enunciado: son decisiones de diseño y se pueden ajustar libremente.
 */
public final class Constantes {
    private Constantes() { }

    // --- Puntos por cada 100 unidades (enunciado) ---
    public static final int UNIDADES_POR_BLOQUE = 100;
    public static final int PUNTOS_DEPOSITO = 1;
    public static final int PUNTOS_RETIRO = 2;
    public static final int PUNTOS_TRANSFERENCIA = 3;

    // --- Umbrales mínimos de cada rango (enunciado) ---
    public static final long UMBRAL_BRONCE = 0;     // 0 - 500
    public static final long UMBRAL_PLATA = 501;    // 501 - 1000
    public static final long UMBRAL_ORO = 1001;     // 1001 - 5000
    public static final long UMBRAL_PLATINO = 5001; // más de 5000

    // --- Beneficios por rango, en % entero (*) ---
    public static final int BONO_PUNTOS_BRONCE = 0;
    public static final int BONO_PUNTOS_PLATA = 5;
    public static final int BONO_PUNTOS_ORO = 10;
    public static final int BONO_PUNTOS_PLATINO = 20;

    public static final int DESCUENTO_COMISION_BRONCE = 0;
    public static final int DESCUENTO_COMISION_PLATA = 10;
    public static final int DESCUENTO_COMISION_ORO = 20;
    public static final int DESCUENTO_COMISION_PLATINO = 30;

    // --- Canje de puntos (enunciado) ---
    public static final long COSTO_DESCUENTO_TRANSFERENCIA = 100;
    public static final int PORCENTAJE_DESCUENTO_TRANSFERENCIA = 10;
    public static final long COSTO_MES_SIN_CARGOS_RETIRO = 500;
    public static final int DIAS_SIN_CARGOS_RETIRO = 30;
    public static final long COSTO_BONO_SALDO = 1000;
    public static final double MONTO_BONO_SALDO = 50;

    // --- Comisiones base, en % (*) ---
    public static final double COMISION_TRANSFERENCIA_PORCENTAJE = 2.0;
    public static final double COMISION_RETIRO_PORCENTAJE = 1.0;

    // --- Alertas y notificaciones (*) ---
    public static final double UMBRAL_SALDO_BAJO = 50;
    public static final int MAX_NOTIFICACIONES = 50;

    public static final String NOMBRE_MONEDERO_PRINCIPAL = "Principal";
}
