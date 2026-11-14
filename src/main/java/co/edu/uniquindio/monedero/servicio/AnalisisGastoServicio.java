package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.algoritmos.Ordenamiento;
import co.edu.uniquindio.monedero.estructuras.grafos.Arista;
import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.transaccion.Retiro;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

/**
 * Análisis de patrones con grafos dirigidos. Los grafos se reconstruyen desde
 * los historiales en cada consulta, así siempre reflejan las reversiones.
 * Un "gasto" es un retiro o una transferencia a otro cliente, sin revertir.
 */
public class AnalisisGastoServicio {

    public record ResumenCategoria(CategoriaGasto categoria, double total, int cantidad) { }

    public record ClienteMonto(String clienteId, double monto) { }

    private final ClienteRepositorio repo;

    public AnalisisGastoServicio(ClienteRepositorio repo) { this.repo = repo; }

    /** Gastos del cliente en orden cronológico. */
    public ListaEnlazada<Transaccion> gastos(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        String idCuenta = c.getCuenta().getId();
        ListaEnlazada<Transaccion> gastos = new ListaEnlazada<>();
        for (Transaccion t : c.getCuenta().getHistorial()) {
            if (t.estaRevertida()) continue;
            if (t instanceof Retiro) {
                gastos.agregarAlFinal(t);
            } else if (t instanceof Transferencia tr && !tr.esInterna() && tr.getIdCuentaOrigen().equals(idCuenta)) {
                gastos.agregarAlFinal(t);
            }
        }
        return gastos;
    }

    /** Total y cantidad de gastos por categoría, de mayor a menor total. */
    public ListaEnlazada<ResumenCategoria> resumenPorCategoria(String clienteId) {
        CategoriaGasto[] categorias = CategoriaGasto.values();
        double[] total = new double[categorias.length];
        int[] cantidad = new int[categorias.length];
        for (Transaccion t : gastos(clienteId)) {
            total[t.getCategoria().ordinal()] += t.getMonto();
            cantidad[t.getCategoria().ordinal()]++;
        }
        ListaEnlazada<ResumenCategoria> resumen = new ListaEnlazada<>();
        for (CategoriaGasto cat : categorias) {
            if (cantidad[cat.ordinal()] > 0) {
                resumen.agregarAlFinal(new ResumenCategoria(cat, total[cat.ordinal()], cantidad[cat.ordinal()]));
            }
        }
        return Ordenamiento.ordenar(resumen, (a, b) -> Double.compare(b.total(), a.total()));
    }

    public double gastoTotal(String clienteId) {
        double total = 0;
        for (Transaccion t : gastos(clienteId)) total += t.getMonto();
        return total;
    }

    /**
     * Grafo de hábitos: arista A -> B con peso n = las veces que, después de
     * gastar en A, el siguiente gasto fue en B (A -> A = gastó dos veces seguidas en A).
     */
    public GrafoDirigido<CategoriaGasto> grafoCategorias(String clienteId) {
        GrafoDirigido<CategoriaGasto> grafo = new GrafoDirigido<>();
        CategoriaGasto anterior = null;
        for (Transaccion t : gastos(clienteId)) {
            CategoriaGasto actual = t.getCategoria();
            grafo.agregarVertice(actual);
            if (anterior != null) grafo.acumularArista(anterior, actual, 1);
            anterior = actual;
        }
        return grafo;
    }

    /** Categoría que más suele seguir a 'categoria' en el gasto del cliente; null si no hay datos. */
    public CategoriaGasto categoriaSiguienteMasFrecuente(String clienteId, CategoriaGasto categoria) {
        GrafoDirigido<CategoriaGasto> grafo = grafoCategorias(clienteId);
        if (!grafo.contieneVertice(categoria)) return null;
        CategoriaGasto mejor = null;
        double pesoMejor = 0;
        for (Arista<CategoriaGasto> a : grafo.aristasSalientes(categoria)) {
            if (a.getPeso() > pesoMejor) {
                pesoMejor = a.getPeso();
                mejor = a.getDestino();
            }
        }
        return mejor;
    }

    /** Grafo de transferencias entre clientes: arista A -> B = monto total que A le envió a B. */
    public GrafoDirigido<String> grafoTransferencias() {
        GrafoDirigido<String> grafo = new GrafoDirigido<>();
        for (Cliente c : repo.todos()) grafo.agregarVertice(c.getId());
        for (Cliente c : repo.todos()) {
            String idCuenta = c.getCuenta().getId();
            for (Transaccion t : c.getCuenta().getHistorial()) {
                // se cuenta solo desde el historial del emisor, para no duplicar
                if (t instanceof Transferencia tr && !tr.estaRevertida() && !tr.esInterna()
                        && tr.getIdCuentaOrigen().equals(idCuenta)) {
                    Cliente receptor = repo.exigirPorCuenta(tr.getIdCuentaDestino());
                    grafo.acumularArista(c.getId(), receptor.getId(), tr.getMonto());
                }
            }
        }
        return grafo;
    }

    public ListaEnlazada<ClienteMonto> topEmisores(int n) {
        GrafoDirigido<String> g = grafoTransferencias();
        ListaEnlazada<ClienteMonto> lista = new ListaEnlazada<>();
        for (String id : g.vertices()) {
            double monto = g.pesoTotalSalida(id);
            if (monto > 0) lista.agregarAlFinal(new ClienteMonto(id, monto));
        }
        return Ordenamiento.primeros(Ordenamiento.ordenar(lista, (a, b) -> Double.compare(b.monto(), a.monto())), n);
    }

    public ListaEnlazada<ClienteMonto> topReceptores(int n) {
        GrafoDirigido<String> g = grafoTransferencias();
        ListaEnlazada<ClienteMonto> lista = new ListaEnlazada<>();
        for (String id : g.vertices()) {
            double monto = g.pesoTotalEntrada(id);
            if (monto > 0) lista.agregarAlFinal(new ClienteMonto(id, monto));
        }
        return Ordenamiento.primeros(Ordenamiento.ordenar(lista, (a, b) -> Double.compare(b.monto(), a.monto())), n);
    }
}
