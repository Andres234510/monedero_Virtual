package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaEnlazadaTest {

    @Test
    void agregaAlInicioYAlFinal() {
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        l.agregarAlFinal(2);
        l.agregarAlFinal(3);
        l.agregarAlInicio(1);
        assertEquals("[1, 2, 3]", l.toString());
        assertEquals(3, l.tamanio());
        assertEquals(1, l.primero());
        assertEquals(3, l.ultimo());
    }

    @Test
    void insertaEnPosicionesValidas() {
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        l.insertar(0, 10);
        l.insertar(1, 30);
        l.insertar(1, 20);
        assertEquals("[10, 20, 30]", l.toString());
        assertThrows(IndexOutOfBoundsException.class, () -> l.insertar(5, 1));
    }

    @Test
    void eliminaPrimeroMedioUltimoYActualizaCola() {
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        for (int i = 1; i <= 5; i++) l.agregarAlFinal(i);
        assertEquals(1, l.eliminarEn(0));
        assertEquals(3, l.eliminarEn(1));
        assertEquals(5, l.eliminarEn(2));
        assertEquals("[2, 4]", l.toString());
        l.agregarAlFinal(9); // la cola debe haberse actualizado bien
        assertEquals("[2, 4, 9]", l.toString());
    }

    @Test
    void eliminarPorValorYBuscar() {
        ListaEnlazada<String> l = new ListaEnlazada<>();
        l.agregarAlFinal("a");
        l.agregarAlFinal("b");
        assertTrue(l.contiene("b"));
        assertEquals(1, l.indiceDe("b"));
        assertTrue(l.eliminar("a"));
        assertFalse(l.eliminar("zzz"));
        assertEquals(1, l.tamanio());
    }

    @Test
    void vaciaLanzaExcepciones() {
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        assertTrue(l.estaVacia());
        assertThrows(NoSuchElementException.class, l::primero);
        assertThrows(IndexOutOfBoundsException.class, () -> l.obtener(0));
    }

    @Test
    void iteraConForEach() {
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        for (int i = 1; i <= 4; i++) l.agregarAlFinal(i);
        int suma = 0;
        for (int x : l) suma += x;
        assertEquals(10, suma);
    }
}
