package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.estructuras.colas.ColaPrioridad;
import co.edu.uniquindio.monedero.estructuras.hash.TablaHash;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.excepciones.CuentaNoEncontradaException;
import co.edu.uniquindio.monedero.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.TipoNotificacion;
import co.edu.uniquindio.monedero.modelo.transaccion.Frecuencia;
import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.TransaccionProgramada;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.time.LocalDate;

/**
 * Transacciones programadas con una cola de prioridad (min-heap por fecha de
 * ejecución). Para listar y cancelar por cliente se mantiene además un índice
 * en tablas hash; cancelar solo marca la programación como inactiva y la cola
 * la descarta cuando le toca salir ("borrado perezoso"), evitando buscar en el
 * heap, que costaría O(n).
 */
public class ProgramadorServicio {

    /** Resultado de intentar una ocurrencia programada. */
    public record Ejecucion(String idProgramada, LocalDate fechaPrevista, boolean exitosa, String mensaje) { }

    private final ClienteRepositorio repo;
    private final TransaccionServicio transacciones;
    private final NotificacionServicio notificaciones;
    private final ColaPrioridad<TransaccionProgramada> cola = new ColaPrioridad<>();
    private final TablaHash<String, TransaccionProgramada> porId = new TablaHash<>();
    private final TablaHash<String, ListaEnlazada<TransaccionProgramada>> porCliente = new TablaHash<>();
    private final TablaHash<String, LocalDate> recordadas = new TablaHash<>();

    public ProgramadorServicio(ClienteRepositorio repo, TransaccionServicio transacciones,
                               NotificacionServicio notificaciones) {
        this.repo = repo;
        this.transacciones = transacciones;
        this.notificaciones = notificaciones;
    }

    // ---------- Programar / cancelar ----------
    public TransaccionProgramada programarDeposito(String clienteId, String idMonedero, double monto,
                                                   Frecuencia frecuencia, LocalDate primeraEjecucion) {
        Cliente c = repo.exigir(clienteId);
        Monedero m = c.getCuenta().monederoPrincipal();
        TransaccionProgramada p = new TransaccionProgramada(TipoTransaccion.DEPOSITO, monto, null, null,
                c.getCuenta().getId(), m.getId(), frecuencia, primeraEjecucion);
        guardar(clienteId, p);
        return p;
    }

    public TransaccionProgramada programarTransferencia(String origenId, String idMonederoOrigen,
                                                        String destinoId, String idMonederoDestino,
                                                        double monto, Frecuencia frecuencia,
                                                        LocalDate primeraEjecucion) {
        Cliente origen = repo.exigir(origenId);
        Cliente destino = repo.exigir(destinoId);
        Monedero mo = origen.getCuenta().monederoPrincipal();
        Monedero md = destino.getCuenta().monederoPrincipal();
        TransaccionProgramada p = new TransaccionProgramada(TipoTransaccion.TRANSFERENCIA, monto,
                origen.getCuenta().getId(), mo.getId(), destino.getCuenta().getId(), md.getId(),
                frecuencia, primeraEjecucion);
        guardar(origenId, p);
        return p;
    }

    public void cancelar(String idProgramada) {
        TransaccionProgramada p = porId.obtener(idProgramada);
        if (p == null) throw new TransaccionInvalidaException("No existe la programación " + idProgramada);
        p.cancelar();
    }

    public ListaEnlazada<TransaccionProgramada> listarActivas(String clienteId) {
        ListaEnlazada<TransaccionProgramada> activas = new ListaEnlazada<>();
        ListaEnlazada<TransaccionProgramada> todas = porCliente.obtener(clienteId);
        if (todas != null) {
            for (TransaccionProgramada p : todas) {
                if (p.isActiva()) activas.agregarAlFinal(p);
            }
        }
        return activas;
    }

    // ---------- Proceso ----------
    /**
     * Ejecuta todo lo que venza hasta 'hoy' en orden de fecha. Si una
     * ocurrencia falla (p. ej. saldo insuficiente) se avisa al cliente, esa
     * ocurrencia se omite y la programación sigue con la siguiente fecha.
     */
    public ListaEnlazada<Ejecucion> procesarVencidas(LocalDate hoy) {
        ListaEnlazada<Ejecucion> resultados = new ListaEnlazada<>();
        while (!cola.estaVacia() && !cola.verMinimo().getProximaEjecucion().isAfter(hoy)) {
            TransaccionProgramada p = cola.extraerMinimo();
            if (!p.isActiva()) continue;                       // cancelada: se descarta
            LocalDate prevista = p.getProximaEjecucion();
            resultados.agregarAlFinal(ejecutar(p, prevista));
            if (p.avanzar()) cola.insertar(p);
        }
        return resultados;
    }

    /** Avisa de lo que se ejecuta mañana (una sola vez por ocurrencia). Devuelve cuántos avisos envió. */
    public int enviarRecordatorios(LocalDate hoy) {
        int enviados = 0;
        LocalDate manana = hoy.plusDays(1);
        for (String clienteId : porCliente.claves()) {
            Cliente c = repo.buscar(clienteId);
            if (c == null) continue;
            for (TransaccionProgramada p : porCliente.obtener(clienteId)) {
                if (!p.isActiva() || !p.getProximaEjecucion().equals(manana)) continue;
                if (manana.equals(recordadas.obtener(p.getId()))) continue;
                notificaciones.notificar(c, TipoNotificacion.TRANSACCION_PROGRAMADA,
                        "Mañana se ejecuta " + p.getTipo() + " de " + p.getMonto() + " (" + p.getId() + ")");
                recordadas.poner(p.getId(), manana);
                enviados++;
            }
        }
        return enviados;
    }

    public int pendientesEnCola() { return cola.tamanio(); }

    // ---------- Internos ----------
    private void guardar(String clienteId, TransaccionProgramada p) {
        cola.insertar(p);
        porId.poner(p.getId(), p);
        ListaEnlazada<TransaccionProgramada> lista = porCliente.obtener(clienteId);
        if (lista == null) {
            lista = new ListaEnlazada<>();
            porCliente.poner(clienteId, lista);
        }
        lista.agregarAlFinal(p);
    }

    private Ejecucion ejecutar(TransaccionProgramada p, LocalDate prevista) {
        Cliente dueno = null;
        try {
            String descripcion = "Programada " + p.getId();
            if (p.getTipo() == TipoTransaccion.DEPOSITO) {
                dueno = repo.exigirPorCuenta(p.getIdCuentaDestino());
                transacciones.depositar(dueno.getId(), p.getIdMonederoDestino(), p.getMonto(), descripcion);
            } else {
                dueno = repo.exigirPorCuenta(p.getIdCuentaOrigen());
                Cliente destino = repo.exigirPorCuenta(p.getIdCuentaDestino());
                transacciones.transferir(dueno.getId(), p.getIdMonederoOrigen(), destino.getId(),
                        p.getIdMonederoDestino(), p.getMonto(), CategoriaGasto.OTROS, descripcion);
            }
            return new Ejecucion(p.getId(), prevista, true, "Ejecutada");
        } catch (SaldoInsuficienteException | TransaccionInvalidaException | CuentaNoEncontradaException e) {
            if (dueno != null) {
                notificaciones.notificar(dueno, TipoNotificacion.TRANSACCION_PROGRAMADA,
                        "No se pudo ejecutar " + p.getId() + " del " + prevista + ": " + e.getMessage());
            }
            return new Ejecucion(p.getId(), prevista, false, e.getMessage());
        }
    }
}
