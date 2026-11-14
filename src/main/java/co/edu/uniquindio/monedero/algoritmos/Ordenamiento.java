package co.edu.uniquindio.monedero.algoritmos;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;

import java.util.Comparator;

/**
 * Ordenamiento por mezcla (merge sort): O(n log n) siempre y estable (los
 * elementos iguales conservan su orden). Devuelve una lista nueva y no toca la
 * original. Se usa en los reportes (top de categorías, clientes, etc.).
 */
public final class Ordenamiento {
    private Ordenamiento() { }

    public static <T> ListaEnlazada<T> ordenar(ListaEnlazada<T> lista, Comparator<? super T> comparador) {
        int n = lista.tamanio();
        Object[] datos = new Object[n];
        int i = 0;
        for (T x : lista) datos[i++] = x;
        mezcla(datos, new Object[n], 0, n, comparador);

        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (Object o : datos) {
            @SuppressWarnings("unchecked") T x = (T) o;
            resultado.agregarAlFinal(x);
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private static <T> void mezcla(Object[] a, Object[] aux, int desde, int hasta, Comparator<? super T> cmp) {
        if (hasta - desde < 2) return;
        int medio = (desde + hasta) >>> 1;
        mezcla(a, aux, desde, medio, cmp);
        mezcla(a, aux, medio, hasta, cmp);
        int i = desde;
        int j = medio;
        int k = desde;
        while (i < medio && j < hasta) {
            aux[k++] = cmp.compare((T) a[i], (T) a[j]) <= 0 ? a[i++] : a[j++];
        }
        while (i < medio) aux[k++] = a[i++];
        while (j < hasta) aux[k++] = a[j++];
        for (k = desde; k < hasta; k++) a[k] = aux[k];
    }

    /** Los primeros n elementos (o todos si hay menos). */
    public static <T> ListaEnlazada<T> primeros(ListaEnlazada<T> lista, int n) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        int i = 0;
        for (T x : lista) {
            if (i++ >= n) break;
            resultado.agregarAlFinal(x);
        }
        return resultado;
    }
}
