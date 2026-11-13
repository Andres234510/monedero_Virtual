package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TransaccionController {
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMonto, txtDestino;
    @FXML private Label lblPuntos, lblMensaje;

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll("Depósito", "Retiro", "Transferencia");
        cmbTipo.valueProperty().addListener((o, a, n) -> {
            txtDestino.setDisable(!"Transferencia".equals(n));
            actualizarPuntos();
        });
        txtMonto.textProperty().addListener((o, a, n) -> actualizarPuntos());
    }

    private double leerMonto() {
        try {
            return Double.parseDouble(txtMonto.getText().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void actualizarPuntos() {
        double monto = leerMonto();
        String tipo = cmbTipo.getValue();
        if (monto <= 0 || tipo == null) {
            lblPuntos.setText("");
            return;
        }
        int factor = switch (tipo) {
            case "Depósito" -> 1;
            case "Retiro" -> 2;
            default -> 3;
        };
        lblPuntos.setText("Ganarás " + ((int) (monto / 100) * factor) + " puntos");
    }

    @FXML
    private void ejecutar() {
        double monto = leerMonto();
        if (cmbTipo.getValue() == null || monto <= 0) {
            lblMensaje.setText("Elige tipo y un monto válido");
            return;
        }
        if ("Transferencia".equals(cmbTipo.getValue()) && txtDestino.getText().isBlank()) {
            lblMensaje.setText("Falta la cuenta destino");
            return;
        }
        // Aquí irá TransaccionServicio
        lblMensaje.setText(cmbTipo.getValue() + " de $" + monto + " registrada (simulado)");
        txtMonto.clear();
        txtDestino.clear();
    }
}
