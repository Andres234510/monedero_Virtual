package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MonederosController {

    // Clase temporal; luego se usa Monedero del modelo
    public static class Fila {
        final SimpleStringProperty nombre;
        final SimpleDoubleProperty saldo;

        Fila(String nombre, double saldo) {
            this.nombre = new SimpleStringProperty(nombre);
            this.saldo = new SimpleDoubleProperty(saldo);
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
        javafx.util.StringConverter<Fila> conv = new javafx.util.StringConverter<>() {
            @Override public String toString(Fila f) { return f == null ? "" : f.nombre.get(); }
            @Override public Fila fromString(String s) { return null; }
        };
        cmbOrigen.setConverter(conv);
        cmbDestino.setConverter(conv);

        // Datos falsos
        datos.add(new Fila("Gastos diarios", 400000));
        datos.add(new Fila("Ahorros", 850000));
    }

    @FXML
    private void crear() {
        String nombre = txtNuevo.getText().trim();
        if (nombre.isEmpty()) {
            lblMensaje.setText("Escribe un nombre");
            return;
        }
        for (Fila f : datos) {
            if (f.nombre.get().equalsIgnoreCase(nombre)) {
                lblMensaje.setText("Ya existe un monedero con ese nombre");
                return;
            }
        }
        datos.add(new Fila(nombre, 0));
        txtNuevo.clear();
        lblMensaje.setText("Monedero creado");
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
        if (o == d) {
            lblMensaje.setText("Origen y destino deben ser distintos");
            return;
        }
        if (o.saldo.get() < monto) {
            lblMensaje.setText("Saldo insuficiente en " + o.nombre.get());
            return;
        }
        // Luego: MultimonederoServicio + GrafoDirigido (arista origen -> destino)
        o.saldo.set(o.saldo.get() - monto);
        d.saldo.set(d.saldo.get() + monto);
        listaMovimientos.getItems().add(0,
                o.nombre.get() + " → " + d.nombre.get() + ": $" + monto);
        lblMensaje.setText("Movimiento realizado");
        txtMonto.clear();
    }
}