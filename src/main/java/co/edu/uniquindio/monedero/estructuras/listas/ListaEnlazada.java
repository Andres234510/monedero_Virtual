package main.java.co.edu.uniquindio.monedero.estructuras.listas;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada con referencia a la cola.
 * Uso en el proyecto: historial de transacciones por cliente.
 *
 * Complejidades: agregarAlInicio/agregarAlFinal O(1); obtener, insertar,
 * eliminarEn, indiceDe O(n).
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        if (cola == null) cola = nuevo;
        tamanio++;
    }

    public void agregarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cola == null) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tamanio++;
    }

    public void insertar(int indice, T dato) {
        if (indice < 0 || indice > tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + ", tamaño " + tamanio);
        }
        if (indice == 0) { agregarAlInicio(dato); return; }
        if (indice == tamanio) { agregarAlFinal(dato); return; }
        Nodo<T> previo = nodoEn(indice - 1);
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(previo.getSiguiente());
        previo.setSiguiente(nuevo);
        tamanio++;
    }

    public T obtener(int indice) {
        validarIndice(indice);
        return nodoEn(indice).getDato();
    }

    public T primero() {
        if (cabeza == null) throw new NoSuchElementException("La lista está vacía");
        return cabeza.getDato();
    }

    public T ultimo() {
        if (cola == null) throw new NoSuchElementException("La lista está vacía");
        return cola.getDato();
    }

    public T eliminarEn(int indice) {
        validarIndice(indice);
        T eliminado;
        if (indice == 0) {
            eliminado = cabeza.getDato();
            cabeza = cabeza.getSiguiente();
            if (cabeza == null) cola = null;
        } else {
            Nodo<T> previo = nodoEn(indice - 1);
            Nodo<T> objetivo = previo.getSiguiente();
            eliminado = objetivo.getDato();
            previo.setSiguiente(objetivo.getSiguiente());
            if (objetivo == cola) cola = previo;
        }
        tamanio--;
        return eliminado;
    }

    /** Elimina la primera aparición del dato. */
    public boolean eliminar(T dato) {
        int i = indiceDe(dato);
        if (i < 0) return false;
        eliminarEn(i);
        return true;
    }

    public int indiceDe(T dato) {
        int i = 0;
        for (Nodo<T> a = cabeza; a != null; a = a.getSiguiente(), i++) {
            if (iguales(a.getDato(), dato)) return i;
        }
        return -1;
    }

    public boolean contiene(T dato) { return indiceDe(dato) >= 0; }
    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void limpiar() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    private Nodo<T> nodoEn(int indice) {
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) actual = actual.getSiguiente();
        return actual;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + ", tamaño " + tamanio);
        }
    }

    private static boolean iguales(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() { return actual != null; }

            @Override
            public T next() {
                if (actual == null) throw new NoSuchElementException();
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> a = cabeza; a != null; a = a.getSiguiente()) {
            sb.append(a.getDato());
            if (a.getSiguiente() != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
