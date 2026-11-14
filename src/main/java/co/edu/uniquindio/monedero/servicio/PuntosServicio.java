package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.algoritmos.CalculadoraPuntos;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Cuenta;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.TipoNotificacion;
import co.edu.uniquindio.monedero.modelo.puntos.Beneficio;
import co.edu.uniquindio.monedero.modelo.puntos.BeneficioActivo;
import co.edu.uniquindio.monedero.modelo.puntos.RegistroPuntos;
import co.edu.uniquindio.monedero.modelo.transaccion.Deposito;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.time.Clock;
import java.time.LocalDate;

public class PuntosServicio {
    private final ClienteRepositorio repo;
    private final RangoServicio rangos;
    private final NotificacionServicio notificaciones;
    private final Clock reloj;

    public PuntosServicio(ClienteRepositorio repo, RangoServicio rangos,
                          NotificacionServicio notificaciones, Clock reloj) {
        this.repo = repo;
        this.rangos = rangos;
        this.notificaciones = notificaciones;
        this.reloj = reloj;
    }

    /** Da los puntos de la transacción (con el bono del rango que el cliente tiene en ese momento). */
    public CalculadoraPuntos.Resultado acreditar(Cliente c, Transaccion t) {
        RegistroPuntos registro = c.getRegistroPuntos();
        long antes = registro.getAcumulados();
        CalculadoraPuntos.Resultado r = CalculadoraPuntos.calcular(t.getTipo(), t.getMonto(), c.getRango());
        registro.acreditar(t.getId(), r.total(), t.getTipo() + " de " + t.getMonto());
        t.setPuntosGenerados(r.total());
        rangos.actualizar(c, antes, registro.getAcumulados());
        if (r.total() > 0) {
            notificaciones.notificar(c, TipoNotificacion.PUNTOS, "Ganaste " + r.total() + " puntos ("
                    + r.base() + " base + " + r.bono() + " de bono por rango " + c.getRango() + ")");
        }
        return r;
    }

    /** Quita los puntos de una transacción revertida y recalcula rango y totales. */
    public long revertir(Cliente c, Transaccion t) {
        RegistroPuntos registro = c.getRegistroPuntos();
        long antes = registro.getAcumulados();
        long revertidos = registro.revertir(t.getId());
        rangos.actualizar(c, antes, registro.getAcumulados());
        if (revertidos > 0) {
            notificaciones.notificar(c, TipoNotificacion.PUNTOS, "Se descontaron " + revertidos
                    + " puntos por revertir " + t.getId());
        }
        return revertidos;
    }

    /**
     * Canjea puntos por un beneficio. Con BONO_SALDO acredita 50 unidades en el
     * monedero indicado (o el principal) y lo deja en el historial como depósito.
     */
    public BeneficioActivo canjear(String clienteId, Beneficio beneficio, String idMonedero) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        Monedero destino = cuenta.monederoPrincipal(); // se valida antes de gastar puntos

        c.getRegistroPuntos().canjear(beneficio);                 // lanza si no alcanzan
        LocalDate hoy = LocalDate.now(reloj);
        BeneficioActivo activo = BeneficioActivo.activar(beneficio, hoy);
        c.getBeneficiosActivos().agregarAlFinal(activo);

        if (beneficio == Beneficio.BONO_SALDO) {
            Deposito bono = new Deposito(cuenta.getId(), destino.getId(), beneficio.getValor(),
                    "Bono por canje de puntos");
            destino.depositar(bono.getMonto());
            cuenta.registrarSoloHistorial(bono);
        }
        notificaciones.notificar(c, TipoNotificacion.PUNTOS, "Canjeaste: " + beneficio.getDescripcion());
        return activo;
    }
}
