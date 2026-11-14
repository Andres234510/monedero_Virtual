package co.edu.uniquindio.monedero.excepciones;

/** No existe el cliente, la cuenta o el monedero buscado. */
public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(String mensaje) { super(mensaje); }
}
