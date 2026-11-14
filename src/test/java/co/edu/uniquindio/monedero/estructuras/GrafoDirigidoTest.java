package co.edu.uniquindio.monedero.estructuras;

import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrafoDirigidoTest {

    private GrafoDirigido<String> grafoDePrueba() {
        // A -> B -> D ; A -> C -> D ; E aislado
        GrafoDirigido<String> g = new GrafoDirigido<>();
        for (String v : new String[]{"A", "B", "C", "D", "E"}) g.agregarVertice(v);
        g.agregarArista("A", "B", 10);
        g.agregarArista("A", "C", 20);
        g.agregarArista("B", "D", 5);
        g.agregarArista("C", "D", 7);
        return g;
    }

    @Test
    void esDirigido() {
        GrafoDirigido<String> g = grafoDePrueba();
        assertTrue(g.existeArista("A", "B"));
        assertFalse(g.existeArista("B", "A"));
        assertEquals(4, g.numAristas());
        assertEquals(5, g.numVertices());
        assertEquals(10.0, g.pesoArista("A", "B"));
        assertEquals(0.0, g.pesoArista("B", "A"));
    }

    @Test
    void gradosYPesosTotales() {
        GrafoDirigido<String> g = grafoDePrueba();
        assertEquals(2, g.gradoSalida("A"));
        assertEquals(2, g.gradoEntrada("D"));
        assertEquals(30.0, g.pesoTotalSalida("A"));
        assertEquals(12.0, g.pesoTotalEntrada("D"));
    }

    @Test
    void acumularSumaMontosYCreaVertices() {
        GrafoDirigido<String> g = new GrafoDirigido<>();
        g.acumularArista("Alimentacion", "Transporte", 100);
        g.acumularArista("Alimentacion", "Transporte", 50);
        assertEquals(2, g.numVertices());
        assertEquals(1, g.numAristas());
        assertEquals(150.0, g.pesoArista("Alimentacion", "Transporte"));
    }

    @Test
    void recorridosAlcanzanSoloLoAlcanzable() {
        GrafoDirigido<String> g = grafoDePrueba();
        assertEquals("[A, B, C, D]", g.recorridoAnchura("A").toString());
        assertEquals(4, g.recorridoProfundidad("A").tamanio());
        assertTrue(g.hayCamino("A", "D"));
        assertFalse(g.hayCamino("D", "A"));
        assertFalse(g.hayCamino("A", "E"));
    }

    @Test
    void eliminaAristasYVertices() {
        GrafoDirigido<String> g = grafoDePrueba();
        assertTrue(g.eliminarArista("A", "B"));
        assertFalse(g.eliminarArista("A", "B"));
        assertEquals(3, g.numAristas());
        assertTrue(g.eliminarVertice("D"));   // quita B->D y C->D
        assertEquals(1, g.numAristas());      // solo queda A->C
        assertFalse(g.contieneVertice("D"));
        assertEquals(4, g.numVertices());
    }

    @Test
    void verticeInexistenteLanzaExcepcion() {
        GrafoDirigido<String> g = grafoDePrueba();
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista("A", "Z", 1));
        assertThrows(IllegalArgumentException.class, () -> g.recorridoAnchura("Z"));
    }
}
