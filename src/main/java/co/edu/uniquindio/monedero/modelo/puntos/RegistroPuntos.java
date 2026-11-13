package main.java.co.edu.uniquindio.monedero.modelo.puntos;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import main.java.co.edu.uniquindio.monedero.excepciones.PuntosInsuficientesException;

/**
 * Registro de puntos de un cliente: historial + dos totales.
 *
 *  - acumulados: puntos ganados menos reversiones. NO baja al canjear, y es lo
 *    que define el rango (canjear no te hace perder rango).
 *  - disponibles: lo que se puede gastar en beneficios (baja con cada canje).
 *
 * Ambos se pueden recalcular desde el historial con {@link #recalcular()},
 * que es lo que se hace tras revertir una transacción.
 */
public class RegistroPuntos {
    private final String clienteId;
    private final ListaEnlazada<MovimientoPuntos> historial = new ListaEnlazada<>();
    private long acumulados;
    private long disponibles;

    public RegistroPuntos(String clienteId) { this.clienteId = clienteId; }

    /** Acredita puntos de una transacción. Con 0 puntos no registra nada. */
    public void acreditar(String idTransaccion, long puntos, String descripcion) {
        if (puntos <= 0) return;
        historial.agregarAlFinal(
                new MovimientoPuntos(TipoMovimientoPuntos.GANADO, puntos, idTransaccion, descripcion));
        acumulados += puntos;
        disponibles += puntos;
    }

    /** Resta los puntos del beneficio de los disponibles (no toca los acumulados). */
    public void canjear(Beneficio beneficio) {
        if (disponibles < beneficio.getCostoPuntos()) {
            throw new PuntosInsuficientesException("Necesitas " + beneficio.getCostoPuntos()
                    + " puntos y tienes " + disponibles);
        }
        historial.agregarAlFinal(new MovimientoPuntos(TipoMovimientoPuntos.CANJEADO,
                -beneficio.getCostoPuntos(), null, beneficio.getDescripcion()));
        disponibles -= beneficio.getCostoPuntos();
    }

    /**
     * Revierte los puntos que aún quedan vigentes de una transacción (bono de
     * rango incluido) y recalcula los totales. Es idempotente: una segunda
     * llamada con el mismo id no hace nada.
     *
     * @return puntos descontados (0 si no había nada que revertir)
     */
    public long revertir(String idTransaccion) {
        long vigentes = 0;
        for (MovimientoPuntos m : historial) {
            if (idTransaccion.equals(m.getIdTransaccion())) vigentes += m.getPuntos();
        }
        if (vigentes <= 0) return 0;
        historial.agregarAlFinal(new MovimientoPuntos(TipoMovimientoPuntos.REVERTIDO, -vigentes,
                idTransaccion, "Reversión de " + idTransaccion));
        recalcular();
        return vigentes;
    }

    /** Recalcula acumulados y disponibles sumando todo el historial. */
    public void recalcular() {
        long acum = 0;
        long disp = 0;
        for (MovimientoPuntos m : historial) {
            disp += m.getPuntos();
            if (m.getTipo() != TipoMovimientoPuntos.CANJEADO) acum += m.getPuntos();
        }
        acumulados = acum;
        disponibles = disp;
    }

    public Rango getRango() { return Rango.desdePuntos(acumulados); }

    /** Puntos que faltan para el siguiente rango; 0 si ya es Platino. */
    public long puntosParaSiguienteRango() {
        Rango siguiente = getRango().siguiente();
        return siguiente == null ? 0 : Math.max(0, siguiente.getPuntosMinimos() - acumulados);
    }

    public String getClienteId() { return clienteId; }
    public long getAcumulados() { return acumulados; }
    public long getDisponibles() { return disponibles; }
    public ListaEnlazada<MovimientoPuntos> getHistorial() { return historial; }
}
