package main.java.co.edu.uniquindio.monedero.estructuras.colas;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.Nodo;

import java.util.NoSuchElementException;

/** Cola (FIFO) sobre nodos enlazados. Todas las operaciones son O(1). */
public class Cola<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamanio++;
    }

    public T desencolar() {
        if (primero == null) throw new NoSuchElementException("La cola está vacía");
        T dato = primero.getDato();
        primero = primero.getSiguiente();
        if (primero == null) ultimo = null;
        tamanio--;
        return dato;
    }

    public T frente() {
        if (primero == null) throw new NoSuchElementException("La cola está vacía");
        return primero.getDato();
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void limpiar() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> a = primero; a != null; a = a.getSiguiente()) {
            sb.append(a.getDato());
            if (a.getSiguiente() != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
