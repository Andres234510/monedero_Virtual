package co.edu.uniquindio.monedero.modelo.puntos;

import co.edu.uniquindio.monedero.util.Constantes;

/** Beneficios canjeables por puntos (enunciado). 'valor' = %, días o unidades según el beneficio. */
public enum Beneficio {
    DESCUENTO_COMISION_TRANSFERENCIA(Constantes.COSTO_DESCUENTO_TRANSFERENCIA,
            Constantes.PORCENTAJE_DESCUENTO_TRANSFERENCIA,
            "Reducción del 10% en la comisión por transferencias"),
    MES_SIN_CARGOS_RETIRO(Constantes.COSTO_MES_SIN_CARGOS_RETIRO,
            Constantes.DIAS_SIN_CARGOS_RETIRO,
            "Un mes sin cargos por retiros"),
    BONO_SALDO(Constantes.COSTO_BONO_SALDO,
            (int) Constantes.MONTO_BONO_SALDO,
            "Bono de saldo de 50 unidades en el monedero");

    private final long costoPuntos;
    private final int valor;
    private final String descripcion;

    Beneficio(long costoPuntos, int valor, String descripcion) {
        this.costoPuntos = costoPuntos;
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public long getCostoPuntos() { return costoPuntos; }
    public int getValor() { return valor; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return descripcion + " (" + costoPuntos + " pts)"; }
}
