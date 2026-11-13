package co.edu.uniquindio.monedero.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    @FXML
    private void iniciarSesion() throws Exception {
        if (txtUsuario.getText().isBlank() || txtClave.getText().isBlank()) {
            lblMensaje.setText("Completa los campos");
            return;
        }
        boolean admin = txtUsuario.getText().trim().equalsIgnoreCase("admin");
        String vista = admin ? "/vistas/admin/dashboard_admin.fxml" : "/vistas/cliente/dashboard.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(vista));
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/estilos/estilos.css").toExternalForm());
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}