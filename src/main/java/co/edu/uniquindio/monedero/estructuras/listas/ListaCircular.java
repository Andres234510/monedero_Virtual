package main.java.co.edu.uniquindio.monedero.estructuras.listas;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista circular simplemente enlazada (solo se guarda el último nodo; el
 * primero es ultimo.siguiente). Uso en el proyecto: notificaciones.
 *
 * Si se crea con capacidad máxima > 0 se comporta como búfer circular:
 * al llenarse, agregar() descarta la notificación más antigua.
 */
public class ListaCircular<T> implements Iterable<T> {
    private Nodo<T> ultimo;
    private int tamanio;
    private final int capacidad; // 0 = ilimitada

    public ListaCircular() { this(0); }

    public ListaCircular(int capacidadMaxima) {
        if (capacidadMaxima < 0) throw new IllegalArgumentException("Capacidad negativa");
        this.capacidad = capacidadMaxima;
    }

    public void agregar(T dato) {
        if (capacidad > 0 && tamanio == capacidad) eliminarPrimero();
        Nodo<T> nuevo = new Nodo<>(dato);
        if (ultimo == null) {
            nuevo.setSiguiente(nuevo);
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamanio++;
    }

    public T primero() {
        if (ultimo == null) throw new NoSuchElementException("La lista está vacía");
        return ultimo.getSiguiente().getDato();
    }

    public T eliminarPrimero() {
        if (ultimo == null) throw new NoSuchElementException("La lista está vacía");
        Nodo<T> primero = ultimo.getSiguiente();
        if (primero == ultimo) {
            ultimo = null;
        } else {
            ultimo.setSiguiente(primero.getSiguiente());
        }
        tamanio--;
        return primero.getDato();
    }

    /** Mueve el primero al final (avanza el "puntero" circular). */
    public void rotar() {
        if (tamanio > 1) ultimo = ultimo.getSiguiente();
    }

    /** Devuelve el primero y rota: sirve para recorrer cíclicamente. */
    public T siguiente() {
        T dato = primero();
        rotar();
        return dato;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + ", tamaño " + tamanio);
        }
        Nodo<T> actual = ultimo.getSiguiente();
        for (int i = 0; i < indice; i++) actual = actual.getSiguiente();
        return actual.getDato();
    }

    public boolean eliminar(T dato) {
        if (ultimo == null) return false;
        Nodo<T> previo = ultimo;
        Nodo<T> actual = ultimo.getSiguiente();
        for (int i = 0; i < tamanio; i++) {
            if (iguales(actual.getDato(), dato)) {
                if (tamanio == 1) {
                    ultimo = null;
                } else {
                    previo.setSiguiente(actual.getSiguiente());
                    if (actual == ultimo) ultimo = previo;
                }
                tamanio--;
                return true;
            }
            previo = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean contiene(T dato) {
        if (ultimo == null) return false;
        Nodo<T> actual = ultimo.getSiguiente();
        for (int i = 0; i < tamanio; i++, actual = actual.getSiguiente()) {
            if (iguales(actual.getDato(), dato)) return true;
        }
        return false;
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void limpiar() {
        ultimo = null;
        tamanio = 0;
    }

    private static boolean iguales(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    /** Recorre una sola vuelta, desde el primero. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = ultimo == null ? null : ultimo.getSiguiente();
            private int restantes = tamanio;

            @Override
            public boolean hasNext() { return restantes > 0; }

            @Override
            public T next() {
                if (restantes == 0) throw new NoSuchElementException();
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                restantes--;
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        int i = 0;
        for (T dato : this) {
            if (i++ > 0) sb.append(", ");
            sb.append(dato);
        }
        return sb.append("]").toString();
    }
}
