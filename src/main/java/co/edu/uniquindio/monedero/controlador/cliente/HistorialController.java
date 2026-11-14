package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.modelo.transaccion.TipoTransaccion;
import co.edu.uniquindio.monedero.modelo.transaccion.Transaccion;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
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

    public static class Fila {
        final SimpleStringProperty fecha, tipo;
        final SimpleDoubleProperty monto;
        final SimpleLongProperty puntos;

        Fila(String fecha, String tipo, double monto, long puntos) {
            this.fecha = new SimpleStringProperty(fecha);
            this.tipo = new SimpleStringProperty(tipo);
            this.monto = new SimpleDoubleProperty(monto);
            this.puntos = new SimpleLongProperty(puntos);
        }
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

        ObservableList<Fila> datos = FXCollections.observableArrayList();
        for (Transaccion t : Sesion.cliente().getCuenta().getHistorial()) {
            String tipo = t.getTipo().toString() + (t.estaRevertida() ? " (revertida)" : "");
            datos.add(0, new Fila(t.getFecha().toString().replace('T', ' ').substring(0, 16),
                    tipo, t.getMonto(), t.getPuntosGenerados()));
        }
        filtrada = new FilteredList<>(datos, f -> true);
        tabla.setItems(filtrada);

        cmbFiltro.getItems().add("Todos");
        for (TipoTransaccion tt : TipoTransaccion.values()) cmbFiltro.getItems().add(tt.toString());
        cmbFiltro.setValue("Todos");
        cmbFiltro.valueProperty().addListener((o, a, n) -> {
            filtrada.setPredicate(f -> "Todos".equals(n) || f.tipo.get().startsWith(n));
            lblTotal.setText(filtrada.size() + " transacciones");
        });
        lblTotal.setText(filtrada.size() + " transacciones");
    }
}