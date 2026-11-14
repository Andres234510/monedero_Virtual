package co.edu.uniquindio.monedero.estructuras.arboles;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;

import java.util.NoSuchElementException;

/**
 * Árbol AVL (autobalanceado) clave-valor. Uso en el proyecto: rangos de
 * clientes. Garantiza altura O(log n), así que buscar, insertar y eliminar
 * son O(log n) incluso en el peor caso.
 *
 * Truco para los rangos: guardar los umbrales (0, 501, 1001, 5001) como
 * claves y usar {@link #valorPiso(Comparable)} con los puntos del cliente.
 */
public class ArbolAVL<K extends Comparable<K>, V> {
    private NodoArbol<K, V> raiz;
    private int tamanio;

    // ---------- Inserción ----------
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
        else { nodo.setValor(valor); return nodo; }
        return balancear(nodo);
    }

    // ---------- Eliminación ----------
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
            NodoArbol<K, V> sucesor = nodoMinimo(nodo.getDerecho());
            nodo.setClave(sucesor.getClave());
            nodo.setValor(sucesor.getValor());
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getClave()));
        }
        return balancear(nodo);
    }

    // ---------- Consultas ----------
    public V buscar(K clave) {
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.getClave());
            if (cmp == 0) return actual.getValor();
            actual = cmp < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    public boolean contiene(K clave) {
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.getClave());
            if (cmp == 0) return true;
            actual = cmp < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return false;
    }

    /** Mayor clave menor o igual a la dada; null si no existe. */
    public K clavePiso(K clave) {
        NodoArbol<K, V> nodo = nodoPiso(clave);
        return nodo == null ? null : nodo.getClave();
    }

    /** Valor asociado a la mayor clave menor o igual a la dada; null si no existe. */
    public V valorPiso(K clave) {
        NodoArbol<K, V> nodo = nodoPiso(clave);
        return nodo == null ? null : nodo.getValor();
    }

    private NodoArbol<K, V> nodoPiso(K clave) {
        NodoArbol<K, V> mejor = null;
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.getClave());
            if (cmp == 0) return actual;
            if (cmp < 0) {
                actual = actual.getIzquierdo();
            } else {
                mejor = actual;
                actual = actual.getDerecho();
            }
        }
        return mejor;
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
    public int tamanio() { return tamanio; }
    public boolean estaVacio() { return tamanio == 0; }

    public void limpiar() {
        raiz = null;
        tamanio = 0;
    }

    public ListaEnlazada<K> clavesEnOrden() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        enOrden(raiz, claves, null);
        return claves;
    }

    public ListaEnlazada<V> valoresEnOrden() {
        ListaEnlazada<V> valores = new ListaEnlazada<>();
        enOrden(raiz, null, valores);
        return valores;
    }

    public ListaEnlazada<V> valoresEnRango(K min, K max) {
        ListaEnlazada<V> resultado = new ListaEnlazada<>();
        enRango(raiz, min, max, resultado);
        return resultado;
    }

    /** Verifica la propiedad AVL en todo el árbol (útil en pruebas). */
    public boolean estaBalanceado() { return verificar(raiz) >= 0; }

    // ---------- Internos ----------
    private int altura(NodoArbol<K, V> nodo) { return nodo == null ? 0 : nodo.getAltura(); }

    private void actualizarAltura(NodoArbol<K, V> nodo) {
        nodo.setAltura(1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
    }

    private int factorBalance(NodoArbol<K, V> nodo) {
        return nodo == null ? 0 : altura(nodo.getIzquierdo()) - altura(nodo.getDerecho());
    }

    private NodoArbol<K, V> rotarDerecha(NodoArbol<K, V> y) {
        NodoArbol<K, V> x = y.getIzquierdo();
        y.setIzquierdo(x.getDerecho());
        x.setDerecho(y);
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private NodoArbol<K, V> rotarIzquierda(NodoArbol<K, V> x) {
        NodoArbol<K, V> y = x.getDerecho();
        x.setDerecho(y.getIzquierdo());
        y.setIzquierdo(x);
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    private NodoArbol<K, V> balancear(NodoArbol<K, V> nodo) {
        actualizarAltura(nodo);
        int balance = factorBalance(nodo);
        if (balance > 1) {                                   // pesado a la izquierda
            if (factorBalance(nodo.getIzquierdo()) < 0) {    // caso izquierda-derecha
                nodo.setIzquierdo(rotarIzquierda(nodo.getIzquierdo()));
            }
            return rotarDerecha(nodo);
        }
        if (balance < -1) {                                  // pesado a la derecha
            if (factorBalance(nodo.getDerecho()) > 0) {      // caso derecha-izquierda
                nodo.setDerecho(rotarDerecha(nodo.getDerecho()));
            }
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    private int verificar(NodoArbol<K, V> nodo) {
        if (nodo == null) return 0;
        int hi = verificar(nodo.getIzquierdo());
        int hd = verificar(nodo.getDerecho());
        if (hi < 0 || hd < 0 || Math.abs(hi - hd) > 1) return -1;
        return 1 + Math.max(hi, hd);
    }

    private void enOrden(NodoArbol<K, V> nodo, ListaEnlazada<K> claves, ListaEnlazada<V> valores) {
        if (nodo == null) return;
        enOrden(nodo.getIzquierdo(), claves, valores);
        if (claves != null) claves.agregarAlFinal(nodo.getClave());
        if (valores != null) valores.agregarAlFinal(nodo.getValor());
        enOrden(nodo.getDerecho(), claves, valores);
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
