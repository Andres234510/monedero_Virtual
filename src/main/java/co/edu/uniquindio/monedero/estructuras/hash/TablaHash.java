package main.java.co.edu.uniquindio.monedero.estructuras.hash;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;

/**
 * Tabla hash con encadenamiento separado y redimensionamiento dinámico.
 * Uso en el proyecto: acceso O(1) promedio a clientes y sus puntos por id.
 *
 * - La capacidad siempre es potencia de 2 (índice = hash & (capacidad-1)).
 * - Al superar el factor de carga 0.75 la capacidad se duplica y se reubican
 *   las entradas.
 * - Las claves deben implementar bien equals() y hashCode().
 */
public class TablaHash<K, V> {
    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    private EntradaHash<K, V>[] tabla;
    private int tamanio;

    public TablaHash() { this(CAPACIDAD_INICIAL); }

    public TablaHash(int capacidadInicial) {
        tabla = crearTabla(potenciaDeDosDesde(capacidadInicial));
    }

    public void poner(K clave, V valor) {
        if (clave == null) throw new IllegalArgumentException("Clave nula");
        EntradaHash<K, V> existente = buscarEntrada(clave);
        if (existente != null) {
            existente.setValor(valor);
            return;
        }
        if (tamanio + 1 > tabla.length * FACTOR_CARGA) redimensionar();
        int i = indice(clave, tabla.length);
        EntradaHash<K, V> nueva = new EntradaHash<>(clave, valor);
        nueva.setSiguiente(tabla[i]);
        tabla[i] = nueva;
        tamanio++;
    }

    /** Devuelve el valor o null si la clave no existe. */
    public V obtener(K clave) {
        EntradaHash<K, V> e = buscarEntrada(clave);
        return e == null ? null : e.getValor();
    }

    public V obtenerOPorDefecto(K clave, V porDefecto) {
        EntradaHash<K, V> e = buscarEntrada(clave);
        return e == null ? porDefecto : e.getValor();
    }

    public boolean contiene(K clave) { return buscarEntrada(clave) != null; }

    /** Elimina la clave; devuelve el valor que tenía o null si no existía. */
    public V eliminar(K clave) {
        if (clave == null) return null;
        int i = indice(clave, tabla.length);
        EntradaHash<K, V> previa = null;
        for (EntradaHash<K, V> e = tabla[i]; e != null; previa = e, e = e.getSiguiente()) {
            if (e.getClave().equals(clave)) {
                if (previa == null) tabla[i] = e.getSiguiente();
                else previa.setSiguiente(e.getSiguiente());
                tamanio--;
                return e.getValor();
            }
        }
        return null;
    }

    public ListaEnlazada<K> claves() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        for (EntradaHash<K, V> cabeza : tabla) {
            for (EntradaHash<K, V> e = cabeza; e != null; e = e.getSiguiente()) {
                claves.agregarAlFinal(e.getClave());
            }
        }
        return claves;
    }

    public ListaEnlazada<V> valores() {
        ListaEnlazada<V> valores = new ListaEnlazada<>();
        for (EntradaHash<K, V> cabeza : tabla) {
            for (EntradaHash<K, V> e = cabeza; e != null; e = e.getSiguiente()) {
                valores.agregarAlFinal(e.getValor());
            }
        }
        return valores;
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }
    public int capacidad() { return tabla.length; }

    public void limpiar() {
        tabla = crearTabla(CAPACIDAD_INICIAL);
        tamanio = 0;
    }

    // ---------- Internos ----------
    private EntradaHash<K, V> buscarEntrada(K clave) {
        if (clave == null) return null;
        for (EntradaHash<K, V> e = tabla[indice(clave, tabla.length)]; e != null; e = e.getSiguiente()) {
            if (e.getClave().equals(clave)) return e;
        }
        return null;
    }

    private static int indice(Object clave, int longitud) {
        int h = clave.hashCode();
        h ^= (h >>> 16); // mezcla los bits altos con los bajos
        return h & (longitud - 1);
    }

    private void redimensionar() {
        EntradaHash<K, V>[] nueva = crearTabla(tabla.length * 2);
        for (EntradaHash<K, V> cabeza : tabla) {
            EntradaHash<K, V> e = cabeza;
            while (e != null) {
                EntradaHash<K, V> siguiente = e.getSiguiente();
                int i = indice(e.getClave(), nueva.length);
                e.setSiguiente(nueva[i]);
                nueva[i] = e;
                e = siguiente;
            }
        }
        tabla = nueva;
    }

    private static int potenciaDeDosDesde(int minimo) {
        int c = 1;
        while (c < minimo) c <<= 1;
        return c;
    }

    @SuppressWarnings("unchecked")
    private EntradaHash<K, V>[] crearTabla(int capacidad) {
        return (EntradaHash<K, V>[]) new EntradaHash[capacidad];
    }
}
