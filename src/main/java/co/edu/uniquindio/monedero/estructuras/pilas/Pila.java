package main.java.co.edu.uniquindio.monedero.estructuras.pilas;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.Nodo;

import java.util.NoSuchElementException;

/**
 * Pila (LIFO) sobre nodos enlazados. Uso en el proyecto: transacciones
 * reversibles (la última transacción es la primera en poder revertirse).
 * Todas las operaciones son O(1).
 */
public class Pila<T> {
    private Nodo<T> tope;
    private int tamanio;

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    public T desapilar() {
        if (tope == null) throw new NoSuchElementException("La pila está vacía");
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    public T cima() {
        if (tope == null) throw new NoSuchElementException("La pila está vacía");
        return tope.getDato();
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void limpiar() {
        tope = null;
        tamanio = 0;
    }

    /** Muestra de la cima hacia la base. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> a = tope; a != null; a = a.getSiguiente()) {
            sb.append(a.getDato());
            if (a.getSiguiente() != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
