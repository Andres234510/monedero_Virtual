package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.arboles.ArbolBinarioBusqueda;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArbolBinarioBusquedaTest {

    private ArbolBinarioBusqueda<Integer, String> arbolDePrueba() {
        ArbolBinarioBusqueda<Integer, String> a = new ArbolBinarioBusqueda<>();
        int[] claves = {50, 30, 70, 20, 40, 60, 80};
        for (int k : claves) a.insertar(k, "c" + k);
        return a;
    }

    @Test
    void insertaYBusca() {
        ArbolBinarioBusqueda<Integer, String> a = arbolDePrueba();
        assertEquals(7, a.tamanio());
        assertEquals("c40", a.buscar(40));
        assertNull(a.buscar(99));
        assertTrue(a.contiene(80));
    }

    @Test
    void claveRepetidaActualizaElValor() {
        ArbolBinarioBusqueda<String, Integer> puntos = new ArbolBinarioBusqueda<>();
        puntos.insertar("cliente-1", 100);
        puntos.insertar("cliente-1", 250);
        assertEquals(1, puntos.tamanio());
        assertEquals(250, puntos.buscar("cliente-1"));
    }

    @Test
    void inordenDevuelveClavesOrdenadas() {
        assertEquals("[20, 30, 40, 50, 60, 70, 80]", arbolDePrueba().clavesEnOrden().toString());
    }

    @Test
    void minimoMaximoYRango() {
        ArbolBinarioBusqueda<Integer, String> a = arbolDePrueba();
        assertEquals(20, a.minimo());
        assertEquals(80, a.maximo());
        assertEquals("[c30, c40, c50, c60]", a.valoresEnRango(25, 65).toString());
    }

    @Test
    void eliminaHojaUnHijoYDosHijos() {
        ArbolBinarioBusqueda<Integer, String> a = arbolDePrueba();
        assertTrue(a.eliminar(20));            // hoja
        assertTrue(a.eliminar(30));            // un hijo (queda el 40)
        assertTrue(a.eliminar(50));            // raíz con dos hijos
        assertFalse(a.eliminar(999));
        assertEquals("[40, 60, 70, 80]", a.clavesEnOrden().toString());
        assertEquals(4, a.tamanio());
    }

    @Test
    void vacioLanzaExcepcion() {
        ArbolBinarioBusqueda<Integer, String> a = new ArbolBinarioBusqueda<>();
        assertThrows(NoSuchElementException.class, a::minimo);
        assertThrows(NoSuchElementException.class, a::maximo);
        assertEquals(0, a.altura());
    }
}
