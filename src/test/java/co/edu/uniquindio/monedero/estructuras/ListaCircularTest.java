package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.listas.ListaCircular;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaCircularTest {

    @Test
    void agregaYMantieneOrden() {
        ListaCircular<String> l = new ListaCircular<>();
        l.agregar("a");
        l.agregar("b");
        l.agregar("c");
        assertEquals("[a, b, c]", l.toString());
        assertEquals("a", l.primero());
    }

    @Test
    void rotaYRecorreCiclicamente() {
        ListaCircular<Integer> l = new ListaCircular<>();
        l.agregar(1);
        l.agregar(2);
        l.agregar(3);
        assertEquals(1, l.siguiente());
        assertEquals(2, l.siguiente());
        assertEquals(3, l.siguiente());
        assertEquals(1, l.siguiente()); // da la vuelta
        assertEquals(3, l.tamanio());
    }

    @Test
    void bufferCircularDescartaLoMasAntiguo() {
        ListaCircular<Integer> l = new ListaCircular<>(3);
        for (int i = 1; i <= 5; i++) l.agregar(i);
        assertEquals("[3, 4, 5]", l.toString());
        assertEquals(3, l.tamanio());
    }

    @Test
    void eliminaEnCualquierPosicion() {
        ListaCircular<Integer> l = new ListaCircular<>();
        for (int i = 1; i <= 4; i++) l.agregar(i);
        assertTrue(l.eliminar(4));   // el último
        assertTrue(l.eliminar(1));   // el primero
        assertTrue(l.eliminar(2));
        assertEquals("[3]", l.toString());
        assertTrue(l.eliminar(3));
        assertTrue(l.estaVacia());
        assertFalse(l.eliminar(99));
        l.agregar(7);                // sigue funcionando tras vaciarse
        assertEquals("[7]", l.toString());
    }

    @Test
    void vaciaLanzaExcepcion() {
        ListaCircular<Integer> l = new ListaCircular<>();
        assertThrows(NoSuchElementException.class, l::primero);
        assertThrows(NoSuchElementException.class, l::eliminarPrimero);
    }
}
