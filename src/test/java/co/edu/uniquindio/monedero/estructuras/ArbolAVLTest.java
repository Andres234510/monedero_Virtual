package test.java.co.edu.uniquindio.monedero.estructuras;

import main.java.co.edu.uniquindio.monedero.estructuras.arboles.ArbolAVL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArbolAVLTest {

    @Test
    void clavesOrdenadasNoDegeneranEnLista() {
        ArbolAVL<Integer, Integer> a = new ArbolAVL<>();
        for (int i = 1; i <= 1000; i++) a.insertar(i, i);
        assertEquals(1000, a.tamanio());
        assertTrue(a.estaBalanceado());
        assertTrue(a.altura() <= 14, "Altura esperada <= 1.44*log2(n), fue " + a.altura());
    }

    @Test
    void rotacionesSimplesYDobles() {
        ArbolAVL<Integer, Integer> ll = new ArbolAVL<>();
        for (int k : new int[]{30, 20, 10}) ll.insertar(k, k);   // izquierda-izquierda
        ArbolAVL<Integer, Integer> lr = new ArbolAVL<>();
        for (int k : new int[]{30, 10, 20}) lr.insertar(k, k);   // izquierda-derecha
        ArbolAVL<Integer, Integer> rl = new ArbolAVL<>();
        for (int k : new int[]{10, 30, 20}) rl.insertar(k, k);   // derecha-izquierda
        for (ArbolAVL<Integer, Integer> a : java.util.List.of(ll, lr, rl)) {
            assertEquals(2, a.altura());
            assertTrue(a.estaBalanceado());
            assertEquals("[10, 20, 30]", a.clavesEnOrden().toString());
        }
    }

    @Test
    void eliminarMantieneElBalance() {
        ArbolAVL<Integer, Integer> a = new ArbolAVL<>();
        for (int i = 1; i <= 200; i++) a.insertar(i, i);
        for (int i = 1; i <= 200; i += 2) assertTrue(a.eliminar(i));
        assertEquals(100, a.tamanio());
        assertTrue(a.estaBalanceado());
        assertFalse(a.contiene(51));
        assertTrue(a.contiene(52));
        assertFalse(a.eliminar(51));
    }

    @Test
    void pisoClasificaRangosPorPuntos() {
        // Umbral mínimo de cada rango -> nombre del rango
        ArbolAVL<Integer, String> rangos = new ArbolAVL<>();
        rangos.insertar(0, "BRONCE");
        rangos.insertar(501, "PLATA");
        rangos.insertar(1001, "ORO");
        rangos.insertar(5001, "PLATINO");

        assertEquals("BRONCE", rangos.valorPiso(0));
        assertEquals("BRONCE", rangos.valorPiso(500));
        assertEquals("PLATA", rangos.valorPiso(501));
        assertEquals("PLATA", rangos.valorPiso(1000));
        assertEquals("ORO", rangos.valorPiso(1001));
        assertEquals("ORO", rangos.valorPiso(5000));
        assertEquals("PLATINO", rangos.valorPiso(5001));
        assertEquals("PLATINO", rangos.valorPiso(999999));
        assertNull(rangos.valorPiso(-1));
        assertEquals(501, rangos.clavePiso(700));
    }

    @Test
    void rangoYExtremos() {
        ArbolAVL<Integer, String> a = new ArbolAVL<>();
        for (int k : new int[]{5, 1, 9, 3, 7}) a.insertar(k, "v" + k);
        assertEquals(1, a.minimo());
        assertEquals(9, a.maximo());
        assertEquals("[v3, v5, v7]", a.valoresEnRango(2, 8).toString());
    }
}
