package co.edu.uniquindio.monedero.util;

import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.servicio.Servicios;

public class Sesion {
    private static final Servicios SERVICIOS = new Servicios();
    private static Cliente cliente;

    static {
        DatosPrueba.cargar(SERVICIOS);
    }

    private Sesion() {}

    public static Servicios servicios() { return SERVICIOS; }
    public static Cliente cliente() { return cliente; }
    public static void iniciar(Cliente c) { cliente = c; }
    public static void cerrar() { cliente = null; }
}