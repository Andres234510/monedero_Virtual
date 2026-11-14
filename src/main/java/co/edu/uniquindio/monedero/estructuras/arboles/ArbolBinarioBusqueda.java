package co.edu.uniquindio.monedero.estructuras.arboles;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;

import java.util.NoSuchElementException;

/**
 * Árbol binario de búsqueda (sin balanceo) clave-valor.
 * Uso en el proyecto: puntos acumulados por cliente.
 *
 * Promedio O(log n); peor caso O(n) si las claves llegan ordenadas
 * (por eso los rangos usan {@link ArbolAVL}).
 * Si se inserta una clave existente, se reemplaza su valor.
 */
public class ArbolBinarioBusqueda<K extends Comparable<K>, V> {
    private NodoArbol<K, V> raiz;
    private int tamanio;

    public void insertar(K clave, V valor) {
        if (clave == null) throw new IllegalArgumentException("Clave nula");
        raiz = insertar(raiz, clave, valor);
    }

    private NodoArbol<K, V> insertar(NodoArbol<K, V> nodo, K clave, V valor) {
        if (nodo == null) {
            tamanio++;
            return new NodoArbol<>(clave, valor);
        }
        int cmp = clave.compareTo(nodo.getClave());
        if (cmp < 0) nodo.setIzquierdo(insertar(nodo.getIzquierdo(), clave, valor));
        else if (cmp > 0) nodo.setDerecho(insertar(nodo.getDerecho(), clave, valor));
        else nodo.setValor(valor);
        return nodo;
    }

    /** Devuelve el valor o null si la clave no existe. */
    public V buscar(K clave) {
        NodoArbol<K, V> nodo = buscarNodo(clave);
        return nodo == null ? null : nodo.getValor();
    }

    public boolean contiene(K clave) { return buscarNodo(clave) != null; }

    private NodoArbol<K, V> buscarNodo(K clave) {
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.getClave());
            if (cmp == 0) return actual;
            actual = cmp < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    public boolean eliminar(K clave) {
        int antes = tamanio;
        raiz = eliminar(raiz, clave);
        return tamanio < antes;
    }

    private NodoArbol<K, V> eliminar(NodoArbol<K, V> nodo, K clave) {
        if (nodo == null) return null;
        int cmp = clave.compareTo(nodo.getClave());
        if (cmp < 0) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), clave));
        } else if (cmp > 0) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), clave));
        } else {
            if (nodo.getIzquierdo() == null) { tamanio--; return nodo.getDerecho(); }
            if (nodo.getDerecho() == null) { tamanio--; return nodo.getIzquierdo(); }
            // Dos hijos: se reemplaza por el sucesor en orden
            NodoArbol<K, V> sucesor = nodoMinimo(nodo.getDerecho());
            nodo.setClave(sucesor.getClave());
            nodo.setValor(sucesor.getValor());
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getClave()));
        }
        return nodo;
    }

    public K minimo() {
        if (raiz == null) throw new NoSuchElementException("El árbol está vacío");
        return nodoMinimo(raiz).getClave();
    }

    public K maximo() {
        if (raiz == null) throw new NoSuchElementException("El árbol está vacío");
        NodoArbol<K, V> actual = raiz;
        while (actual.getDerecho() != null) actual = actual.getDerecho();
        return actual.getClave();
    }

    private NodoArbol<K, V> nodoMinimo(NodoArbol<K, V> nodo) {
        while (nodo.getIzquierdo() != null) nodo = nodo.getIzquierdo();
        return nodo;
    }

    public int altura() { return altura(raiz); }

    private int altura(NodoArbol<K, V> nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacio() { return tamanio == 0; }

    public void limpiar() {
        raiz = null;
        tamanio = 0;
    }

    /** Claves en orden ascendente (recorrido inorden). */
    public ListaEnlazada<K> clavesEnOrden() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        enOrden(raiz, claves, null);
        return claves;
    }

    /** Valores en orden ascendente de clave. */
    public ListaEnlazada<V> valoresEnOrden() {
        ListaEnlazada<V> valores = new ListaEnlazada<>();
        enOrden(raiz, null, valores);
        return valores;
    }

    private void enOrden(NodoArbol<K, V> nodo, ListaEnlazada<K> claves, ListaEnlazada<V> valores) {
        if (nodo == null) return;
        enOrden(nodo.getIzquierdo(), claves, valores);
        if (claves != null) claves.agregarAlFinal(nodo.getClave());
        if (valores != null) valores.agregarAlFinal(nodo.getValor());
        enOrden(nodo.getDerecho(), claves, valores);
    }

    /** Valores cuya clave está en [min, max], en orden ascendente. */
    public ListaEnlazada<V> valoresEnRango(K min, K max) {
        ListaEnlazada<V> resultado = new ListaEnlazada<>();
        enRango(raiz, min, max, resultado);
        return resultado;
    }

    private void enRango(NodoArbol<K, V> nodo, K min, K max, ListaEnlazada<V> resultado) {
        if (nodo == null) return;
        if (min.compareTo(nodo.getClave()) < 0) enRango(nodo.getIzquierdo(), min, max, resultado);
        if (min.compareTo(nodo.getClave()) <= 0 && max.compareTo(nodo.getClave()) >= 0) {
            resultado.agregarAlFinal(nodo.getValor());
        }
        if (max.compareTo(nodo.getClave()) > 0) enRango(nodo.getDerecho(), min, max, resultado);
    }
}
