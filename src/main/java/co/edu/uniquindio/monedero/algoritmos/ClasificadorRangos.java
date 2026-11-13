package main.java.co.edu.uniquindio.monedero.algoritmos;

import main.java.co.edu.uniquindio.monedero.estructuras.arboles.ArbolAVL;
import main.java.co.edu.uniquindio.monedero.modelo.puntos.Rango;

/**
 * Clasifica puntos en rangos usando un árbol AVL de umbrales: cada rango se
 * guarda con su puntaje mínimo como clave y la búsqueda es un "piso"
 * (mayor umbral <= puntos), O(log r) con r = número de rangos.
 *
 * Con 4 rangos una cadena de if sería igual de rápida; el AVL es lo que pide
 * el enunciado y deja agregar rangos nuevos sin tocar el algoritmo.
 */
public class ClasificadorRangos {
    private final ArbolAVL<Long, Rango> umbrales = new ArbolAVL<>();

    public ClasificadorRangos() {
        for (Rango r : Rango.values()) umbrales.insertar(r.getPuntosMinimos(), r);
    }

    public Rango clasificar(long puntos) {
        Rango r = umbrales.valorPiso(Math.max(puntos, 0));
        return r == null ? Rango.BRONCE : r;
    }

    /** true si pasar de 'antes' a 'despues' puntos cambia el rango. */
    public boolean cambiaDeRango(long antes, long despues) {
        return clasificar(antes) != clasificar(despues);
    }

    /** Puntos que faltan para el siguiente rango; 0 si ya es el máximo. */
    public long puntosFaltantes(long puntos) {
        Rango siguiente = clasificar(puntos).siguiente();
        return siguiente == null ? 0 : siguiente.getPuntosMinimos() - Math.max(puntos, 0);
    }
}
