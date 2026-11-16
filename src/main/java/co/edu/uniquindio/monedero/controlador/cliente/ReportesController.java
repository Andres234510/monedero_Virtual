package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.algoritmos.Ordenamiento;
import co.edu.uniquindio.monedero.algoritmos.VerificadorRecursivo;
import co.edu.uniquindio.monedero.estructuras.grafos.Arista;
import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import co.edu.uniquindio.monedero.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.transaccion.Deposito;
import co.edu.uniquindio.monedero.modelo.transaccion.Retiro;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;
import co.edu.uniquindio.monedero.servicio.AnalisisGastoServicio;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Reportes con datos reales. Con un cliente en sesión muestra SUS datos; cuando se
 * abre desde el panel del administrador (sin cliente en sesión) suma a todos.
 */
public class ReportesController {

    public static class Fila {
        final SimpleStringProperty concepto, valor, detalle;

        Fila(String concepto, String valor, String detalle) {
            this.concepto = new SimpleStringProperty(concepto);
            this.valor = new SimpleStringProperty(valor);
            this.detalle = new SimpleStringProperty(detalle);
        }
    }

    @FXML private ComboBox<String> cmbReporte;
    @FXML private Label lblTitulo, lblMensaje;
    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colConcepto, colValor, colDetalle;
    @FXML private ListView<String> listaRelaciones;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbReporte.getItems().addAll(
                "Gasto por categoría",
                "Resumen de transacciones",
                "Transferencias entre clientes",
                "Puntos por tipo de transacción");
        cmbReporte.setValue("Gasto por categoría");

        colConcepto.setCellValueFactory(c -> c.getValue().concepto);
        colValor.setCellValueFactory(c -> c.getValue().valor);
        colDetalle.setCellValueFactory(c -> c.getValue().detalle);
        tabla.setItems(datos);
        generar();
    }

    @FXML
    private void generar() {
        String tipo = cmbReporte.getValue();
        if (tipo == null) {
            lblMensaje.setText("Elige un tipo de reporte");
            return;
        }
        lblMensaje.setText("");
        lblTitulo.setText(tipo + (esAdmin() ? " (todos los clientes)" : ""));
        datos.clear();
        listaRelaciones.getItems().clear();

        try {
            switch (tipo) {
                case "Gasto por categoría" -> gastoPorCategoria();
                case "Resumen de transacciones" -> resumenTransacciones();
                case "Transferencias entre clientes" -> transferenciasEntreClientes();
                default -> puntosPorTipo();
            }
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
        }
    }

    // ---------- Reportes ----------
    private void gastoPorCategoria() {
        double[] total = new double[CategoriaGasto.values().length];
        int[] cantidad = new int[total.length];
        double suma = 0;
        for (Cliente c : alcance()) {
            for (AnalisisGastoServicio.ResumenCategoria r : analisis().resumenPorCategoria(c.getId())) {
                total[r.categoria().ordinal()] += r.total();
                cantidad[r.categoria().ordinal()] += r.cantidad();
                suma += r.total();
            }
        }
        if (suma == 0) {
            lblMensaje.setText("Todavía no hay gastos registrados");
            return;
        }
        ListaEnlazada<AnalisisGastoServicio.ResumenCategoria> porCategoria = new ListaEnlazada<>();
        for (CategoriaGasto cat : CategoriaGasto.values()) {
            int i = cat.ordinal();
            if (cantidad[i] > 0) {
                porCategoria.agregarAlFinal(new AnalisisGastoServicio.ResumenCategoria(cat, total[i], cantidad[i]));
            }
        }
        // de mayor a menor gasto (ordenamiento propio, estable)
        for (AnalisisGastoServicio.ResumenCategoria r :
                Ordenamiento.ordenar(porCategoria, (a, b) -> Double.compare(b.total(), a.total()))) {
            datos.add(new Fila(r.categoria().toString(), dinero(r.total()),
                    String.format("%.1f%% (%d %s)", r.total() / suma * 100, r.cantidad(),
                            r.cantidad() == 1 ? "gasto" : "gastos")));
        }

        // Grafo dirigido de hábitos: qué categoría suele seguir a cuál
        if (esAdmin()) {
            listaRelaciones.getItems().add("Los patrones de gasto se consultan por cliente");
            return;
        }
        GrafoDirigido<CategoriaGasto> g = analisis().grafoCategorias(Sesion.cliente().getId());
        for (CategoriaGasto origen : g.vertices()) {
            for (Arista<CategoriaGasto> a : g.aristasSalientes(origen)) {
                listaRelaciones.getItems().add(origen + " → " + a.getDestino()
                        + " (frecuencia: " + (int) a.getPeso() + ")");
            }
        }
    }

    private void resumenTransacciones() {
        int dep = 0, ret = 0, env = 0, rec = 0, internas = 0, revertidas = 0;
        double mDep = 0, mRet = 0, mEnv = 0, mRec = 0, mInt = 0;
        for (Cliente c : alcance()) {
            String cuenta = c.getCuenta().getId();
            for (Transaccion t : c.getCuenta().getHistorial()) {
                boolean propia = !(t instanceof Transferencia)
                        || ((Transferencia) t).getIdCuentaOrigen().equals(cuenta);
                if (t.estaRevertida()) {
                    if (propia) revertidas++;           // una transferencia sale en 2 historiales: se cuenta 1 vez
                    continue;
                }
                if (t instanceof Deposito) {
                    dep++; mDep += t.getMonto();
                } else if (t instanceof Retiro) {
                    ret++; mRet += t.getMonto();
                } else if (t instanceof Transferencia) {
                    Transferencia tr = (Transferencia) t;
                    if (tr.esInterna()) { internas++; mInt += tr.getMonto(); }
                    else if (propia) { env++; mEnv += tr.getMonto(); }
                    else { rec++; mRec += tr.getMonto(); }
                }
            }
        }
        datos.add(new Fila("Depósitos", String.valueOf(dep), dinero(mDep)));
        datos.add(new Fila("Retiros", String.valueOf(ret), dinero(mRet)));
        datos.add(new Fila("Transferencias enviadas", String.valueOf(env), dinero(mEnv)));
        datos.add(new Fila("Transferencias recibidas", String.valueOf(rec), dinero(mRec)));
        datos.add(new Fila("Entre mis monederos", String.valueOf(internas), dinero(mInt)));
        datos.add(new Fila("Revertidas", String.valueOf(revertidas), "no cuentan en los totales"));
    }

    private void transferenciasEntreClientes() {
        GrafoDirigido<String> g = analisis().grafoTransferencias();
        if (esAdmin()) {
            for (AnalisisGastoServicio.ClienteMonto cm : analisis().topEmisores(10)) {
                datos.add(new Fila(nombre(cm.clienteId()), dinero(cm.monto()), "enviado"));
            }
            for (AnalisisGastoServicio.ClienteMonto cm : analisis().topReceptores(10)) {
                datos.add(new Fila(nombre(cm.clienteId()), dinero(cm.monto()), "recibido"));
            }
            for (String origen : g.vertices()) {
                for (Arista<String> a : g.aristasSalientes(origen)) {
                    listaRelaciones.getItems().add(nombre(origen) + " → " + nombre(a.getDestino())
                            + ": " + dinero(a.getPeso()));
                }
            }
        } else {
            String yo = Sesion.cliente().getId();
            for (Arista<String> a : g.aristasSalientes(yo)) {
                datos.add(new Fila("A " + nombre(a.getDestino()) + " (" + a.getDestino() + ")",
                        dinero(a.getPeso()), "enviado"));
                listaRelaciones.getItems().add("Yo → " + nombre(a.getDestino()));
            }
            for (String otro : g.vertices()) {
                double peso = otro.equals(yo) ? 0 : g.pesoArista(otro, yo);
                if (peso > 0) {
                    datos.add(new Fila("De " + nombre(otro) + " (" + otro + ")", dinero(peso), "recibido"));
                    listaRelaciones.getItems().add(nombre(otro) + " → Yo");
                }
            }
        }
        if (datos.isEmpty()) lblMensaje.setText("Todavía no hay transferencias entre clientes");
    }

    private void puntosPorTipo() {
        long dep = 0, ret = 0, tra = 0;
        for (Cliente c : alcance()) {
            String cuenta = c.getCuenta().getId();
            for (Transaccion t : c.getCuenta().getHistorial()) {
                long puntos = (long) VerificadorRecursivo.puntosParaCuenta(t, cuenta);
                if (puntos <= 0) continue;
                if (t instanceof Deposito) dep += puntos;
                else if (t instanceof Retiro) ret += puntos;
                else tra += puntos;
            }
        }
        datos.add(new Fila("Depósitos", dep + " pts", "1 por cada 100 (+ bono por rango)"));
        datos.add(new Fila("Retiros", ret + " pts", "2 por cada 100 (+ bono por rango)"));
        datos.add(new Fila("Transferencias", tra + " pts", "3 por cada 100 (+ bono por rango)"));
        datos.add(new Fila("Total", (dep + ret + tra) + " pts", "sin contar transacciones revertidas"));
    }

    // ---------- Auxiliares ----------
    private static AnalisisGastoServicio analisis() { return Sesion.servicios().analisis(); }

    /** Cliente en sesión, o null si entró el administrador. */
    private static Cliente clienteActual() {
        try {
            return Sesion.cliente();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static boolean esAdmin() { return clienteActual() == null; }

    private static ListaEnlazada<Cliente> alcance() {
        ListaEnlazada<Cliente> lista = new ListaEnlazada<>();
        Cliente actual = clienteActual();
        if (actual != null) {
            lista.agregarAlFinal(actual);
        } else {
            for (Cliente c : Sesion.servicios().clientes().listar()) lista.agregarAlFinal(c);
        }
        return lista;
    }

    private static String nombre(String clienteId) {
        return Sesion.servicios().clientes().buscar(clienteId).getNombre();
    }

    private static String dinero(double v) { return String.format("$ %,.2f", v); }
}