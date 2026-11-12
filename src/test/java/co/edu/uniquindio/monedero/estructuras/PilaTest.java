package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.pilas.Pila;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PilaTest {

    @Test
    void esLifo() {
        Pila<String> p = new Pila<>();
        p.apilar("T1");
        p.apilar("T2");
        p.apilar("T3");
        assertEquals("T3", p.cima());
        assertEquals("T3", p.desapilar());
        assertEquals("T2", p.desapilar());
        assertEquals(1, p.tamanio());
    }

    @Test
    void vaciaLanzaExcepcion() {
        Pila<Integer> p = new Pila<>();
        assertTrue(p.estaVacia());
        assertThrows(NoSuchElementException.class, p::desapilar);
        assertThrows(NoSuchElementException.class, p::cima);
    }

    @Test
    void limpiarVaciaLaPila() {
        Pila<Integer> p = new Pila<>();
        p.apilar(1);
        p.apilar(2);
        p.limpiar();
        assertTrue(p.estaVacia());
        assertEquals(0, p.tamanio());
    }
}
