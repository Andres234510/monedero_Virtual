package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.hash.TablaHash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablaHashTest {

    /** Clave con hashCode constante: obliga a que todas colisionen. */
    private static final class ClaveColision {
        private final int id;
        ClaveColision(int id) { this.id = id; }
        @Override public int hashCode() { return 7; }
        @Override public boolean equals(Object o) {
            return o instanceof ClaveColision c && c.id == id;
        }
    }

    @Test
    void poneObtieneYActualiza() {
        TablaHash<String, Integer> t = new TablaHash<>();
        t.poner("ana", 100);
        t.poner("luis", 200);
        t.poner("ana", 150);
        assertEquals(2, t.tamanio());
        assertEquals(150, t.obtener("ana"));
        assertNull(t.obtener("nadie"));
        assertEquals(0, t.obtenerOPorDefecto("nadie", 0));
    }

    @Test
    void eliminaYReportaElValor() {
        TablaHash<String, Integer> t = new TablaHash<>();
        t.poner("a", 1);
        assertEquals(1, t.eliminar("a"));
        assertNull(t.eliminar("a"));
        assertFalse(t.contiene("a"));
        assertTrue(t.estaVacia());
    }

    @Test
    void redimensionaConMuchasClaves() {
        TablaHash<Integer, String> t = new TablaHash<>(4);
        for (int i = 0; i < 5000; i++) t.poner(i, "v" + i);
        assertEquals(5000, t.tamanio());
        assertTrue(t.capacidad() >= 5000 / 0.75);
        for (int i = 0; i < 5000; i++) assertEquals("v" + i, t.obtener(i));
        assertEquals(5000, t.claves().tamanio());
        assertEquals(5000, t.valores().tamanio());
    }

    @Test
    void manejaColisiones() {
        TablaHash<ClaveColision, Integer> t = new TablaHash<>();
        for (int i = 0; i < 50; i++) t.poner(new ClaveColision(i), i * 10);
        assertEquals(50, t.tamanio());
        assertEquals(250, t.obtener(new ClaveColision(25)));
        assertEquals(250, t.eliminar(new ClaveColision(25)));
        assertNull(t.obtener(new ClaveColision(25)));
        assertEquals(490, t.obtener(new ClaveColision(49)));
        assertEquals(49, t.tamanio());
    }

    @Test
    void rechazaClaveNula() {
        TablaHash<String, Integer> t = new TablaHash<>();
        assertThrows(IllegalArgumentException.class, () -> t.poner(null, 1));
        assertNull(t.obtener(null));
    }
}
