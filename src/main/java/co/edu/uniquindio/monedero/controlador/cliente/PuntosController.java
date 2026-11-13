package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class PuntosController {
    @FXML private Label lblPuntos, lblRango, lblSiguiente, lblMensaje;
    @FXML private ProgressBar barra;
    @FXML private Button btn100, btn500, btn1000;
    @FXML private ListView<String> listaMovimientos;

    private int puntos = 1200; // dato falso

    @FXML
    private void initialize() {
        listaMovimientos.getItems().addAll(
                "+5   Depósito $500.000",
                "+24  Retiro $120.000",
                "+9   Transferencia $300.000");
        refrescar();
    }

    private void refrescar() {
        lblPuntos.setText(String.valueOf(puntos));
        lblRango.setText(rango(puntos));
        actualizarProgreso();
        btn100.setDisable(puntos < 100);
        btn500.setDisable(puntos < 500);
        btn1000.setDisable(puntos < 1000);
    }

    private String rango(int p) {
        if (p <= 500) return "BRONCE";
        if (p <= 1000) return "PLATA";
        if (p <= 5000) return "ORO";
        return "PLATINO";
    }

    private void actualizarProgreso() {
        int[] limites = {501, 1001, 5001};
        String[] nombres = {"PLATA", "ORO", "PLATINO"};
        for (int i = 0; i < limites.length; i++) {
            if (puntos < limites[i]) {
                lblSiguiente.setText("Te faltan " + (limites[i] - puntos) + " puntos para " + nombres[i]);
                barra.setProgress((double) puntos / limites[i]);
                return;
            }
        }
        lblSiguiente.setText("Rango máximo alcanzado");
        barra.setProgress(1);
    }

    @FXML
    private void canjear(ActionEvent e) {
        Button b = (Button) e.getSource();
        int costo = b == btn100 ? 100 : b == btn500 ? 500 : 1000;
        if (puntos < costo) {
            lblMensaje.setText("Puntos insuficientes");
            return;
        }
        puntos -= costo; // luego: PuntosServicio.canjear(...)
        listaMovimientos.getItems().add(0, "-" + costo + " Canje de beneficio");
        lblMensaje.setText("Canje de " + costo + " puntos realizado");
        refrescar();
    }
}