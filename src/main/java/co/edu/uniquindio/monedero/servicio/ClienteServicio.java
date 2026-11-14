package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.excepciones.AutenticacionException;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

public class ClienteServicio {
    private final ClienteRepositorio repo;
    private final RangoServicio rangos;

    public ClienteServicio(ClienteRepositorio repo, RangoServicio rangos) {
        this.repo = repo;
        this.rangos = rangos;
    }

    public Cliente registrar(String documento, String nombre, String pin) {
        if (documento == null || documento.isBlank()) throw new IllegalArgumentException("Falta el documento");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Falta el nombre");
        validarPin(pin);
        Cliente c = new Cliente(documento.trim(), nombre.trim(), pin);
        repo.agregar(c);          // lanza si el documento ya existe
        rangos.registrar(c);
        return c;
    }

    /** Mismo mensaje si el documento no existe o el PIN falla, para no revelar cuál fue. */
    public Cliente autenticar(String documento, String pin) {
        Cliente c = documento == null ? null : repo.buscar(documento);
        if (c == null || !c.verificarPin(pin)) throw new AutenticacionException("Documento o PIN incorrectos");
        return c;
    }

    public void cambiarPin(String documento, String pinActual, String pinNuevo) {
        Cliente c = autenticar(documento, pinActual);
        validarPin(pinNuevo);
        c.setPin(pinNuevo);
    }

    public Cliente buscar(String documento) { return repo.exigir(documento); }
    public ListaEnlazada<Cliente> listar() { return repo.todos(); }

    private static void validarPin(String pin) {
        if (pin == null || pin.length() != 4) throw new IllegalArgumentException("El PIN debe tener 4 dígitos");
        for (int i = 0; i < pin.length(); i++) {
            if (!Character.isDigit(pin.charAt(i))) throw new IllegalArgumentException("El PIN debe ser numérico");
        }
    }
}
