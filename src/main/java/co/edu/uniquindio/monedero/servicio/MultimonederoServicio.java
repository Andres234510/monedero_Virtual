package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Cuenta;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

public class MultimonederoServicio {
    private final ClienteRepositorio repo;
    private final TransaccionServicio transacciones;

    public MultimonederoServicio(ClienteRepositorio repo, TransaccionServicio transacciones) {
        this.repo = repo;
        this.transacciones = transacciones;
    }

    /** Crea un monedero; los nombres no pueden repetirse dentro de una cuenta (sin distinguir mayúsculas). */
    public Monedero crearMonedero(String clienteId, String nombre) {
        Cuenta cuenta = repo.exigir(clienteId).getCuenta();
        if (nombre == null || nombre.isBlank()) throw new TransaccionInvalidaException("El monedero necesita un nombre");
        for (Monedero m : cuenta.getMonederos()) {
            if (m.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new TransaccionInvalidaException("Ya tienes un monedero llamado '" + m.getNombre() + "'");
            }
        }
        return cuenta.crearMonedero(nombre);
    }

    public ListaEnlazada<Monedero> monederos(String clienteId) {
        return repo.exigir(clienteId).getCuenta().getMonederos();
    }

    /** Mueve dinero entre dos monederos del mismo cliente (sin comisión ni puntos). */
    public Transferencia transferirEntreMonederos(String clienteId, String idOrigen, String idDestino,
                                                  double monto, String descripcion) {
        return transacciones.transferir(clienteId, idOrigen, clienteId, idDestino, monto,
                CategoriaGasto.OTROS, descripcion);
    }

    /** Grafo dirigido: vértice = monedero (por nombre), arista = total movido de uno a otro (sin revertidas). */
    public GrafoDirigido<String> grafoMonederos(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        GrafoDirigido<String> grafo = new GrafoDirigido<>();
        for (Monedero m : cuenta.getMonederos()) grafo.agregarVertice(m.getNombre());
        for (Transaccion t : cuenta.getHistorial()) {
            if (t instanceof Transferencia tr && tr.esInterna() && !tr.estaRevertida()) {
                Monedero origen = cuenta.buscarMonedero(tr.getIdMonederoOrigen());
                Monedero destino = cuenta.buscarMonedero(tr.getIdMonederoDestino());
                grafo.acumularArista(origen.getNombre(), destino.getNombre(), tr.getMonto());
            }
        }
        return grafo;
    }

    /** ¿Ha habido flujo de dinero (directo o con pasos intermedios) del primer monedero al segundo? */
    public boolean hayFlujo(String clienteId, String nombreOrigen, String nombreDestino) {
        return grafoMonederos(clienteId).hayCamino(nombreOrigen, nombreDestino);
    }
}
