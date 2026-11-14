package co.edu.uniquindio.monedero.modelo;

import java.time.LocalDateTime;

public class Notificacion {
    private final TipoNotificacion tipo;
    private final String mensaje;
    private final LocalDateTime fecha;
    private boolean leida;

    public Notificacion(TipoNotificacion tipo, String mensaje) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public TipoNotificacion getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isLeida() { return leida; }
    public void marcarLeida() { this.leida = true; }

    @Override
    public String toString() { return "[" + tipo + "] " + mensaje; }
}
