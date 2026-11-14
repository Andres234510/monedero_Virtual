package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.algoritmos.ClasificadorRangos;
import co.edu.uniquindio.monedero.algoritmos.Ordenamiento;
import co.edu.uniquindio.monedero.estructuras.arboles.ArbolAVL;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.TipoNotificacion;
import co.edu.uniquindio.monedero.modelo.puntos.ClavePuntos;
import co.edu.uniquindio.monedero.modelo.puntos.Rango;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

/**
 * Mantiene un AVL de clientes ordenados por puntos acumulados. Cada cambio de
 * puntos cuesta O(log n) y permite sacar el ranking y los clientes de un rango
 * sin recorrer a todos.
 */
public class RangoServicio {
    private final ArbolAVL<ClavePuntos, String> arbol = new ArbolAVL<>();
    private final ClasificadorRangos clasificador = new ClasificadorRangos();
    private final ClienteRepositorio repo;
    private final NotificacionServicio notificaciones;

    public RangoServicio(ClienteRepositorio repo, NotificacionServicio notificaciones) {
        this.repo = repo;
        this.notificaciones = notificaciones;
    }

    public void registrar(Cliente c) {
        arbol.insertar(new ClavePuntos(c.getRegistroPuntos().getAcumulados(), c.getId()), c.getId());
    }

    /** Mueve al cliente en el árbol y avisa si cambió de rango. */
    public void actualizar(Cliente c, long puntosAntes, long puntosDespues) {
        if (puntosAntes == puntosDespues) return;
        arbol.eliminar(new ClavePuntos(puntosAntes, c.getId()));
        arbol.insertar(new ClavePuntos(puntosDespues, c.getId()), c.getId());
        Rango antes = clasificador.clasificar(puntosAntes);
        Rango despues = clasificador.clasificar(puntosDespues);
        if (antes != despues) {
            String verbo = despues.ordinal() > antes.ordinal() ? "Subiste" : "Bajaste";
            notificaciones.notificar(c, TipoNotificacion.CAMBIO_RANGO,
                    verbo + " de " + antes + " a " + despues + " (" + puntosDespues + " puntos)");
        }
    }

    public Rango rangoDe(long puntos) { return clasificador.clasificar(puntos); }

    /** Los n clientes con más puntos, de mayor a menor. */
    public ListaEnlazada<Cliente> ranking(int n) {
        ListaEnlazada<Cliente> descendente = new ListaEnlazada<>();
        for (String id : arbol.valoresEnOrden()) descendente.agregarAlInicio(repo.exigir(id));
        return Ordenamiento.primeros(descendente, n);
    }

    /** Clientes cuyo rango es r, de menor a mayor puntaje. */
    public ListaEnlazada<Cliente> clientesDeRango(Rango r) {
        long min = r == Rango.BRONCE ? Long.MIN_VALUE : r.getPuntosMinimos();
        Rango siguiente = r.siguiente();
        long max = siguiente == null ? Long.MAX_VALUE : siguiente.getPuntosMinimos() - 1;
        ListaEnlazada<Cliente> resultado = new ListaEnlazada<>();
        for (String id : arbol.valoresEnRango(new ClavePuntos(min, ""), new ClavePuntos(max, "\uffff"))) {
            resultado.agregarAlFinal(repo.exigir(id));
        }
        return resultado;
    }

    public int totalClientes() { return arbol.tamanio(); }
}
