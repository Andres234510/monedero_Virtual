package co.edu.uniquindio.monedero.util;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.servicio.Servicios;

public class DatosPrueba {
    private DatosPrueba() {}

    public static void cargar(Servicios s) {
        try {
            s.clientes().registrar("1001", "Cliente Demo", "1234");
            s.clientes().registrar("1002", "Ana Gómez", "1234");
            s.clientes().registrar("1003", "Luis Pérez", "1234");

            CategoriaGasto cat = CategoriaGasto.values()[0];

            s.transacciones().depositar("1001", null, 1000000, "Depósito inicial");
            s.transacciones().depositar("1001", null, 500000, "Salario");
            s.transacciones().retirar("1001", null, 120000, cat, "Compras");
            s.transacciones().transferir("1001", null, "1002", null, 200000, cat, "Pago a Ana");

            s.transacciones().depositar("1002", null, 800000, "Depósito inicial");
            s.transacciones().depositar("1003", null, 6000000, "Depósito grande");
        } catch (Exception e) {
            System.err.println("Error cargando datos de prueba: " + e.getMessage());
        }
    }
}