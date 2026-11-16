package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.estructuras.grafos.Arista;
import co.edu.uniquindio.monedero.estructuras.grafos.GrafoDirigido;
import co.edu.uniquindio.monedero.modelo.Cuenta;
import co.edu.uniquindio.monedero.modelo.Monedero;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transferencia;
import co.edu.uniquindio.monedero.servicio.MultimonederoServicio;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class MonederosController {

    /** Fila de la tabla: envuelve un Monedero real del modelo. */
    public static class Fila {
        final String id;
        final SimpleStringProperty nombre;
        final SimpleDoubleProperty saldo;

        Fila(Monedero m) {
            this.id = m.getId();
            this.nombre = new SimpleStringProperty(m.getNombre());
            this.saldo = new SimpleDoubleProperty(m.getSaldo());
        }
    }

    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colNombre;
    @FXML private TableColumn<Fila, Number> colSaldo;
    @FXML private TextField txtNuevo, txtMonto;
    @FXML private ComboBox<Fila> cmbOrigen, cmbDestino;
    @FXML private Label lblMensaje;
    @FXML private ListView<String> listaMovimientos;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> c.getValue().nombre);
        colSaldo.setCellValueFactory(c -> c.getValue().saldo);
        tabla.setItems(datos);
        cmbOrigen.setItems(datos);
        cmbDestino.setItems(datos);

        // Muestra solo el nombre en los combos
        StringConverter<Fila> conv = new StringConverter<>() {
            @Override public String toString(Fila f) { return f == null ? "" : f.nombre.get(); }
            @Override public Fila fromString(String s) { return null; }
        };
        cmbOrigen.setConverter(conv);
        cmbDestino.setConverter(conv);

        recargar();
    }

    @FXML
    private void crear() {
        String nombre = txtNuevo.getText().trim();
        if (nombre.isEmpty()) {
            lblMensaje.setText("Escribe un nombre");
            return;
        }
        try {
            servicio().crearMonedero(clienteId(), nombre);   // valida nombres repetidos
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
            return;
        }
        txtNuevo.clear();
        lblMensaje.setText("Monedero creado");
        recargar();
    }

    @FXML
    private void mover() {
        Fila o = cmbOrigen.getValue();
        Fila d = cmbDestino.getValue();
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim());
        } catch (NumberFormatException ex) {
            monto = -1;
        }
        if (o == null || d == null || monto <= 0) {
            lblMensaje.setText("Elige origen, destino y un monto válido");
            return;
        }
        if (o.id.equals(d.id)) {
            lblMensaje.setText("Origen y destino deben ser distintos");
            return;
        }
        try {
            // Transferencia interna: sin comisión ni puntos; el saldo lo valida el servicio
            servicio().transferirEntreMonederos(clienteId(), o.id, d.id, monto, "Movimiento entre monederos");
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
            return;
        }
        lblMensaje.setText("Movimiento realizado (sin comisión)");
        txtMonto.clear();
        recargar();
    }

    // ---------- Internos ----------
    private void recargar() {
        String origenId = idDe(cmbOrigen.getValue());
        String destinoId = idDe(cmbDestino.getValue());

        datos.clear();
        for (Monedero m : servicio().monederos(clienteId())) datos.add(new Fila(m));

        cmbOrigen.setValue(buscar(origenId));
        cmbDestino.setValue(buscar(destinoId));
        cargarMovimientos();
    }

    /** Movimientos (lo más nuevo arriba) y, debajo, las relaciones del grafo dirigido de monederos. */
    private void cargarMovimientos() {
        listaMovimientos.getItems().clear();
        Cuenta cuenta = Sesion.cliente().getCuenta();
        for (Transaccion t : cuenta.getHistorial()) {
            if (t instanceof Transferencia && ((Transferencia) t).esInterna()) {
                Transferencia tr = (Transferencia) t;
                Monedero origen = cuenta.buscarMonedero(tr.getIdMonederoOrigen());
                Monedero destino = cuenta.buscarMonedero(tr.getIdMonederoDestino());
                listaMovimientos.getItems().add(0, tr.getFecha().toLocalDate() + "  "
                        + origen.getNombre() + " → " + destino.getNombre() + ": "
                        + String.format("$ %,.2f", tr.getMonto())
                        + (tr.estaRevertida() ? " (revertido)" : ""));
            }
        }

        listaMovimientos.getItems().add("— Relaciones entre monederos (grafo) —");
        GrafoDirigido<String> grafo = servicio().grafoMonederos(clienteId());
        boolean hay = false;
        for (String origen : grafo.vertices()) {
            for (Arista<String> a : grafo.aristasSalientes(origen)) {
                listaMovimientos.getItems().add(origen + " → " + a.getDestino()
                        + "  (total " + String.format("$ %,.2f", a.getPeso()) + ")");
                hay = true;
            }
        }
        if (!hay) listaMovimientos.getItems().add("Aún no hay movimientos entre monederos");
    }

    private Fila buscar(String id) {
        if (id == null) return null;
        for (Fila f : datos) {
            if (f.id.equals(id)) return f;
        }
        return null;
    }

    private static String idDe(Fila f) { return f == null ? null : f.id; }

    private static MultimonederoServicio servicio() { return Sesion.servicios().multimonedero(); }

    private static String clienteId() { return Sesion.cliente().getId(); }
}