package main.java.co.edu.uniquindio.monedero.modelo.transaccion;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;

/**
 * Transferencia entre monederos. Si origen y destino son de la misma cuenta es
 * una transferencia interna (entre monederos propios).
 */
public class Transferencia extends Transaccion {
    private final String idCuentaOrigen;
    private final String idMonederoOrigen;
    private final String idCuentaDestino;
    private final String idMonederoDestino;

    public Transferencia(String idCuentaOrigen, String idMonederoOrigen,
                         String idCuentaDestino, String idMonederoDestino,
                         double monto, CategoriaGasto categoria, String descripcion) {
        super(TipoTransaccion.TRANSFERENCIA, monto, categoria, descripcion);
        this.idCuentaOrigen = idCuentaOrigen;
        this.idMonederoOrigen = idMonederoOrigen;
        this.idCuentaDestino = idCuentaDestino;
        this.idMonederoDestino = idMonederoDestino;
    }

    public boolean esInterna() { return idCuentaOrigen.equals(idCuentaDestino); }

    public String getIdCuentaOrigen() { return idCuentaOrigen; }
    public String getIdMonederoOrigen() { return idMonederoOrigen; }
    public String getIdCuentaDestino() { return idCuentaDestino; }
    public String getIdMonederoDestino() { return idMonederoDestino; }
}
