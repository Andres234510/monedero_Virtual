package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ProgramadasController {

    // Clase temporal; luego se usa TransaccionProgramada del modelo
    public static class Fila {
        final LocalDate fecha;
        final SimpleStringProperty fechaTxt, tipo, frecuencia;
        final SimpleDoubleProperty monto;

        Fila(LocalDate fecha, String tipo, double monto, String frecuencia) {
            this.fecha = fecha;
            this.fechaTxt = new SimpleStringProperty(fecha.toString());
            this.tipo = new SimpleStringProperty(tipo);
            this.monto = new SimpleDoubleProperty(monto);
            this.frecuencia = new SimpleStringProperty(frecuencia);
        }
    }

    @FXML private ComboBox<String> cmbTipo, cmbFrecuencia;
    @FXML private TextField txtMonto, txtDestino;
    @FXML private DatePicker dpFecha;
    @FXML private Label lblMensaje;
    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colFecha, colTipo, colFrecuencia;
    @FXML private TableColumn<Fila, Number> colMonto;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll("Depósito", "Transferencia");
        cmbFrecuencia.getItems().addAll("Una vez", "Semanal", "Mensual");
        cmbFrecuencia.setValue("Una vez");
        cmbTipo.valueProperty().addListener((o, a, n) ->
                txtDestino.setDisable(!"Transferencia".equals(n)));

        colFecha.setCellValueFactory(c -> c.getValue().fechaTxt);
        colTipo.setCellValueFactory(c -> c.getValue().tipo);
        colMonto.setCellValueFactory(c -> c.getValue().monto);
        colFrecuencia.setCellValueFactory(c -> c.getValue().frecuencia);
        tabla.setItems(datos);

        // Datos falsos
        datos.add(new Fila(LocalDate.now().plusDays(3), "Depósito", 200000, "Mensual"));
        datos.add(new Fila(LocalDate.now().plusDays(10), "Transferencia", 80000, "Semanal"));
        ordenar();
    }

    // Luego: ColaPrioridad del equipo
    private void ordenar() {
        datos.sort((a, b) -> a.fecha.compareTo(b.fecha));
    }

    @FXML
    private void programar() {
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim());
        } catch (NumberFormatException ex) {
            monto = -1;
        }
        if (cmbTipo.getValue() == null || monto <= 0 || dpFecha.getValue() == null) {
            lblMensaje.setText("Completa tipo, monto y fecha");
            return;
        }
        if (dpFecha.getValue().isBefore(LocalDate.now())) {
            lblMensaje.setText("La fecha no puede estar en el pasado");
            return;
        }
        if ("Transferencia".equals(cmbTipo.getValue()) && txtDestino.getText().isBlank()) {
            lblMensaje.setText("Falta la cuenta destino");
            return;
        }
        datos.add(new Fila(dpFecha.getValue(), cmbTipo.getValue(), monto, cmbFrecuencia.getValue()));
        ordenar();
        lblMensaje.setText("Transacción programada");
        txtMonto.clear();
        txtDestino.clear();
        dpFecha.setValue(null);
    }

    @FXML
    private void cancelar() {
        Fila f = tabla.getSelectionModel().getSelectedItem();
        if (f == null) {
            lblMensaje.setText("Selecciona una fila");
            return;
        }
        datos.remove(f);
        lblMensaje.setText("Programación cancelada");
    }
}