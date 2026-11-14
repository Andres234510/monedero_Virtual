package co.edu.uniquindio.monedero.estructuras.grafos;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;

/** Vértice con su lista de adyacencia (aristas salientes). */
public class Vertice<T> {
    private final T dato;
    private final ListaEnlazada<Arista<T>> aristas = new ListaEnlazada<>();

    public Vertice(T dato) { this.dato = dato; }

    public T getDato() { return dato; }
    public ListaEnlazada<Arista<T>> getAristas() { return aristas; }

    public Arista<T> buscarArista(T destino) {
        for (Arista<T> a : aristas) {
            if (a.getDestino().equals(destino)) return a;
        }
        return null;
    }

    public boolean quitarArista(T destino) {
        int i = 0;
        for (Arista<T> a : aristas) {
            if (a.getDestino().equals(destino)) {
                aristas.eliminarEn(i);
                return true;
            }
            i++;
        }
        return false;
    }
}
