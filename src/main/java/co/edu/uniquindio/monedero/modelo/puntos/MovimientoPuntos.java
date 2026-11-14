package co.edu.uniquindio.monedero.modelo.puntos;

import java.time.LocalDateTime;

/** Línea inmutable del historial de puntos. 'puntos' lleva signo (+ gana, - resta). */
public class MovimientoPuntos {
    private final TipoMovimientoPuntos tipo;
    private final long puntos;
    private final String idTransaccion; // null en canjes
    private final String descripcion;
    private final LocalDateTime fecha;

    public MovimientoPuntos(TipoMovimientoPuntos tipo, long puntos, String idTransaccion, String descripcion) {
        this.tipo = tipo;
        this.puntos = puntos;
        this.idTransaccion = idTransaccion;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public TipoMovimientoPuntos getTipo() { return tipo; }
    public long getPuntos() { return puntos; }
    public String getIdTransaccion() { return idTransaccion; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return tipo + " " + (puntos > 0 ? "+" : "") + puntos + " (" + descripcion + ")";
    }
}
