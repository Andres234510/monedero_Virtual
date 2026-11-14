package co.edu.uniquindio.monedero.modelo;

import co.edu.uniquindio.monedero.estructuras.listas.ListaCircular;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.puntos.Beneficio;
import co.edu.uniquindio.monedero.modelo.puntos.BeneficioActivo;
import co.edu.uniquindio.monedero.modelo.puntos.Rango;
import co.edu.uniquindio.monedero.modelo.puntos.RegistroPuntos;
import co.edu.uniquindio.monedero.util.Constantes;

import java.time.LocalDate;

public class Cliente {
    private final String id; // documento de identidad
    private String nombre;
    private String pin;      // simulación académica: NO es seguridad real
    private final Cuenta cuenta;
    private final RegistroPuntos registroPuntos;
    private final ListaCircular<Notificacion> notificaciones =
            new ListaCircular<>(Constantes.MAX_NOTIFICACIONES);
    private final ListaEnlazada<BeneficioActivo> beneficiosActivos = new ListaEnlazada<>();

    public Cliente(String id, String nombre, String pin) {
        this.id = id;
        this.nombre = nombre;
        this.pin = pin;
        this.cuenta = new Cuenta(id);
        this.registroPuntos = new RegistroPuntos(id);
    }

    public boolean verificarPin(String intento) { return pin != null && pin.equals(intento); }

    public Rango getRango() { return registroPuntos.getRango(); }

    public void notificar(TipoNotificacion tipo, String mensaje) {
        notificaciones.agregar(new Notificacion(tipo, mensaje));
    }

    /** Primer beneficio vigente de ese tipo, o null. */
    public BeneficioActivo beneficioVigente(Beneficio beneficio, LocalDate hoy) {
        for (BeneficioActivo b : beneficiosActivos) {
            if (b.getBeneficio() == beneficio && b.estaVigente(hoy)) return b;
        }
        return null;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPin(String pin) { this.pin = pin; }
    public Cuenta getCuenta() { return cuenta; }
    public RegistroPuntos getRegistroPuntos() { return registroPuntos; }
    public ListaCircular<Notificacion> getNotificaciones() { return notificaciones; }
    public ListaEnlazada<BeneficioActivo> getBeneficiosActivos() { return beneficiosActivos; }

    @Override
    public String toString() { return nombre + " (" + id + ")"; }
}
