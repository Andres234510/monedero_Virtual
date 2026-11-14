package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.time.Clock;

/**
 * Punto único de acceso a la lógica: arma los servicios y sus dependencias.
 * La interfaz gráfica crea una sola instancia y la comparte entre controladores.
 */
public class Servicios {
    private final ClienteRepositorio repositorio = new ClienteRepositorio();
    private final NotificacionServicio notificaciones = new NotificacionServicio();
    private final RangoServicio rangos = new RangoServicio(repositorio, notificaciones);
    private final PuntosServicio puntos;
    private final TransaccionServicio transacciones;
    private final ClienteServicio clientes = new ClienteServicio(repositorio, rangos);
    private final CuentaServicio cuentas = new CuentaServicio(repositorio);
    private final MultimonederoServicio multimonedero;
    private final ProgramadorServicio programador;
    private final AnalisisGastoServicio analisis = new AnalisisGastoServicio(repositorio);
    private final IntegridadServicio integridad = new IntegridadServicio(repositorio);
    private final ReporteServicio reportes;

    public Servicios() { this(Clock.systemDefaultZone()); }

    /** El reloj se inyecta para poder probar vencimientos de beneficios sin esperar días reales. */
    public Servicios(Clock reloj) {
        this.puntos = new PuntosServicio(repositorio, rangos, notificaciones, reloj);
        this.transacciones = new TransaccionServicio(repositorio, puntos, notificaciones, reloj);
        this.multimonedero = new MultimonederoServicio(repositorio, transacciones);
        this.programador = new ProgramadorServicio(repositorio, transacciones, notificaciones);
        this.reportes = new ReporteServicio(repositorio, rangos, analisis);
    }

    public ClienteRepositorio repositorio() { return repositorio; }
    public NotificacionServicio notificaciones() { return notificaciones; }
    public RangoServicio rangos() { return rangos; }
    public PuntosServicio puntos() { return puntos; }
    public TransaccionServicio transacciones() { return transacciones; }
    public ClienteServicio clientes() { return clientes; }
    public CuentaServicio cuentas() { return cuentas; }
    public MultimonederoServicio multimonedero() { return multimonedero; }
    public ProgramadorServicio programador() { return programador; }
    public AnalisisGastoServicio analisis() { return analisis; }
    public IntegridadServicio integridad() { return integridad; }
    public ReporteServicio reportes() { return reportes; }
}
