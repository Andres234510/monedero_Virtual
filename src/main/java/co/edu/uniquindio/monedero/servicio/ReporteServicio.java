package co.edu.uniquindio.monedero.servicio;

import co.edu.uniquindio.monedero.algoritmos.Ordenamiento;
import co.edu.uniquindio.monedero.estructuras.grafos.Arista;
import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.puntos.MovimientoPuntos;
import co.edu.uniquindio.monedero.modelo.puntos.Rango;
import co.edu.uniquindio.monedero.modelo.puntos.RegistroPuntos;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.repositorio.ClienteRepositorio;

import java.util.Locale;

/** Informes en texto, listos para mostrar en la interfaz o pegar en el informe final. */
public class ReporteServicio {
    private final ClienteRepositorio repo;
    private final RangoServicio rangos;
    private final AnalisisGastoServicio analisis;

    public ReporteServicio(ClienteRepositorio repo, RangoServicio rangos, AnalisisGastoServicio analisis) {
        this.repo = repo;
        this.rangos = rangos;
        this.analisis = analisis;
    }

    public String resumenCliente(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        RegistroPuntos r = c.getRegistroPuntos();
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(c.getNombre()).append(" (").append(c.getId()).append(")\n");
        sb.append("Saldo total: ").append(dinero(c.getCuenta().saldoTotal())).append('\n');
        for (Monedero m : c.getCuenta().getMonederos()) {
            sb.append("  - ").append(m.getNombre()).append(": ").append(dinero(m.getSaldo())).append('\n');
        }
        sb.append("Rango: ").append(r.getRango()).append(" | puntos acumulados: ").append(r.getAcumulados())
                .append(" | disponibles: ").append(r.getDisponibles()).append('\n');
        long falta = r.puntosParaSiguienteRango();
        if (falta > 0) {
            sb.append("Faltan ").append(falta).append(" puntos para ").append(r.getRango().siguiente()).append('\n');
        }
        ListaEnlazada<AnalisisGastoServicio.ResumenCategoria> cats = analisis.resumenPorCategoria(clienteId);
        if (!cats.estaVacia()) {
            sb.append("Mayor gasto: ").append(cats.primero().categoria()).append(" (")
                    .append(dinero(cats.primero().total())).append(")\n");
        }
        return sb.toString();
    }

    public String extracto(String clienteId) {
        Cliente c = repo.exigir(clienteId);
        StringBuilder sb = new StringBuilder("Extracto de " + c.getNombre() + "\n");
        for (Transaccion t : c.getCuenta().getHistorial()) {
            sb.append(t.getFecha().toLocalDate()).append(' ').append(t.getId()).append(' ')
                    .append(t.getTipo()).append(' ').append(dinero(t.getMonto()))
                    .append(" | comisión ").append(dinero(t.getComision()))
                    .append(" | puntos ").append(t.getPuntosGenerados())
                    .append(" | ").append(t.getEstado()).append('\n');
        }
        return sb.toString();
    }

    public String rankingClientes(int n) {
        StringBuilder sb = new StringBuilder("Ranking de clientes por puntos\n");
        int pos = 1;
        for (Cliente c : rangos.ranking(n)) {
            sb.append(pos++).append(". ").append(c.getNombre()).append(" - ").append(c.getRango())
                    .append(" - ").append(c.getRegistroPuntos().getAcumulados()).append(" pts\n");
        }
        return sb.toString();
    }

    public String informeCategorias(String clienteId) {
        StringBuilder sb = new StringBuilder("Gasto por categoría\n");
        for (AnalisisGastoServicio.ResumenCategoria r : analisis.resumenPorCategoria(clienteId)) {
            sb.append("  ").append(r.categoria()).append(": ").append(dinero(r.total()))
                    .append(" (").append(r.cantidad()).append(r.cantidad() == 1 ? " gasto)\n" : " gastos)\n");
        }
        return sb.toString();
    }

    public String informePatrones(String clienteId) {
        GrafoDirigido<CategoriaGasto> g = analisis.grafoCategorias(clienteId);
        StringBuilder sb = new StringBuilder("Patrones de gasto (qué categoría suele seguir a cuál)\n");
        for (CategoriaGasto origen : g.vertices()) {
            ListaEnlazada<Arista<CategoriaGasto>> salientes =
                    Ordenamiento.ordenar(g.aristasSalientes(origen), (a, b) -> Double.compare(b.getPeso(), a.getPeso()));
            for (Arista<CategoriaGasto> a : salientes) {
                sb.append("  Después de ").append(origen).append(" -> ").append(a.getDestino())
                        .append(" (").append((int) a.getPeso()).append(" veces)\n");
            }
        }
        return sb.toString();
    }

    public String informeTransferencias() {
        StringBuilder sb = new StringBuilder("Transferencias entre clientes\nQuienes más envían:\n");
        for (AnalisisGastoServicio.ClienteMonto cm : analisis.topEmisores(5)) {
            sb.append("  ").append(repo.exigir(cm.clienteId()).getNombre()).append(": ").append(dinero(cm.monto())).append('\n');
        }
        sb.append("Quienes más reciben:\n");
        for (AnalisisGastoServicio.ClienteMonto cm : analisis.topReceptores(5)) {
            sb.append("  ").append(repo.exigir(cm.clienteId()).getNombre()).append(": ").append(dinero(cm.monto())).append('\n');
        }
        return sb.toString();
    }

    /** Impacto del sistema de puntos: clientes por rango y puntos emitidos, revertidos y canjeados. */
    public String informeImpactoPuntos() {
        long ganados = 0;
        long revertidos = 0;
        long canjeados = 0;
        int canjes = 0;
        for (Cliente c : repo.todos()) {
            for (MovimientoPuntos m : c.getRegistroPuntos().getHistorial()) {
                switch (m.getTipo()) {
                    case GANADO -> ganados += m.getPuntos();
                    case REVERTIDO -> revertidos += -m.getPuntos();
                    case CANJEADO -> { canjeados += -m.getPuntos(); canjes++; }
                }
            }
        }
        StringBuilder sb = new StringBuilder("Impacto del sistema de puntos\nClientes por rango:\n");
        for (Rango r : Rango.values()) {
            sb.append("  ").append(r).append(": ").append(rangos.clientesDeRango(r).tamanio()).append('\n');
        }
        sb.append("Puntos emitidos: ").append(ganados).append('\n');
        sb.append("Puntos revertidos: ").append(revertidos).append('\n');
        sb.append("Puntos canjeados: ").append(canjeados).append(" en ").append(canjes).append(" canjes\n");
        return sb.toString();
    }

    private static String dinero(double v) { return String.format(Locale.US, "%,.2f", v); }
}
