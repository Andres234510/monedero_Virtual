package main.java.co.edu.uniquindio.monedero.excepciones;

public class TransaccionInvalidaException extends RuntimeException {
    public TransaccionInvalidaException(String mensaje) { super(mensaje); }
}
