package co.edu.uniquindio.monedero.transaccion;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;

/** Un retiro es también un "gasto": lleva categoría para el análisis de patrones. */
public class Retiro extends Transaccion {
    private final String idCuentaOrigen;
    private final String idMonederoOrigen;

    public Retiro(String idCuentaOrigen, String idMonederoOrigen, double monto,
                  CategoriaGasto categoria, String descripcion) {
        super(TipoTransaccion.RETIRO, monto, categoria, descripcion);
        this.idCuentaOrigen = idCuentaOrigen;
        this.idMonederoOrigen = idMonederoOrigen;
    }

    public String getIdCuentaOrigen() { return idCuentaOrigen; }
    public String getIdMonederoOrigen() { return idMonederoOrigen; }
}
