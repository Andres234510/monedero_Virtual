package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.modelo.puntos.Beneficio;
import co.edu.uniquindio.monedero.modelo.puntos.MovimientoPuntos;
import co.edu.uniquindio.monedero.modelo.puntos.RegistroPuntos;
import co.edu.uniquindio.monedero.util.Sesion;
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

    @FXML
    private void initialize() {
        refrescar();
    }

    private void refrescar() {
        Cliente c = Sesion.cliente();
        RegistroPuntos r = c.getRegistroPuntos();

        lblPuntos.setText(String.valueOf(r.getDisponibles()));
        lblRango.setText(c.getRango().toString());

        long faltan = r.puntosParaSiguienteRango();
        if (faltan == 0) {
            lblSiguiente.setText("Rango máximo alcanzado");
            barra.setProgress(1);
        } else {
            lblSiguiente.setText("Te faltan " + faltan + " puntos para el siguiente rango");
            barra.setProgress((double) r.getAcumulados() / (r.getAcumulados() + faltan));
        }

        listaMovimientos.getItems().clear();
        for (MovimientoPuntos m : r.getHistorial()) {
            listaMovimientos.getItems().add(0, m.getPuntos() + "  " + m.getTipo());
        }

        btn100.setDisable(r.getDisponibles() < 100);
        btn500.setDisable(r.getDisponibles() < 500);
        btn1000.setDisable(r.getDisponibles() < 1000);
    }

    @FXML
    private void canjear(ActionEvent e) {
        Button b = (Button) e.getSource();
        Beneficio beneficio = b == btn100 ? Beneficio.DESCUENTO_COMISION_TRANSFERENCIA
                : b == btn500 ? Beneficio.MES_SIN_CARGOS_RETIRO
                : Beneficio.BONO_SALDO;
        try {
            Sesion.servicios().puntos().canjear(Sesion.cliente().getId(), beneficio, null);
            lblMensaje.setText("Canjeaste: " + beneficio.getDescripcion());
        } catch (Exception ex) {
            lblMensaje.setText(ex.getMessage());
        }
        refrescar();
    }
}