package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.algoritmos.VerificadorRecursivo;
import co.edu.uniquindio.monedero.estructuras.hash.TablaHash;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Cuenta;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.util.Locale;

/**
 * Auditoría: reconstruye saldos y puntos desde el historial (con recursión
 * divide y vencerás) y los compara con lo que el sistema tiene guardado.
 */
public class IntegridadServicio {
    private static final double TOLERANCIA = 0.005;

    public record Informe(String clienteId, boolean valido, ListaEnlazada<String> problemas) { }

    private final ClienteRepositorio repo;

    public IntegridadServicio(ClienteRepositorio repo) { this.repo = repo; }

    public Informe verificar(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        Cuenta cuenta = c.getCuenta();
        ListaEnlazada<String> problemas = new ListaEnlazada<>();

        Transaccion[] txs = aArreglo(cuenta.getHistorial());

        // 1. Cada transacción por separado: monto, comisión e ids únicos
        TablaHash<String, Boolean> vistos = new TablaHash<>();
        for (Transaccion t : txs) {
            if (!(t.getMonto() > 0)) problemas.agregarAlFinal(t.getId() + ": monto no positivo");
            if (t.getComision() < 0) problemas.agregarAlFinal(t.getId() + ": comisión negativa");
            if (vistos.contiene(t.getId())) problemas.agregarAlFinal(t.getId() + ": id repetido");
            vistos.poner(t.getId(), true);
        }

        // 2. El saldo de cada monedero debe ser la suma de lo que movió su historial
        for (Monedero m : cuenta.getMonederos()) {
            String idMonedero = m.getId();
            double esperado = VerificadorRecursivo.sumar(txs, 0, txs.length,
                    t -> VerificadorRecursivo.aporte(t, idMonedero));
            if (Math.abs(esperado - m.getSaldo()) > TOLERANCIA) {
                problemas.agregarAlFinal("Monedero '" + m.getNombre() + "': saldo real "
                        + fmt(m.getSaldo()) + " pero el historial indica " + fmt(esperado));
            }
        }

        // 3. Los puntos acumulados deben ser los de las transacciones vigentes
        String idCuenta = cuenta.getId();
        double puntosEsperados = VerificadorRecursivo.sumar(txs, 0, txs.length,
                t -> VerificadorRecursivo.puntosParaCuenta(t, idCuenta));
        long acumulados = c.getRegistroPuntos().getAcumulados();
        if (Math.abs(puntosEsperados - acumulados) > TOLERANCIA) {
            problemas.agregarAlFinal("Puntos acumulados " + acumulados
                    + " pero las transacciones vigentes suman " + (long) puntosEsperados);
        }
        return new Informe(clienteId, problemas.estaVacia(), problemas);
    }

    public ListaEnlazada<Informe> verificarTodos() {
        ListaEnlazada<Informe> informes = new ListaEnlazada<>();
        for (Cliente c : repo.todos()) informes.agregarAlFinal(verificar(c.getId()));
        return informes;
    }

    private static Transaccion[] aArreglo(ListaEnlazada<Transaccion> lista) {
        Transaccion[] a = new Transaccion[lista.tamanio()];
        int i = 0;
        for (Transaccion t : lista) a[i++] = t;
        return a;
    }

    private static String fmt(double v) { return String.format(Locale.US, "%.2f", v); }
}
