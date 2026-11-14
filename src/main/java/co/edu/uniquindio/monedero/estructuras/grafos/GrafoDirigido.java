package co.edu.uniquindio.monedero.estructuras.grafos;

import co.edu.uniquindio.monedero.estructuras.colas.Cola;
import co.edu.uniquindio.monedero.estructuras.hash.TablaHash;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.estructuras.pilas.Pila;

/**
 * Grafo dirigido y ponderado con listas de adyacencia. Los vértices se
 * localizan en O(1) mediante una {@link TablaHash}.
 *
 * Usos en el proyecto:
 *  - Transferencias entre clientes (vértice = cliente, peso = monto acumulado).
 *  - Relación categorías de gasto (vértice = categoría).
 *  - Relación entre monederos de una misma cuenta.
 *
 * El tipo T debe implementar equals() y hashCode() (String, Integer, enums...).
 */
public class GrafoDirigido<T> {
    private final TablaHash<T, Vertice<T>> vertices = new TablaHash<>();
    private final ListaEnlazada<T> orden = new ListaEnlazada<>(); // orden de inserción
    private int numAristas;

    // ---------- Vértices ----------
    /** @return false si el vértice ya existía */
    public boolean agregarVertice(T dato) {
        if (dato == null) throw new IllegalArgumentException("Vértice nulo");
        if (vertices.contiene(dato)) return false;
        vertices.poner(dato, new Vertice<>(dato));
        orden.agregarAlFinal(dato);
        return true;
    }

    public boolean contieneVertice(T dato) { return vertices.contiene(dato); }

    /** Elimina el vértice y todas las aristas que entran o salen de él. */
    public boolean eliminarVertice(T dato) {
        Vertice<T> v = vertices.obtener(dato);
        if (v == null) return false;
        numAristas -= v.getAristas().tamanio();
        for (T otro : orden) {
            if (!otro.equals(dato) && vertices.obtener(otro).quitarArista(dato)) numAristas--;
        }
        vertices.eliminar(dato);
        orden.eliminar(dato);
        return true;
    }

    public ListaEnlazada<T> vertices() {
        ListaEnlazada<T> copia = new ListaEnlazada<>();
        for (T v : orden) copia.agregarAlFinal(v);
        return copia;
    }

    // ---------- Aristas ----------
    /** Crea la arista o reemplaza su peso. Ambos vértices deben existir. */
    public void agregarArista(T origen, T destino, double peso) {
        Vertice<T> vo = exigir(origen);
        exigir(destino);
        Arista<T> existente = vo.buscarArista(destino);
        if (existente != null) {
            existente.setPeso(peso);
        } else {
            vo.getAristas().agregarAlFinal(new Arista<>(destino, peso));
            numAristas++;
        }
    }

    /**
     * Suma 'delta' al peso de la arista; crea vértices y arista si no existen.
     * Pensado para acumular montos de transferencias o gasto por categoría.
     */
    public void acumularArista(T origen, T destino, double delta) {
        agregarVertice(origen);
        agregarVertice(destino);
        Vertice<T> vo = vertices.obtener(origen);
        Arista<T> existente = vo.buscarArista(destino);
        if (existente != null) {
            existente.setPeso(existente.getPeso() + delta);
        } else {
            vo.getAristas().agregarAlFinal(new Arista<>(destino, delta));
            numAristas++;
        }
    }

    public boolean eliminarArista(T origen, T destino) {
        Vertice<T> vo = vertices.obtener(origen);
        if (vo == null || !vo.quitarArista(destino)) return false;
        numAristas--;
        return true;
    }

    public boolean existeArista(T origen, T destino) {
        Vertice<T> vo = vertices.obtener(origen);
        return vo != null && vo.buscarArista(destino) != null;
    }

    /** Peso de la arista, o 0.0 si no existe. */
    public double pesoArista(T origen, T destino) {
        Vertice<T> vo = vertices.obtener(origen);
        if (vo == null) return 0.0;
        Arista<T> a = vo.buscarArista(destino);
        return a == null ? 0.0 : a.getPeso();
    }

    public ListaEnlazada<Arista<T>> aristasSalientes(T origen) {
        return exigir(origen).getAristas();
    }

    // ---------- Métricas ----------
    public int gradoSalida(T v) { return exigir(v).getAristas().tamanio(); }

    public int gradoEntrada(T v) {
        exigir(v);
        int grado = 0;
        for (T otro : orden) {
            if (vertices.obtener(otro).buscarArista(v) != null) grado++;
        }
        return grado;
    }

    /** Suma de pesos de las aristas que salen del vértice (ej. total transferido). */
    public double pesoTotalSalida(T v) {
        double total = 0;
        for (Arista<T> a : exigir(v).getAristas()) total += a.getPeso();
        return total;
    }

    /** Suma de pesos de las aristas que llegan al vértice (ej. total recibido). */
    public double pesoTotalEntrada(T v) {
        exigir(v);
        double total = 0;
        for (T otro : orden) {
            Arista<T> a = vertices.obtener(otro).buscarArista(v);
            if (a != null) total += a.getPeso();
        }
        return total;
    }

    public int numVertices() { return orden.tamanio(); }
    public int numAristas() { return numAristas; }
    public boolean estaVacio() { return orden.estaVacia(); }

    // ---------- Recorridos ----------
    /** BFS: vértices alcanzables desde 'inicio', por niveles. */
    public ListaEnlazada<T> recorridoAnchura(T inicio) {
        exigir(inicio);
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        TablaHash<T, Boolean> visitados = new TablaHash<>();
        Cola<T> cola = new Cola<>();
        visitados.poner(inicio, true);
        cola.encolar(inicio);
        while (!cola.estaVacia()) {
            T actual = cola.desencolar();
            resultado.agregarAlFinal(actual);
            for (Arista<T> a : vertices.obtener(actual).getAristas()) {
                if (!visitados.contiene(a.getDestino())) {
                    visitados.poner(a.getDestino(), true);
                    cola.encolar(a.getDestino());
                }
            }
        }
        return resultado;
    }

    /** DFS iterativo con pila: vértices alcanzables desde 'inicio'. */
    public ListaEnlazada<T> recorridoProfundidad(T inicio) {
        exigir(inicio);
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        TablaHash<T, Boolean> visitados = new TablaHash<>();
        Pila<T> pila = new Pila<>();
        pila.apilar(inicio);
        while (!pila.estaVacia()) {
            T actual = pila.desapilar();
            if (visitados.contiene(actual)) continue;
            visitados.poner(actual, true);
            resultado.agregarAlFinal(actual);
            for (Arista<T> a : vertices.obtener(actual).getAristas()) {
                if (!visitados.contiene(a.getDestino())) pila.apilar(a.getDestino());
            }
        }
        return resultado;
    }

    public boolean hayCamino(T origen, T destino) {
        exigir(destino);
        return recorridoAnchura(origen).contiene(destino);
    }

    // ---------- Internos ----------
    private Vertice<T> exigir(T dato) {
        Vertice<T> v = vertices.obtener(dato);
        if (v == null) throw new IllegalArgumentException("El vértice no existe: " + dato);
        return v;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T dato : orden) {
            sb.append(dato).append(" -> ");
            int i = 0;
            for (Arista<T> a : vertices.obtener(dato).getAristas()) {
                if (i++ > 0) sb.append(", ");
                sb.append(a.getDestino()).append("(").append(a.getPeso()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
