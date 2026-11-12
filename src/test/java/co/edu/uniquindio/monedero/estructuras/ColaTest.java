package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.colas.Cola;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ColaTest {

    @Test
    void esFifo() {
        Cola<Integer> c = new Cola<>();
        c.encolar(1);
        c.encolar(2);
        c.encolar(3);
        assertEquals(1, c.frente());
        assertEquals(1, c.desencolar());
        assertEquals(2, c.desencolar());
        assertEquals(1, c.tamanio());
    }

    @Test
    void reutilizableTrasVaciarse() {
        Cola<Integer> c = new Cola<>();
        c.encolar(1);
        c.desencolar();
        assertTrue(c.estaVacia());
        c.encolar(5);
        assertEquals(5, c.desencolar());
    }

    @Test
    void vaciaLanzaExcepcion() {
        Cola<Integer> c = new Cola<>();
        assertThrows(NoSuchElementException.class, c::desencolar);
        assertThrows(NoSuchElementException.class, c::frente);
    }
}
