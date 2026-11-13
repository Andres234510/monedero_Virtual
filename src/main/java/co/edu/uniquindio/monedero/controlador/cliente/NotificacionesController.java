package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesController {
    @FXML private Label lblActual, lblPosicion;
    @FXML private ListView<String> lista;

    // Luego: ListaCircular del equipo
    private final List<String> notificaciones = new ArrayList<>();
    private final List<Boolean> leidas = new ArrayList<>();
    private int actual = 0;

    @FXML
    private void initialize() {
        agregar("Recordatorio: depósito programado en 3 días");
        agregar("Alerta: tu saldo está por debajo de $100.000");
        agregar("Subiste de rango: ahora eres PLATA");
        mostrar();
    }

    private void agregar(String texto) {
        notificaciones.add(texto);
        leidas.add(false);
    }

    private void mostrar() {
        lista.getItems().clear();
        for (int i = 0; i < notificaciones.size(); i++) {
            lista.getItems().add((leidas.get(i) ? "✔ " : "● ") + notificaciones.get(i));
        }
        if (notificaciones.isEmpty()) {
            lblActual.setText("Sin notificaciones");
            lblPosicion.setText("");
            return;
        }
        lblActual.setText(notificaciones.get(actual));
        lblPosicion.setText((actual + 1) + " de " + notificaciones.size());
    }

    // Navegación circular: del último vuelve al primero
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
        leidas.set(actual, true);
        mostrar();
    }

    @FXML
    private void simularSaldoBajo() {
        agregar("Alerta: saldo bajo detectado");
        actual = notificaciones.size() - 1;
        mostrar();
    }
}