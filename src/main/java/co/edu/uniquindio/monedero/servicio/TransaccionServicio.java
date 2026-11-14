package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Cuenta;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.TipoNotificacion;
import co.edu.uniquindio.monedero.modelo.puntos.Beneficio;
import co.edu.uniquindio.monedero.modelo.puntos.BeneficioActivo;
import co.edu.uniquindio.monedero.modelo.transaccion.Deposito;
import co.edu.uniquindio.monedero.modelo.transaccion.Retiro;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;
import co.edu.uniquindio.monedero.util.Constantes;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Núcleo del sistema: depósitos, retiros, transferencias y reversión.
 *
 * Reglas:
 *  - Cada operación valida TODO antes de mover dinero, así un error no deja
 *    saldos a medias.
 *  - Retiro: comisión base 1% (0 con el beneficio "mes sin cargos").
 *  - Transferencia a otro cliente: comisión base 2%, menos el descuento del
 *    rango y el del beneficio canjeado (se suman; el beneficio se consume).
 *  - Transferencia entre monederos propios: sin comisión y sin puntos.
 *  - Los puntos son para quien envía/deposita/retira, nunca para quien recibe.
 *  - revertirUltima() deshace la transacción en la cima de la pila.
 */
public class TransaccionServicio {
    private final ClienteRepositorio repo;
    private final PuntosServicio puntos;
    private final NotificacionServicio notificaciones;
    private final Clock reloj;

    public TransaccionServicio(ClienteRepositorio repo, PuntosServicio puntos,
                               NotificacionServicio notificaciones, Clock reloj) {
        this.repo = repo;
        this.puntos = puntos;
        this.notificaciones = notificaciones;
        this.reloj = reloj;
    }

    // ---------- Operaciones ----------
    public Deposito depositar(String clienteId, String idMonedero, double monto, String descripcion) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        Monedero m = cuenta.monederoPrincipal();

        Deposito d = new Deposito(cuenta.getId(), m.getId(), monto, descripcion); // valida el monto
        m.depositar(monto);
        cuenta.registrarTransaccion(d);
        puntos.acreditar(c, d);
        return d;
    }

    public Retiro retirar(String clienteId, String idMonedero, double monto,
                          CategoriaGasto categoria, String descripcion) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        Monedero m = cuenta.monederoPrincipal();

        Retiro r = new Retiro(cuenta.getId(), m.getId(), monto, categoria, descripcion);
        boolean sinCargos = c.beneficioVigente(Beneficio.MES_SIN_CARGOS_RETIRO, hoy()) != null;
        double comision = sinCargos ? 0 : redondear(monto * Constantes.COMISION_RETIRO_PORCENTAJE / 100);
        validarSaldo(m, redondear(monto + comision));

        m.retirar(redondear(monto + comision));
        r.setComision(comision);
        cuenta.registrarTransaccion(r);
        puntos.acreditar(c, r);
        notificaciones.alertarSaldoBajo(c, m);
        return r;
    }

    public Transferencia transferir(String origenId, String idMonederoOrigen,
                                    String destinoId, String idMonederoDestino,
                                    double monto, CategoriaGasto categoria, String descripcion) {
        Cliente origen = repo.exigir(origenId);
        Cliente destino = repo.exigir(destinoId);
        Cuenta cuentaOrigen = origen.getCuenta();
        Cuenta cuentaDestino = destino.getCuenta();
        Monedero mo = cuentaOrigen.monederoPrincipal();
        Monedero md = cuentaDestino.monederoPrincipal();
        if (mo == md) throw new TransaccionInvalidaException("El origen y el destino son el mismo monedero");

        Transferencia t = new Transferencia(cuentaOrigen.getId(), mo.getId(), cuentaDestino.getId(),
                md.getId(), monto, categoria, descripcion);

        double comision = 0;
        BeneficioActivo descuento = null;
        if (!t.esInterna()) {
            descuento = origen.beneficioVigente(Beneficio.DESCUENTO_COMISION_TRANSFERENCIA, hoy());
            int porcentajeDescuento = origen.getRango().getDescuentoComisionPorcentaje()
                    + (descuento != null ? Beneficio.DESCUENTO_COMISION_TRANSFERENCIA.getValor() : 0);
            comision = comisionTransferencia(monto, porcentajeDescuento);
        }
        validarSaldo(mo, redondear(monto + comision));

        mo.retirar(redondear(monto + comision));
        md.depositar(monto);
        t.setComision(comision);
        if (descuento != null && comision > 0) descuento.consumir();

        cuentaOrigen.registrarTransaccion(t);
        if (!t.esInterna()) {
            cuentaDestino.registrarSoloHistorial(t);
            puntos.acreditar(origen, t);
            notificaciones.notificar(destino, TipoNotificacion.INFORMATIVA,
                    "Recibiste " + dinero(monto) + " de " + origen.getNombre());
        }
        notificaciones.alertarSaldoBajo(origen, mo);
        return t;
    }

    /**
     * Revierte la última transacción del cliente (cima de la pila). Si el dinero
     * ya no está disponible para deshacerla lanza SaldoInsuficienteException y
     * no cambia nada.
     */
    public Transaccion revertirUltima(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        if (cuenta.getReversibles().estaVacia()) {
            throw new TransaccionInvalidaException("No hay transacciones para revertir");
        }
        Transaccion t = cuenta.getReversibles().cima();
        aplicarReversion(c, t);                      // si falla, la pila queda intacta
        cuenta.getReversibles().desapilar();
        t.marcarRevertida();
        puntos.revertir(c, t);
        notificaciones.notificar(c, TipoNotificacion.INFORMATIVA, "Se revirtió la transacción " + t.getId());
        return t;
    }

    // ---------- Internos ----------
    private void aplicarReversion(Cliente c, Transaccion t) {
        Cuenta cuenta = c.getCuenta();
        if (t instanceof Deposito d) {
            Monedero m = cuenta.monederoPrincipal();
            validarSaldo(m, d.getMonto());
            m.retirar(d.getMonto());
        } else if (t instanceof Retiro r) {
            cuenta.monederoPrincipal().depositar(redondear(r.getMonto() + r.getComision()));
        } else if (t instanceof Transferencia tr) {
            Cliente receptor = repo.exigirPorCuenta(tr.getIdCuentaDestino());
            Monedero md = receptor.getCuenta().monederoPrincipal();
            Monedero mo = cuenta.monederoPrincipal();
            validarSaldo(md, tr.getMonto());
            md.retirar(tr.getMonto());
            mo.depositar(redondear(tr.getMonto() + tr.getComision()));
            if (!tr.esInterna()) {
                notificaciones.notificar(receptor, TipoNotificacion.INFORMATIVA,
                        "Se revirtió una transferencia de " + dinero(tr.getMonto()) + " de " + c.getNombre());
            }
        }
    }

    private static void validarSaldo(Monedero m, double necesario) {
        if (m.getSaldo() < necesario) {
            throw new SaldoInsuficienteException("Saldo insuficiente en '" + m.getNombre() + "': tiene "
                    + dinero(m.getSaldo()) + " y se necesitan " + dinero(necesario));
        }
    }

    private static double comisionTransferencia(double monto, int porcentajeDescuento) {
        int descuento = Math.min(100, porcentajeDescuento);
        return redondear(monto * Constantes.COMISION_TRANSFERENCIA_PORCENTAJE / 100 * (100 - descuento) / 100);
    }

    private LocalDate hoy() { return LocalDate.now(reloj); }

    private static double redondear(double v) { return Math.round(v * 100) / 100.0; }

    private static String dinero(double v) { return String.format(Locale.US, "%.2f", v); }
}
