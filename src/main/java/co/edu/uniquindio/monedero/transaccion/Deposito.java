package co.edu.uniquindio.monedero.transaccion;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;

public class Deposito extends Transaccion {
    private final String idCuentaDestino;
    private final String idMonederoDestino;

    public Deposito(String idCuentaDestino, String idMonederoDestino, double monto, String descripcion) {
        super(TipoTransaccion.DEPOSITO, monto, CategoriaGasto.OTROS, descripcion);
        this.idCuentaDestino = idCuentaDestino;
        this.idMonederoDestino = idMonederoDestino;
    }

    public String getIdCuentaDestino() { return idCuentaDestino; }
    public String getIdMonederoDestino() { return idMonederoDestino; }
}
