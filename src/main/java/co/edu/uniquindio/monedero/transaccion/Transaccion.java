package co.edu.uniquindio.monedero.transaccion;

import co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.util.GeneradorId;

import java.time.LocalDateTime;

public abstract class Transaccion {
    private final String id;
    private final TipoTransaccion tipo;
    private final double monto;
    private final LocalDateTime fecha;
    private final CategoriaGasto categoria;
    private final String descripcion;
    private EstadoTransaccion estado = EstadoTransaccion.EJECUTADA;
    private double comision;
    private long puntosGenerados;

    protected Transaccion(TipoTransaccion tipo, double monto, CategoriaGasto categoria, String descripcion) {
        if (!(monto > 0) || Double.isInfinite(monto)) {
            throw new TransaccionInvalidaException("El monto debe ser un número positivo: " + monto);
        }
        this.id = GeneradorId.siguiente("TX");
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.categoria = categoria == null ? CategoriaGasto.OTROS : categoria;
        this.descripcion = descripcion == null ? "" : descripcion;
    }

    public void marcarRevertida() { this.estado = EstadoTransaccion.REVERTIDA; }

    public String getId() { return id; }
    public TipoTransaccion getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public CategoriaGasto getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public EstadoTransaccion getEstado() { return estado; }
    public boolean estaRevertida() { return estado == EstadoTransaccion.REVERTIDA; }
    public double getComision() { return comision; }
    public void setComision(double comision) { this.comision = comision; }
    public long getPuntosGenerados() { return puntosGenerados; }
    public void setPuntosGenerados(long puntosGenerados) { this.puntosGenerados = puntosGenerados; }

    @Override
    public String toString() {
        return id + " " + tipo + " " + monto + " (" + estado + ")";
    }
}
