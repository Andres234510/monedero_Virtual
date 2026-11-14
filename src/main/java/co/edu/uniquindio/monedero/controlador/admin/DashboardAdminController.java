package co.edu.uniquindio.monedero.controlador.admin;

import co.edu.uniquindio.monedero.util.Sesion;
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

public class DashboardAdminController {
    @FXML private BorderPane raiz;
    @FXML private Label lblClientes, lblPendientes;
    private Node inicio;

    @FXML
    private void initialize() {
        inicio = raiz.getCenter();
        refrescarInicio();
    }

    private void refrescarInicio() {
        lblClientes.setText(String.valueOf(Sesion.servicios().rangos().totalClientes()));
        lblPendientes.setText(String.valueOf(Sesion.servicios().programador().pendientesEnCola()));
    }

    @FXML
    private void abrir(ActionEvent e) throws Exception {
        String archivo = switch (((Button) e.getSource()).getText()) {
            case "Clientes" -> "admin/admin_clientes";
            case "Rangos" -> "admin/admin_rangos";
            case "Procesos" -> "admin/admin_procesos";
            case "Reportes" -> "cliente/reportes";
            default -> "";
        };
        if (archivo.isEmpty()) {
            refrescarInicio();
            raiz.setCenter(inicio);
            return;
        }
        raiz.setCenter(FXMLLoader.load(getClass().getResource("/vistas/" + archivo + ".fxml")));
    }

    @FXML
    private void cerrarSesion() throws Exception {
        Sesion.cerrar();
        Parent root = FXMLLoader.load(getClass().getResource("/vistas/login.fxml"));
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/estilos/estilos.css").toExternalForm());
        Stage stage = (Stage) raiz.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}