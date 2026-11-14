package co.edu.uniquindio.monedero.estructuras.colas;

import java.util.NoSuchElementException;

/**
 * Cola de prioridad implementada como montículo binario mínimo (min-heap)
 * sobre un arreglo dinámico. El elemento "menor" según compareTo sale primero:
 * para las transacciones programadas, compareTo debe comparar por fecha de
 * ejecución.
 *
 * insertar O(log n), extraerMinimo O(log n), verMinimo O(1).
 */
public class ColaPrioridad<T extends Comparable<? super T>> {
    private Object[] heap;
    private int tamanio;

    public ColaPrioridad() { this(16); }

    public ColaPrioridad(int capacidadInicial) {
        heap = new Object[Math.max(capacidadInicial, 1)];
    }

    public void insertar(T elemento) {
        if (elemento == null) throw new IllegalArgumentException("No se admiten nulos");
        if (tamanio == heap.length) crecer();
        heap[tamanio] = elemento;
        subir(tamanio);
        tamanio++;
    }

    public T extraerMinimo() {
        if (tamanio == 0) throw new NoSuchElementException("La cola de prioridad está vacía");
        T minimo = en(0);
        tamanio--;
        heap[0] = heap[tamanio];
        heap[tamanio] = null;
        if (tamanio > 0) bajar(0);
        return minimo;
    }

    public T verMinimo() {
        if (tamanio == 0) throw new NoSuchElementException("La cola de prioridad está vacía");
        return en(0);
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void limpiar() {
        for (int i = 0; i < tamanio; i++) heap[i] = null;
        tamanio = 0;
    }

    @SuppressWarnings("unchecked")
    private T en(int i) { return (T) heap[i]; }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (en(i).compareTo(en(padre)) >= 0) break;
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int menor = i;
            if (izq < tamanio && en(izq).compareTo(en(menor)) < 0) menor = izq;
            if (der < tamanio && en(der).compareTo(en(menor)) < 0) menor = der;
            if (menor == i) break;
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        Object temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    private void crecer() {
        Object[] nuevo = new Object[heap.length * 2];
        for (int i = 0; i < tamanio; i++) nuevo[i] = heap[i];
        heap = nuevo;
    }
}
