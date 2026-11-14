package co.edu.uniquindio.monedero.controlador.cliente;

import co.edu.uniquindio.monedero.modelo.Notificacion;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesController {
    @FXML private Label lblActual, lblPosicion;
    @FXML private ListView<String> lista;

    private final List<Notificacion> notificaciones = new ArrayList<>();
    private int actual = 0;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    private void initialize() {
        // La más reciente primero
        for (Notificacion n : Sesion.cliente().getNotificaciones()) {
            notificaciones.add(0, n);
        }
        mostrar();
    }

    private String texto(Notificacion n) {
        return (n.isLeida() ? "✔ " : "● ") + n.getFecha().format(FORMATO) + "  " + n;
    }

    private void mostrar() {
        lista.getItems().clear();
        for (Notificacion n : notificaciones) lista.getItems().add(texto(n));
        if (notificaciones.isEmpty()) {
            lblActual.setText("Sin notificaciones");
            lblPosicion.setText("");
            return;
        }
        lblActual.setText(notificaciones.get(actual).toString());
        lblPosicion.setText((actual + 1) + " de " + notificaciones.size());
    }

    @FXML
    private void siguiente() {
        if (notificaciones.isEmpty()) return;
        actual = (actual + 1) % notificaciones.size();
        mostrar();
    }

    @FXML
    private void anterior() {
        if (notificaciones.isEmpty()) return;
        actual = (actual - 1 + notificaciones.size()) % notificaciones.size();
        mostrar();
    }

    @FXML
    private void marcarLeida() {
        if (notificaciones.isEmpty()) return;
        notificaciones.get(actual).marcarLeida();
        mostrar();
    }

    @FXML
    private void simularSaldoBajo() {
        // Ya no aplica: las alertas las genera NotificacionServicio
    }
}