package co.edu.uniquindio.monedero.transaccion;

import co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import co.edu.uniquindio.monedero.util.GeneradorId;

import java.time.LocalDate;

/**
 * Instrucción de una transacción recurrente (depósito automático o
 * transferencia periódica). Se ordena por fecha de la próxima ejecución, con
 * el id como desempate, para usarse en la ColaPrioridad.
 */
public class TransaccionProgramada implements Comparable<TransaccionProgramada> {
    private final String id;
    private final TipoTransaccion tipo;
    private final double monto;
    private final String idCuentaOrigen;     // null en depósitos
    private final String idMonederoOrigen;   // null en depósitos
    private final String idCuentaDestino;
    private final String idMonederoDestino;
    private final Frecuencia frecuencia;
    private final LocalDate fechaBase;
    private int ejecutadas;
    private LocalDate proximaEjecucion;
    private boolean activa = true;

    public TransaccionProgramada(TipoTransaccion tipo, double monto,
                                 String idCuentaOrigen, String idMonederoOrigen,
                                 String idCuentaDestino, String idMonederoDestino,
                                 Frecuencia frecuencia, LocalDate primeraEjecucion) {
        if (tipo == TipoTransaccion.RETIRO) {
            throw new TransaccionInvalidaException("Solo se programan depósitos y transferencias");
        }
        if (!(monto > 0) || Double.isInfinite(monto)) {
            throw new TransaccionInvalidaException("El monto debe ser un número positivo: " + monto);
        }
        if (tipo == TipoTransaccion.TRANSFERENCIA && (idCuentaOrigen == null || idMonederoOrigen == null)) {
            throw new TransaccionInvalidaException("La transferencia programada necesita origen");
        }
        this.id = GeneradorId.siguiente("PRG");
        this.tipo = tipo;
        this.monto = monto;
        this.idCuentaOrigen = idCuentaOrigen;
        this.idMonederoOrigen = idMonederoOrigen;
        this.idCuentaDestino = idCuentaDestino;
        this.idMonederoDestino = idMonederoDestino;
        this.frecuencia = frecuencia;
        this.fechaBase = primeraEjecucion;
        this.proximaEjecucion = primeraEjecucion;
    }

    public boolean estaVencida(LocalDate hoy) { return activa && !proximaEjecucion.isAfter(hoy); }

    /**
     * Pasa a la siguiente ejecución. Devuelve true si sigue activa (y debe
     * volver a la cola) o false si era UNICA y ya terminó.
     */
    public boolean avanzar() {
        if (frecuencia == Frecuencia.UNICA) {
            activa = false;
        } else {
            ejecutadas++;
            proximaEjecucion = frecuencia.fechaNumero(fechaBase, ejecutadas);
        }
        return activa;
    }

    public void cancelar() { activa = false; }

    @Override
    public int compareTo(TransaccionProgramada otra) {
        int cmp = proximaEjecucion.compareTo(otra.proximaEjecucion);
        return cmp != 0 ? cmp : id.compareTo(otra.id);
    }

    public String getId() { return id; }
    public TipoTransaccion getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getIdCuentaOrigen() { return idCuentaOrigen; }
    public String getIdMonederoOrigen() { return idMonederoOrigen; }
    public String getIdCuentaDestino() { return idCuentaDestino; }
    public String getIdMonederoDestino() { return idMonederoDestino; }
    public Frecuencia getFrecuencia() { return frecuencia; }
    public LocalDate getProximaEjecucion() { return proximaEjecucion; }
    public boolean isActiva() { return activa; }
}
