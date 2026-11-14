package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.modelo.transaccion.Frecuencia;
import co.edu.uniquindio.monedero.modelo.transaccion.TransaccionProgramada;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ProgramadasController {

    public static class Fila {
        final String id;
        final LocalDate fecha;
        final SimpleStringProperty fechaTxt, tipo, frecuencia;
        final SimpleDoubleProperty monto;

        Fila(TransaccionProgramada p) {
            this.id = p.getId();
            this.fecha = p.getProximaEjecucion();
            this.fechaTxt = new SimpleStringProperty(fecha.toString());
            this.tipo = new SimpleStringProperty(p.getTipo().toString());
            this.monto = new SimpleDoubleProperty(p.getMonto());
            this.frecuencia = new SimpleStringProperty(p.getFrecuencia().toString());
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
        for (Frecuencia f : Frecuencia.values()) cmbFrecuencia.getItems().add(f.name());
        cmbFrecuencia.getSelectionModel().selectFirst();
        cmbTipo.valueProperty().addListener((o, a, n) ->
                txtDestino.setDisable(!"Transferencia".equals(n)));

        colFecha.setCellValueFactory(c -> c.getValue().fechaTxt);
        colTipo.setCellValueFactory(c -> c.getValue().tipo);
        colMonto.setCellValueFactory(c -> c.getValue().monto);
        colFrecuencia.setCellValueFactory(c -> c.getValue().frecuencia);
        tabla.setItems(datos);
        recargar();
    }

    private void recargar() {
        datos.clear();
        for (TransaccionProgramada p : Sesion.servicios().programador().listarActivas(Sesion.cliente().getId())) {
            datos.add(new Fila(p));
        }
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
        String id = Sesion.cliente().getId();
        Frecuencia fr = Frecuencia.valueOf(cmbFrecuencia.getValue());
        try {
            if ("Transferencia".equals(cmbTipo.getValue())) {
                String destino = txtDestino.getText().trim();
                if (destino.isEmpty()) {
                    lblMensaje.setText("Falta el documento del destinatario");
                    return;
                }
                Sesion.servicios().programador()
                        .programarTransferencia(id, null, destino, null, monto, fr, dpFecha.getValue());
            } else {
                Sesion.servicios().programador()
                        .programarDeposito(id, null, monto, fr, dpFecha.getValue());
            }
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
            return;
        }
        recargar();
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
        try {
            Sesion.servicios().programador().cancelar(f.id);
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
            return;
        }
        recargar();
        lblMensaje.setText("Programación cancelada");
    }
}