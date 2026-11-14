package co.edu.uniquindio.monedero.algoritmos;

import co.edu.uniquindio.monedero.modelo.transaccion.Deposito;
import co.edu.uniquindio.monedero.modelo.transaccion.Retiro;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;

import java.util.function.ToDoubleFunction;

/**
 * Verificación de integridad con recursión "divide y vencerás": la suma se
 * reparte por mitades, así la profundidad es O(log n) y no se desborda la pila
 * con historiales largos (una recursión elemento a elemento sí lo haría).
 */
public final class VerificadorRecursivo {
    private VerificadorRecursivo() { }

    /** Suma f(t) para t en txs[desde, hasta). */
    public static double sumar(Transaccion[] txs, int desde, int hasta, ToDoubleFunction<Transaccion> f) {
        if (desde >= hasta) return 0;
        if (hasta - desde == 1) return f.applyAsDouble(txs[desde]);
        int medio = (desde + hasta) >>> 1;
        return sumar(txs, desde, medio, f) + sumar(txs, medio, hasta, f);
    }

    /**
     * Cuánto mueve la transacción el saldo de ese monedero. Las revertidas
     * aportan 0 porque su reversión devolvió exactamente lo que movieron.
     */
    public static double aporte(Transaccion t, String idMonedero) {
        if (t.estaRevertida()) return 0;
        if (t instanceof Deposito d) {
            return d.getIdMonederoDestino().equals(idMonedero) ? d.getMonto() : 0;
        }
        if (t instanceof Retiro r) {
            return r.getIdMonederoOrigen().equals(idMonedero) ? -(r.getMonto() + r.getComision()) : 0;
        }
        if (t instanceof Transferencia tr) {
            double v = 0;
            if (tr.getIdMonederoOrigen().equals(idMonedero)) v -= tr.getMonto() + tr.getComision();
            if (tr.getIdMonederoDestino().equals(idMonedero)) v += tr.getMonto();
            return v;
        }
        return 0;
    }

    /**
     * Puntos que esta transacción dio a la dueña de 'idCuenta' (las recibidas,
     * las revertidas y los bonos no cuentan).
     */
    public static double puntosParaCuenta(Transaccion t, String idCuenta) {
        if (t.estaRevertida()) return 0;
        if (t instanceof Deposito d) return d.getIdCuentaDestino().equals(idCuenta) ? t.getPuntosGenerados() : 0;
        if (t instanceof Retiro r) return r.getIdCuentaOrigen().equals(idCuenta) ? t.getPuntosGenerados() : 0;
        if (t instanceof Transferencia tr) {
            return tr.getIdCuentaOrigen().equals(idCuenta) ? t.getPuntosGenerados() : 0;
        }
        return 0;
    }
}
