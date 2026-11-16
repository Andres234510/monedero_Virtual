package co.edu.uniquindio.monedero.modelo;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.excepciones.CuentaNoEncontradaException;
import co.edu.uniquindio.monedero.estructuras.pilas.Pila;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.util.Constantes;
import co.edu.uniquindio.monedero.util.GeneradorId;

/**
 * Cuenta de un cliente: varios monederos, historial de transacciones
 * (ListaEnlazada) y pila de transacciones reversibles (Pila).
 */
public class Cuenta {
    private final String id;
    private final String clienteId;
    private final ListaEnlazada<Monedero> monederos = new ListaEnlazada<>();
    private final ListaEnlazada<Transaccion> historial = new ListaEnlazada<>();
    private final Pila<Transaccion> reversibles = new Pila<>();

    public Cuenta(String clienteId) {
        this.id = GeneradorId.siguiente("CTA");
        this.clienteId = clienteId;
        monederos.agregarAlFinal(new Monedero(Constantes.NOMBRE_MONEDERO_PRINCIPAL));
    }

    public Monedero crearMonedero(String nombre) {
        Monedero m = new Monedero(nombre);
        monederos.agregarAlFinal(m);
        return m;
    }

    public Monedero buscarMonedero(String idMonedero) {
        for (Monedero m : monederos) {
            if (m.getId().equals(idMonedero)) return m;
        }
        return null;
    }

    public Monedero monederoPrincipal() { return monederos.primero(); }

    public double saldoTotal() {
        double total = 0;
        for (Monedero m : monederos) total += m.getSaldo();
        return total;
    }

    /** Guarda la transacción en el historial y como candidata a reversión. */
    public void registrarTransaccion(Transaccion t) {
        historial.agregarAlFinal(t);
        reversibles.apilar(t);
    }

    /** Solo al historial: transferencias recibidas y bonos, que no se revierten desde aquí. */
    public void registrarSoloHistorial(Transaccion t) {
        historial.agregarAlFinal(t);
    }

    /** El monedero con ese id, o el principal si no se indica ninguno. */
    public Monedero monederoOPrincipal(String idMonedero) {
        if (idMonedero == null || idMonedero.isBlank()) return monederoPrincipal();
        Monedero m = buscarMonedero(idMonedero);
        if (m == null) throw new CuentaNoEncontradaException("No existe el monedero " + idMonedero);
        return m;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public ListaEnlazada<Monedero> getMonederos() { return monederos; }
    public ListaEnlazada<Transaccion> getHistorial() { return historial; }
    public Pila<Transaccion> getReversibles() { return reversibles; }
}