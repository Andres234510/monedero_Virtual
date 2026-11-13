package main.java.co.edu.uniquindio.monedero.excepciones;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) { super(mensaje); }
}
