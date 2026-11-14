package co.edu.uniquindio.monedero.estructuras;

import co.edu.uniquindio.monedero.estructuras.colas.ColaPrioridad;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ColaPrioridadTest {

    @Test
    void extraeEnOrdenAscendente() {
        ColaPrioridad<Integer> c = new ColaPrioridad<>(2); // fuerza crecimiento
        int[] datos = {42, 7, 19, 3, 88, 1, 56, 23, 7, 60};
        for (int d : datos) c.insertar(d);
        int anterior = Integer.MIN_VALUE;
        int extraidos = 0;
        while (!c.estaVacia()) {
            int actual = c.extraerMinimo();
            assertTrue(actual >= anterior);
            anterior = actual;
            extraidos++;
        }
        assertEquals(datos.length, extraidos);
    }

    @Test
    void ordenaPorFechaComoLasTransaccionesProgramadas() {
        ColaPrioridad<LocalDate> c = new ColaPrioridad<>();
        c.insertar(LocalDate.of(2026, 12, 1));
        c.insertar(LocalDate.of(2026, 11, 5));
        c.insertar(LocalDate.of(2027, 1, 15));
        assertEquals(LocalDate.of(2026, 11, 5), c.verMinimo());
        assertEquals(LocalDate.of(2026, 11, 5), c.extraerMinimo());
        assertEquals(LocalDate.of(2026, 12, 1), c.extraerMinimo());
        assertEquals(1, c.tamanio());
    }

    @Test
    void vaciaLanzaExcepcionYRechazaNulos() {
        ColaPrioridad<Integer> c = new ColaPrioridad<>();
        assertThrows(NoSuchElementException.class, c::extraerMinimo);
        assertThrows(NoSuchElementException.class, c::verMinimo);
        assertThrows(IllegalArgumentException.class, () -> c.insertar(null));
    }
}
