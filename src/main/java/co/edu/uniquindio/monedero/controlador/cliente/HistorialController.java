package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistorialController {

    // Clase temporal; luego se usa la Transaccion del modelo
    public static class Fila {
        private final SimpleStringProperty fecha, tipo;
        private final SimpleDoubleProperty monto;
        private final SimpleIntegerProperty puntos;

        public Fila(String fecha, String tipo, double monto, int puntos) {
            this.fecha = new SimpleStringProperty(fecha);
            this.tipo = new SimpleStringProperty(tipo);
            this.monto = new SimpleDoubleProperty(monto);
            this.puntos = new SimpleIntegerProperty(puntos);
        }
        public String getTipo() { return tipo.get(); }
    }

    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colFecha, colTipo;
    @FXML private TableColumn<Fila, Number> colMonto, colPuntos;
    @FXML private ComboBox<String> cmbFiltro;
    @FXML private Label lblTotal;

    private FilteredList<Fila> filtrada;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(c -> c.getValue().fecha);
        colTipo.setCellValueFactory(c -> c.getValue().tipo);
        colMonto.setCellValueFactory(c -> c.getValue().monto);
        colPuntos.setCellValueFactory(c -> c.getValue().puntos);

        // Datos falsos
        ObservableList<Fila> datos = FXCollections.observableArrayList(
                new Fila("2026-11-01", "Depósito", 500000, 5),
                new Fila("2026-11-03", "Retiro", 120000, 24),
                new Fila("2026-11-07", "Transferencia", 300000, 9),
                new Fila("2026-11-10", "Depósito", 870000, 8));
        filtrada = new FilteredList<>(datos, f -> true);
        tabla.setItems(filtrada);

        cmbFiltro.getItems().addAll("Todos", "Depósito", "Retiro", "Transferencia");
        cmbFiltro.setValue("Todos");
        cmbFiltro.valueProperty().addListener((o, a, n) -> {
            filtrada.setPredicate(f -> "Todos".equals(n) || f.getTipo().equals(n));
            actualizarTotal();
        });
        actualizarTotal();
    }

    private void actualizarTotal() {
        lblTotal.setText(filtrada.size() + " transacciones");
    }
}