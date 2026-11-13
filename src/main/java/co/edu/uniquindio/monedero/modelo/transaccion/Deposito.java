package main.java.co.edu.uniquindio.monedero.modelo.transaccion;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;

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
