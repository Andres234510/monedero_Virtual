package main.java.co.edu.uniquindio.monedero.modelo.puntos;

import java.time.LocalDate;

/**
 * Beneficio ya canjeado por el cliente:
 *  - DESCUENTO_COMISION_TRANSFERENCIA: se consume en la próxima transferencia.
 *  - MES_SIN_CARGOS_RETIRO: vigente durante N días desde el canje.
 *  - BONO_SALDO: se aplica al canjear, por eso nace ya consumido.
 */
public class BeneficioActivo {
    private final Beneficio beneficio;
    private final LocalDate fechaActivacion;
    private final LocalDate fechaExpiracion; // null = sin vencimiento
    private boolean consumido;

    private BeneficioActivo(Beneficio beneficio, LocalDate activacion, LocalDate expiracion, boolean consumido) {
        this.beneficio = beneficio;
        this.fechaActivacion = activacion;
        this.fechaExpiracion = expiracion;
        this.consumido = consumido;
    }

    public static BeneficioActivo activar(Beneficio beneficio, LocalDate hoy) {
        return switch (beneficio) {
            case DESCUENTO_COMISION_TRANSFERENCIA -> new BeneficioActivo(beneficio, hoy, null, false);
            case MES_SIN_CARGOS_RETIRO ->
                    new BeneficioActivo(beneficio, hoy, hoy.plusDays(beneficio.getValor()), false);
            case BONO_SALDO -> new BeneficioActivo(beneficio, hoy, null, true);
        };
    }

    public boolean estaVigente(LocalDate hoy) {
        return !consumido && (fechaExpiracion == null || !hoy.isAfter(fechaExpiracion));
    }

    public void consumir() { this.consumido = true; }

    public Beneficio getBeneficio() { return beneficio; }
    public LocalDate getFechaActivacion() { return fechaActivacion; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public boolean isConsumido() { return consumido; }
}
