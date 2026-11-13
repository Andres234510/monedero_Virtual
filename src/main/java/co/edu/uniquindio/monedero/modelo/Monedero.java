package main.java.co.edu.uniquindio.monedero.modelo;

import main.java.co.edu.uniquindio.monedero.excepciones.SaldoInsuficienteException;
import main.java.co.edu.uniquindio.monedero.excepciones.TransaccionInvalidaException;
import main.java.co.edu.uniquindio.monedero.util.Constantes;
import main.java.co.edu.uniquindio.monedero.util.GeneradorId;

/** Un monedero dentro de la cuenta del cliente (ahorros, gastos diarios...). */
public class Monedero {
    private final String id;
    private final String nombre;
    private double saldo;

    public Monedero(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new TransaccionInvalidaException("El monedero necesita un nombre");
        }
        this.id = GeneradorId.siguiente("MON");
        this.nombre = nombre.trim();
    }

    public void depositar(double monto) {
        validarMonto(monto);
        saldo += monto;
    }

    public void retirar(double monto) {
        validarMonto(monto);
        if (monto > saldo) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente en '" + nombre + "': tiene " + saldo + " y necesita " + monto);
        }
        saldo -= monto;
    }

    public boolean tieneSaldoBajo() { return saldo < Constantes.UMBRAL_SALDO_BAJO; }

    private static void validarMonto(double monto) {
        if (!(monto > 0) || Double.isInfinite(monto)) {
            throw new TransaccionInvalidaException("El monto debe ser un número positivo: " + monto);
        }
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getSaldo() { return saldo; }

    @Override
    public String toString() { return nombre + " (" + saldo + ")"; }
}
