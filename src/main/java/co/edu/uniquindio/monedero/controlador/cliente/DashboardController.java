package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private BorderPane raiz;
    @FXML private Label lblBienvenida, lblSaldo, lblPuntos, lblRango, lblEstado;
    private Node inicio;

    @FXML
    private void initialize() {
        inicio = raiz.getCenter();
        lblBienvenida.setText("Hola, Cliente Demo");
        lblSaldo.setText("$ 1.250.000");
        lblPuntos.setText("320");
        lblRango.setText(calcularRango(320));
    }

    private String calcularRango(int puntos) {
        if (puntos <= 500) return "BRONCE";
        if (puntos <= 1000) return "PLATA";
        if (puntos <= 5000) return "ORO";
        return "PLATINO";
    }

    @FXML
    private void abrir(ActionEvent e) throws Exception {
        String nombre = ((Button) e.getSource()).getText();
        String archivo = switch (nombre) {
            case "Transacciones" -> "transaccion";
            case "Historial" -> "historial";
            case "Puntos y canje" -> "puntos";
            case "Programadas" -> "programadas";
            case "Notificaciones" -> "notificaciones";
            case "Monederos" -> "monederos";
            case "Reportes" -> "reportes";
            default -> "";
        };
        if (archivo.isEmpty()) {
            raiz.setCenter(inicio);
            return;
        }
        var url = getClass().getResource("/vistas/cliente/" + archivo + ".fxml");
        if (url == null) {
            lblEstado.setText("Pantalla '" + nombre + "' pendiente");
            raiz.setCenter(inicio);
            return;
        }
        raiz.setCenter(FXMLLoader.load(url));
    }

    @FXML
    private void cerrarSesion(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/vistas/login.fxml"));
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/estilos/estilos.css").toExternalForm());
        Stage stage = (Stage) raiz.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}