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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    @FXML private Label lblTotal, lblMensaje;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();
    private FilteredList<Fila> filtrada;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(c -> c.getValue().fecha);
        colTipo.setCellValueFactory(c -> c.getValue().tipo);
        colMonto.setCellValueFactory(c -> c.getValue().monto);
        colPuntos.setCellValueFactory(c -> c.getValue().puntos);

        filtrada = new FilteredList<>(datos, f -> true);
        tabla.setItems(filtrada);

        cmbFiltro.getItems().add("Todos");
        for (TipoTransaccion tt : TipoTransaccion.values()) cmbFiltro.getItems().add(tt.toString());
        cmbFiltro.setValue("Todos");
        cmbFiltro.valueProperty().addListener((o, a, n) -> {
            filtrada.setPredicate(f -> "Todos".equals(n) || f.tipo.get().startsWith(n));
            lblTotal.setText(filtrada.size() + " transacciones");
        });
        recargar();
    }

    private void recargar() {
        datos.clear();
        for (Transaccion t : Sesion.cliente().getCuenta().getHistorial()) {
            String tipo = t.getTipo().toString() + (t.estaRevertida() ? " (revertida)" : "");
            datos.add(0, new Fila(t.getFecha().toString().replace('T', ' ').substring(0, 16),
                    tipo, t.getMonto(), t.getPuntosGenerados()));
        }
        lblTotal.setText(filtrada.size() + " transacciones");
    }

    @FXML
    private void revertir() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que quieres revertir la última transacción?",
                ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText("Revertir última transacción");
        if (!confirmar("Revertir última transacción",
                "¿Seguro que quieres revertir la última transacción?")) return;

        try {
            Transaccion t = Sesion.servicios().transacciones().revertirUltima(Sesion.cliente().getId());
            lblMensaje.setText("Revertida: " + t.getTipo() + " de " + t.getMonto());
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
        }
        recargar();
    }

    @FXML
    private void revertirTodo() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "Se revertirán todas las transacciones, de la más reciente a la más antigua. ¿Continuar?",
                ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText("Revertir todo");
        if (!confirmar("Revertir todo",
                "Se revertirán todas las transacciones, de la más reciente a la más antigua. ¿Continuar?")) return;

        int cuenta = 0;
        try {
            while (true) {
                Sesion.servicios().transacciones().revertirUltima(Sesion.cliente().getId());
                cuenta++;
            }
        } catch (Exception ex) {
            lblMensaje.setText(cuenta + " revertidas. Se detuvo: " + ex.getMessage());
        }
        recargar();
    }
    private boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText(titulo);
        alerta.setGraphic(null);
        alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/estilos/estilos.css").toExternalForm());
        return alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}