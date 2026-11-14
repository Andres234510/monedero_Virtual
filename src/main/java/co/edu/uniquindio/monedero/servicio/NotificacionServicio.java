package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.Notificacion;
import co.edu.uniquindio.monedero.modelo.TipoNotificacion;

import java.util.Locale;

/** Alertas y notificaciones; se guardan en la lista circular del cliente (las más viejas se descartan). */
public class NotificacionServicio {

    public void notificar(Cliente cliente, TipoNotificacion tipo, String mensaje) {
        cliente.notificar(tipo, mensaje);
    }

    public void alertarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.tieneSaldoBajo()) {
            notificar(cliente, TipoNotificacion.SALDO_BAJO, "Saldo bajo en '" + monedero.getNombre()
                    + "': " + String.format(Locale.US, "%.2f", monedero.getSaldo()));
        }
    }

    public int sinLeer(Cliente cliente) {
        int n = 0;
        for (Notificacion x : cliente.getNotificaciones()) {
            if (!x.isLeida()) n++;
        }
        return n;
    }

    public void marcarTodasLeidas(Cliente cliente) {
        for (Notificacion x : cliente.getNotificaciones()) x.marcarLeida();
    }
}
