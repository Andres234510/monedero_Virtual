package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.modelo.CategoriaGasto;
import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.util.Sesion;
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
        lblPuntos.setText("Puntos base: " + ((int) (monto / 100) * factor) + " (más bono por rango)");
    }

    @FXML
    private void ejecutar() {
        double monto = leerMonto();
        String tipo = cmbTipo.getValue();
        if (tipo == null || monto <= 0) {
            lblMensaje.setText("Elige tipo y un monto válido");
            return;
        }
        Cliente c = Sesion.cliente();
        CategoriaGasto cat = CategoriaGasto.OTROS;
        try {
            switch (tipo) {
                case "Depósito" -> Sesion.servicios().transacciones()
                        .depositar(c.getId(), null, monto, "Depósito");
                case "Retiro" -> Sesion.servicios().transacciones()
                        .retirar(c.getId(), null, monto, cat, "Retiro");
                default -> {
                    String destino = txtDestino.getText().trim();
                    if (destino.isEmpty()) {
                        lblMensaje.setText("Falta el documento del destinatario");
                        return;
                    }
                    Sesion.servicios().transacciones()
                            .transferir(c.getId(), null, destino, null, monto, cat, "Transferencia");
                }
            }
            lblMensaje.setText(tipo + " realizada. Saldo: "
                    + String.format("$ %,.2f", c.getCuenta().saldoTotal()));
            txtMonto.clear();
            txtDestino.clear();
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
        }
    }
}