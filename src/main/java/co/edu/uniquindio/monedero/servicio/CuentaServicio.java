package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.time.LocalDate;

/** Consultas de saldo e historial. Las listas devueltas son copias: se pueden recorrer sin riesgo. */
public class CuentaServicio {
    private final ClienteRepositorio repo;

    public CuentaServicio(ClienteRepositorio repo) { this.repo = repo; }

    public double consultarSaldo(String clienteId) {
        return repo.exigir(clienteId).getCuenta().saldoTotal();
    }

    public double consultarSaldoMonedero(String clienteId, String idMonedero) {
        return repo.exigir(clienteId).getCuenta().monederoPrincipal().getSaldo();
    }

    public ListaEnlazada<Transaccion> historial(String clienteId) {
        ListaEnlazada<Transaccion> copia = new ListaEnlazada<>();
        for (Transaccion t : repo.exigir(clienteId).getCuenta().getHistorial()) copia.agregarAlFinal(t);
        return copia;
    }

    public ListaEnlazada<Transaccion> historialPorTipo(String clienteId, TipoTransaccion tipo) {
        ListaEnlazada<Transaccion> r = new ListaEnlazada<>();
        for (Transaccion t : repo.exigir(clienteId).getCuenta().getHistorial()) {
            if (t.getTipo() == tipo) r.agregarAlFinal(t);
        }
        return r;
    }

    /** Transacciones entre dos fechas, ambas incluidas. */
    public ListaEnlazada<Transaccion> historialEntre(String clienteId, LocalDate desde, LocalDate hasta) {
        ListaEnlazada<Transaccion> r = new ListaEnlazada<>();
        for (Transaccion t : repo.exigir(clienteId).getCuenta().getHistorial()) {
            LocalDate dia = t.getFecha().toLocalDate();
            if (!dia.isBefore(desde) && !dia.isAfter(hasta)) r.agregarAlFinal(t);
        }
        return r;
    }

    /** Las n más recientes, la más nueva primero. */
    public ListaEnlazada<Transaccion> ultimas(String clienteId, int n) {
        Cliente c = repo.exigir(clienteId);
        ListaEnlazada<Transaccion> r = new ListaEnlazada<>();
        for (Transaccion t : c.getCuenta().getHistorial()) r.agregarAlInicio(t);
        return co.edu.uniquindio.monedero.algoritmos.Ordenamiento.primeros(r, n);
    }
}
