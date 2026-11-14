package co.edu.uniquindio.monedero.repositorio;

import co.edu.uniquindio.monedero.estructuras.hash.TablaHash;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.excepciones.CuentaNoEncontradaException;
import co.edu.uniquindio.monedero.modelo.Cliente;

/** Almacén en memoria de clientes. Dos tablas hash: por id y por id de cuenta (O(1) promedio). */
public class ClienteRepositorio {
    private final TablaHash<String, Cliente> porId = new TablaHash<>();
    private final TablaHash<String, Cliente> porCuenta = new TablaHash<>();

    public void agregar(Cliente cliente) {
        if (porId.contiene(cliente.getId())) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento " + cliente.getId());
        }
        porId.poner(cliente.getId(), cliente);
        porCuenta.poner(cliente.getCuenta().getId(), cliente);
    }

    /** null si no existe. */
    public Cliente buscar(String id) { return porId.obtener(id); }

    public Cliente exigir(String id) {
        Cliente c = porId.obtener(id);
        if (c == null) throw new CuentaNoEncontradaException("No existe el cliente " + id);
        return c;
    }

    public Cliente buscarPorCuenta(String idCuenta) { return porCuenta.obtener(idCuenta); }

    public Cliente exigirPorCuenta(String idCuenta) {
        Cliente c = porCuenta.obtener(idCuenta);
        if (c == null) throw new CuentaNoEncontradaException("No existe la cuenta " + idCuenta);
        return c;
    }

    public boolean existe(String id) { return porId.contiene(id); }
    public ListaEnlazada<Cliente> todos() { return porId.valores(); }
    public int tamanio() { return porId.tamanio(); }
}
